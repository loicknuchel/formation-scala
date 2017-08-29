package exercices.s1Basics

object AverageTemperature {

  case class City(name: String, coords: Coords, temperatures: Seq[Double])

  case class Coords(lat: Double, lng: Double)

  def format(data: Seq[City]): Seq[(Coords, Double)] = {
    var results = Seq.empty[(Coords, Double)]
    var totalTemp = 0d
    var averageTemp = 0d
    for (city <- data) {
      totalTemp = 0d
      for (temp <- city.temperatures) {
        totalTemp += temp
      }
      averageTemp = totalTemp / city.temperatures.length
      results = results :+ (city.coords, averageTemp)
    }
    results
  }

  def average(values: Seq[Double]): Double = {
    values.sum / values.length
  }

  def formatFun(cities: Seq[City]): Seq[(Coords, Double)] = {
    cities.map(city => (city.coords, average(city.temperatures)))
  }

  def formatFun2(cities: Seq[City]): Seq[(Coords, Double)] = {
    cities.map(_.coords).zip(cities.map(_.temperatures).map(average))
  }
}
