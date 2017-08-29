package exercices.s1Basics

object AverageTemperature {

  case class City(name: String, coords: Coords, temperatures: Seq[Double])

  case class Coords(lat: Double, lng: Double)

}
