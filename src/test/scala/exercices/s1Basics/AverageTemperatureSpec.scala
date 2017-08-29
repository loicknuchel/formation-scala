package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class AverageTemperatureSpec extends FunSpec with Matchers {
  describe("TemperatureMap") {
    import AverageTemperature._

    val cities: Seq[City] = Seq(
      City("Paris", Coords(48.8566, 2.3522), Seq(5, 6, 9, 11, 15, 16, 20, 20, 16, 12, 7, 5)),
      City("Marseille", Coords(43.2964, 5.3697), Seq(7, 8, 11, 14, 18, 21, 24, 24, 21, 17, 11, 8)),
      City("Lyon", Coords(45.7640, 4.8356), Seq(3, 4, 8, 11, 16, 18, 22, 21, 18, 13, 7, 5)))
    val results: Seq[(Coords, Double)] = Seq(
      (Coords(48.8566, 2.3522), 11.833333333333334),
      (Coords(43.2964, 5.3697), 15.333333333333334),
      (Coords(45.7640, 4.8356), 12.166666666666666))

    it("should format cities") {
      format(cities) shouldBe results
    }
  }
}
