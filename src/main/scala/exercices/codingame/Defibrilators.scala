package exercices.codingame

import scala.io.StdIn._

object DefibrilatorsSafe {

  import scala.util._

  /**
    * Auto-generated code below aims at helping you parse
    * the standard input according to the problem statement.
    **/
  object Solution extends App {

    case class Point(lon: Double, lat: Double)

    case class Defib(id: Int, name: String, address: String, pos: Point)

    object Defib {
      def parse(line: String): Try[Defib] = {
        line.split(";") match {
          case Array(id, name, address, _, lat, lon) =>
            for {
              intIt <- Try(id.toInt)
              latDouble <- doubleFromFrenchString(lat)
              lonDouble <- doubleFromFrenchString(lon)
            } yield
              Defib(
                id = intIt,
                name = name,
                address = address,
                pos = Point(latDouble, lonDouble))
          case _ =>
            Failure(new Exception("bad array format"))
        }
      }
    }

    def doubleFromFrenchString(s: String): Try[Double] = {
      Try(s.replace(',', '.').toDouble)
    }

    def distance(a: Point, b: Point): Double = {
      val x = (b.lon - a.lon) * math.cos((a.lat + b.lat) / 2)
      val y = b.lat - a.lat
      (x * x) + (y * y)
    }

    for {
      lon <- doubleFromFrenchString(readLine)
      lat <- doubleFromFrenchString(readLine)
      n <- Try(readInt)
    } yield {
      val pos = Point(lon, lat)
      val defibs = (0 until n)
        .map(_ => readLine)
        .map(Defib.parse)
        .collect { case Success(defib) => defib }
      Try(defibs.minBy(d => distance(pos, d.pos)))
        .map(_.name)
        .foreach(println)
    }
  }

}


object Defibrilators1 {

  import scala.util._

  /**
    * Auto-generated code below aims at helping you parse
    * the standard input according to the problem statement.
    **/
  object Solution extends App {

    case class Point(lon: Double, lat: Double)

    case class Defib(id: Int, name: String, address: String, pos: Point)

    object Defib {
      def parse(line: String): Defib = {
        val Array(id, name, address, _, lat, lon) = line.split(";")
        Defib(
          id.toInt,
          name,
          address,
          Point(lat.replace(',', '.').toDouble, lon.replace(',', '.').toDouble)
        )
      }
    }

    def distance(a: Point, b: Point): Double = {
      val x = (b.lon - a.lon) * math.cos((a.lat + b.lat) / 2)
      val y = b.lat - a.lat
      (x * x) + (y * y)
    }

    val lon = readLine.replace(',', '.').toDouble
    val lat = readLine.replace(',', '.').toDouble
    val pos = Point(lon, lat)
    val n = readInt
    val defibs = (0 until n).map(_ => readLine).map(Defib.parse)
    Try(defibs.minBy(d => distance(pos, d.pos))).map(_.name).foreach(println)
  }
}