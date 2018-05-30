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
    val defibs = lines.map(parseDefib).collect { case Success(defib) => defib }
    getClosestDefib(pos, defibs).map(_.name)
  }

  def getClosestDefib(pos: Point, defibs: Seq[Defib]): Try[Defib] = {
    if(defibs.isEmpty) {
      Failure(new Exception("no defibs"))
    } else {
      Success(defibs.minBy(d => distance(d.pos, pos)))
    }
  }

  def distance(a: Point, b: Point): Double = {
    val x = (b.lon - a.lon) * math.cos((a.lat + b.lat) / 2)
    val y = b.lat - a.lat
    math.sqrt(x * x + y * y) * 6371
  }

  def parseDefib(str: String): Try[Defib] = {
    str.split(";") match {
      case Array(idStr, name, adress, phone, lonStr, latStr) =>
        for {
          id <- Try(idStr.toInt)
          lon <- Try(lonStr.replace(',', '.').toDouble)
          lat <- Try(latStr.replace(',', '.').toDouble)
        } yield Defib(id, name, adress, phone, Point(lon, lat))
      case _ =>
        Failure(new Exception("didn't match"))
    }
  }

  private def parseDouble(value: String): Try[Double] =
    Try(value.replace(',', '.').toDouble)
}
