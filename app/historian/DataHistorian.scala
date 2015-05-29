package historian

import akka.actor._
import akka.stream.actor._
import akka.stream.actor.ActorPublisherMessage._

case class PointValue(tag: Int, value: Double, time: Long)

class DataHistorian extends ActorPublisher[PointValue] with ActorLogging {
  private val rnd = new java.util.Random()
  private var currentTime = 1220911200000L
  
  def receive = {
    case Request(count) =>
      startSendingValues
    case Cancel =>
      context.stop(self)
    case _ =>
  }
  
  def startSendingValues {
    while(isActive && totalDemand > 0) {                                             
      onNext(nextValue)
      // Not something you would ever do
      // in a real system...
      Thread.sleep(100)      
    }
  }

  def nextValue : PointValue = { 
    val tag = rnd.nextInt(98) + 1
    val value = rnd.nextInt(100)
    val pv = PointValue(tag, value, currentTime)
    currentTime += 100
    pv
  }
}