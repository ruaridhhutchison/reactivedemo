import play.api._
import akka.actor._
import akka.stream.actor._
import scala.concurrent.Future
import historian._

object Global extends GlobalSettings {
  
  override def onStart(app: Application) {
    Logger.info("Application has started")
    Future.successful(startHistorian)
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown")
  }

  def startHistorian {
    
    val akka = ActorSystem("application")

    Logger.info("Starting historian")
    val historianActor = akka.actorOf(Props[DataHistorian])
    val historian = ActorPublisher[DataHistorian](historianActor)
    
    Logger.info("Starting historian subscription")
    val subscriberActor = akka.actorOf(Props(new DataHistorianSubscriber(TagBroker.broker)))
    val subscriber = ActorSubscriber[DataHistorian](subscriberActor)
    historian.subscribe(subscriber)
    
  }
}