function mainController() {
  this.userName = 'Ruaridh Hutchison';
};

function wellDataController($scope, $stateParams, $http, $q, wsFactory) {

  // Set the chart options
  $scope.chartOptions = chartOptions()

  // Retrieve the well info
  $http.get('/api/wells/' + $stateParams.id)
  .success(function(data) {
    $scope.well = data;
  }).then(function() {

    // Retrieve the historical data
    var oilPromise = $http.get('/api/tags/' + $scope.well['oilTag'] + '/history')
    var gasPromise = $http.get('/api/tags/' + $scope.well['gasTag'] + '/history')
    var wtrPromise = $http.get('/api/tags/' + $scope.well['wtrTag'] + '/history')

    $q.all([oilPromise, gasPromise, wtrPromise]).then(function(result) {
      var oilHistory = toChartArray(result[0].data, 1)
      var gasHistory = toChartArray(result[1].data, 1)
      var wtrHistory = toChartArray(result[2].data, 4)

      // Retrieve and bind to the latest available data
      $scope.oil = latestValue(result[0].data)
      $scope.gas = latestValue(result[1].data)
      $scope.wtr = latestValue(result[2].data)
      $scope.chartData = [{ data: oilHistory, label: "Oil Production (bbl)"},
                          { data: wtrHistory, label: "Water Production (bbl)", yaxis: 1}];

      // Unsubscribe from anything prior to this
      wsFactory.unsubscribeAll()

      // Receive new oil/gas/water data
      wsFactory.subscribe($scope.well['oilTag'], function(msg) {
        $scope.oil = {tag: msg.tag, value: msg.value}
        updateHistory(oilHistory, msg)
      })
      wsFactory.subscribe($scope.well['gasTag'], function(msg) {
        $scope.gas = {tag: msg.tag, value: msg.value}
        updateHistory(gasHistory, msg)
      })
      wsFactory.subscribe($scope.well['wtrTag'], function(msg) {
        $scope.wtr = {tag: msg.tag, value: msg.value}
        updateHistory(wtrHistory, msg)
      })
    });
  });
};

// Return the last value from a history array
function latestValue(arr) {
  var r = arr[arr.length-1]
  return {tag: r.tag, value: r.value}
}

// Add a data point to a history array
function updateHistory(hist, msg) {
  if (hist.length == 100) hist.shift()
  hist.push([msg.time, msg.value])
}

function chartOptions() {
  var position = 'right';
  return {
    xaxes: [{mode: 'time'}],
    yaxes: [{min: 0},{alignTicksWithAxis: position == "right" ? 1 : null, position: position}],
    legend: {position: 'sw'},
    colors: ["#ff0000","#00ff00"],
    grid: {color: "#999999", hoverable: true, clickable: true, tickColor: "#D4D4D4", borderWidth: 0, hoverable: true},
    tooltip: true,
    tooltipOpts: {content: "%s: %y", xDateFormat: "%y-%m-%d", onHover: function (flotItem, $tooltipEl) {}}
  }
}

function toChartArray(data, scale) {
  var result = [];
  for (i = 0; i < data.length; i++) {
    var obj = data[i];
    var value = Math.round(obj.value / scale * 100) / 100
    result.push([obj.time, value]);
  }
  return result;
}

function webSocketFactory($websocket, $location) {
  var protocol = $location.port() == 80 ? 'wss://' : 'ws://'
  var ws = $websocket(protocol + $location.host() + ':' + $location.port().toString() + '/ws/connect')
  var subscriptions = {}

  var subscr = function(tag, callBack) {
  if (!(tag in subscriptions)) {
    console.log('ws subscribe ' + tag)
    subscriptions[tag] = callBack
    ws.send(JSON.stringify({tag: tag.toString(), command: 'subscribe'}))
    }
  }

  var unsubscr = function(tag) {
    if (tag in subscriptions) {
      console.log('ws unsubscribe ' + tag)
      delete subscriptions[tag]
      ws.send(JSON.stringify({tag: tag.toString(), command: 'unsubscribe'}))
    }
  }

  var unsubscrAll = function() {
  for (tag in subscriptions)
    unsubscr(tag)
  }

  ws.onClose(function() {
  console.log('closing')
    unsubscrAll()
  });

  ws.onMessage(function(message) {
    var msg = JSON.parse(message.data)
    if (msg.tag in subscriptions) {
      console.log('ws message ' + msg.tag + ' ' + msg.value)
      var callBack = subscriptions[msg.tag]
      callBack(msg)
    }
  });

  return {
    subscribe: function(tag, callBack) {
      subscr(tag, callBack)
    },
    unsubscribe: function(tag) {
      unsubscr(tag)
    },
    unsubscribeAll: function() {
      unsubscrAll()
    },
    status: function() {
      return ws.readyState
    },
    close: function() {
      ws.close()
    }
  }
}

angular
  .module('ProdViewApp')
  .service('wsFactory', webSocketFactory)
  .controller('mainController', mainController)
  .controller('wellDataController', ['$scope','$stateParams', '$http', '$q', 'wsFactory', wellDataController]);