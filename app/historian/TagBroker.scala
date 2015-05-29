package historian

import akka.actor._
import akka.actor.{Props, ActorRef, Actor}
import akka.actor.actorRef2Scala
import play.api.libs.concurrent.Akka

class TagBroker extends Actor {
  
  // Import the supported messages
  import TagBroker._
  import TagManager._
  
  def receive = {
    case subscribe @ Subscribe(tag, client) =>
      // Received from web sockets. Forward
      // to the appropriate TagManager.
      val tagManager = getOrCreateTagManager(tag)
      tagManager forward subscribe
    case unsubscribe @ Unsubscribe(tag, client) =>
      // Received from web sockets. Forward
      // to the appropriate TagManager.
      val tagManager = getOrCreateTagManager(tag)
      tagManager forward unsubscribe
    case history @ History(tag, client) =>
      val tagManager = getOrCreateTagManager(tag)
      tagManager forward history
    case latest @ Latest(tag, client) =>
      val tagManager = getOrCreateTagManager(tag)
      tagManager forward latest
    case dispatch @ Dispatch(pv) =>
      // Received from the data historian. Forward
      // to the appropriate TagManager.
      val tagManager = getOrCreateTagManager(pv.tag)
      tagManager forward dispatch   
    case ShutDown =>
      // Received from the data historian.
      context.stop(self)
      context.children foreach { child =>
        context.unwatch(child)
        context.stop(child)
      }
    case _ => 
  }

  def getOrCreateTagManager(tag: Int): ActorRef = {
    context.child(tag.toString) match {
      case Some(child) => child
      case None => context.actorOf(Props(classOf[TagManager], tag), tag.toString)
    }
  }
}

object TagBroker { 
  // Supported messages
  case class Dispatch(pv: PointValue)
  case class ShutDown()
  
  private val akka = ActorSystem("application")
  lazy val broker: ActorRef = akka.actorOf(Props(classOf[TagBroker]))
}
