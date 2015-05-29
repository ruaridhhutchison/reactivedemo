package conversions

import historian._
import play.api.libs.json._

object WritesConversions {
  
  implicit val pointValueWrites = new Writes[PointValue] {
    def writes(pv: PointValue) = Json.obj(
        "tag" -> pv.tag,
        "value" -> pv.value,
        "time" -> pv.time)
  }
  
}
