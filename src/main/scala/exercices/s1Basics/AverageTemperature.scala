package exercices.s1Basics

object AverageTemperature {

  case class City(name: String, coords: Coords, temperatures: Seq[Double])

  case class Coords(lat: Double, lng: Double)

  val cities = Seq(
    City("Paris", Coords(48.856614, 2.352222), Seq(5, 6, 9, 11, 15, 16, 20, 20, 16, 12, 7, 5)),
    City("Marseille", Coords(43.296482, 5.36978), Seq(7, 8, 11, 14, 18, 21, 24, 24, 21, 17, 11, 8)),
    City("Lyon", Coords(45.764043, 4.835659), Seq(3, 4, 8, 11, 16, 18, 22, 21, 18, 13, 7, 5))
  )
  val results: Seq[(Coords, Double)] = Seq(
    (Coords(48.856614, 2.352222), 11.833333333333334),
    (Coords(43.296482, 5.36978), 15.333333333333334),
    (Coords(45.764043, 4.835659), 12.166666666666666)
  )

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
