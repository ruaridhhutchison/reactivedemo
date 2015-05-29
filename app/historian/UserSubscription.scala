package historian

import akka.actor._
import play.mvc.WebSocket
import play.api.libs.json._
import conversions.WritesConversions._

// A UserSubscription actor instance is created for
// each WebSocket connection. The actor provides
// bi-directional communication with the client end-point.
class UserSubscription(out: ActorRef) extends Actor {
  
  // Import the supported messages
  import TagBroker._
  import TagManager._

  def receive = {
    case ShutDown =>
      context.stop(self)
    case TagData(pv) =>
      out ! Json.toJson(pv)
    case HistoryData(history) =>
      out ! Json.toJson(history)
    case json: JsValue =>
      val tag = (json \ "tag").as[String]
      val cmd = (json \ "command").as[String]
      if (cmd == "subscribe")
        TagBroker.broker ! Subscribe(tag.toInt, self)
      else if (cmd == "unsubscribe")
        TagBroker.broker ! Unsubscribe(tag.toInt, self)
    case _ =>
  }
}

// Companion object which creates a new UserSubscription 
// actor to handle each WebSocket session.
object UserSubscription {
  def props(out: ActorRef) = Props(new UserSubscription(out))
}
