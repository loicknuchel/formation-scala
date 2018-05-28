package exercices.codingame

import scala.util.{Failure, Success, Try}

// from https://www.codingame.com/ide/puzzle/defibrillators
object Defibrilators {

  case class Point(lon: Double, lat: Double)

  case class Defib(id: Int, name: String, address: String, phone: String, pos: Point)

  def main(args: Array[String]): Unit = {
    for {
      lon <- parseDouble(readLine)
      lat <- parseDouble(readLine)
      n <- Try(readInt)
    } yield {
      val lines = (0 until n).map(_ => readLine)
      getClosestDefibName(Point(lon, lat), lines).foreach(println)
    }
  }

  def getClosestDefibName(pos: Point, lines: Seq[String]): Try[String] = {
    ???
  }

  private def parseDouble(value: String): Try[Double] =
    Try(value.replace(',', '.').toDouble)
}
