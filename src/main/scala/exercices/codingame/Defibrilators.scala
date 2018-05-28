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
    closestDefib(pos, defibs).map(_.name)
  }

  private def closestDefib(pos: Point, defibs: Seq[Defib]): Try[Defib] = {
    if (defibs.isEmpty) {
      Failure(new Exception("Can't find nearest defib on empty list"))
    } else {
      Success(defibs.minBy(d => distance(pos, d.pos)))
    }
  }

  private def distance(a: Point, b: Point): Double = {
    val x = (b.lon - a.lon) * math.cos((a.lat + b.lat) / 2)
    val y = b.lat - a.lat
    (x * x) + (y * y)
  }

  private def parseDefib(line: String): Try[Defib] = {
    line.split(";") match {
      case Array(idStr, name, address, phone, lonStr, latStr) =>
        for {
          id <- Try(idStr.toInt)
          lon <- parseDouble(lonStr)
          lat <- parseDouble(latStr)
        } yield Defib(id, name, address, phone, Point(lon, lat))
      case _ => Failure(new Exception("Unable to parse line"))
    }
  }

  private def parseDouble(value: String): Try[Double] =
    Try(value.replace(',', '.').toDouble)
}
