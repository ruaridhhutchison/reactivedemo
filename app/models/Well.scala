package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Well(id: Int, name: String, asset: String, oilTag: Int, wtrTag: Int, gasTag: Int)

object Well {
  
  implicit val wellWrites: Writes[Well] = (
    (JsPath \ "id").write[Int] and
    (JsPath \ "name").write[String] and
    (JsPath \ "asset").write[String] and
    (JsPath \ "oilTag").write[Int] and
    (JsPath \ "wtrTag").write[Int] and
    (JsPath \ "gasTag").write[Int]
  )(unlift(Well.unapply))

  var list : List[Well] = {
    List(
      Well(1, "Adams Tipton Unit 1H", "Kenedy", 1, 2, 3),
      Well(2, "Bailey Retzloff Unit 1H", "Kenedy", 4, 5, 6),
      Well(3, "Boris 1H", "Kenedy", 7, 8, 9),
      Well(4, "Carter Unit 1H", "Kenedy", 10, 11, 12),
      Well(5, "Challenger Unit 10H", "Kenedy", 13, 14, 15),
      Well(6, "Challenger Unit 11H", "Kenedy", 16, 17, 18),
      Well(7, "Davenport Unit 1H", "Kenedy", 19, 20, 21),
      Well(8, "Easley Unit 1H", "Kenedy", 22, 23, 24),
      Well(9, "Felix Unit 1H", "Kenedy", 25, 26, 27),
      Well(10, "Ann Friar Thomas 1H", "Gonzales", 28, 29, 30),
      Well(11, "Barnhart 1H", "Gonzales", 31, 32, 33),
      Well(12, "Colman 1H", "Gonzales", 34, 35, 36),
      Well(13, "Colman 2H", "Gonzales", 37, 38, 39),
      Well(14, "Dubosse 1H", "Gonzales", 40, 41, 42),
      Well(15, "Henderson 10H", "Gonzales", 43, 44, 45),
      Well(16, "Manning 1H", "Gonzales", 46, 47, 48),
      Well(17, "Robinson 1H", "Gonzales", 49, 50, 51),
      Well(18, "Ward E 1H", "Gonzales", 52, 53, 54),
      Well(19, "Ward F 2H", "Gonzales", 55, 56, 57),
      Well(20, "August Witte 1H", "Pleasanton", 58, 59, 60),
      Well(21, "Beck Unit 1H", "Pleasanton",61, 62, 63),
      Well(22, "Chapman Unit 1H", "Pleasanton", 64, 65, 66),
      Well(23, "Day-Lyssy Unit 1H", "Pleasanton", 67, 68, 69),
      Well(24, "Esse 1H", "Pleasanton", 70, 71, 72),
      Well(25, "George Long 1H", "Pleasanton", 73, 74, 75),
      Well(26, "Hamilton 1H", "Pleasanton", 76, 77, 78),
      Well(27, "Hamilton 2H", "Pleasanton", 79, 80, 81),
      Well(28, "Keller 1H", "Pleasanton", 82, 83, 84),
      Well(29, "May Girls 1H", "Pleasanton", 85, 86, 87),
      Well(30, "May Girls 2H", "Pleasanton", 88, 89, 90),
      Well(31, "Smith East 1H", "Pleasanton", 91, 92, 93),
      Well(32, "Smith West 1H", "Pleasanton", 94, 95, 96),
      Well(33, "Wright Trust 1H", "Pleasanton", 97, 98, 99)
    )
  }
}