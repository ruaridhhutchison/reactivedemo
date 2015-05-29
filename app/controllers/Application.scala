package controllers

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import play.api._
import play.api.mvc._
import play.api.libs.json._
import play.api.libs.concurrent.Akka
import play.api.Play.current
import play.server._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits._
import conversions.WritesConversions._
import historian._
import models._

object Application extends Controller {
  
  import TagManager._
  
  def index = Action {
    Ok(views.html.index())
  }

  def listWells = Action {
    val json = Json.toJson(Well.list)
    Ok(json)
  }
  
  def getWell(id: Int) = Action {
    val json = Json.toJson(Well.list.find({ x => x.id == id }))
    Ok(json)
  }
  
  def getTagHistory(tag: Int) = Action async {
    val p = Promise[String]
    val replyTo = Akka.system.actorOf(Props(new Actor {
       def receive = {
        case HistoryData(history) =>
           p.success(Json.toJson(history).toString)
           context.stop(self)
      }
    }))
    
    // Request the history
    TagBroker.broker ! History(tag, replyTo)
    
    // Transform the response to a Play result
    p.future.map(response => Ok(response))
  }
  
  def getTagLatest(tag: Int) = Action async {
    val p = Promise[String]
    val replyTo = Akka.system.actorOf(Props(new Actor {
      def receive = {
        case TagData(pv) =>
           p.success(Json.toJson(pv).toString)
           context.stop(self)
      }
    }))
    
    // Request the history
    TagBroker.broker ! Latest(tag, replyTo)
    
    // Transform the response to a Play result
    p.future.map(response => Ok(response))
  }

  def connect = WebSocket.acceptWithActor[JsValue, JsValue] { 
    request => out => UserSubscription.props(out)
  }

}