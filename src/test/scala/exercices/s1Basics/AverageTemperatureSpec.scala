package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class AverageTemperatureSpec extends FunSpec with Matchers {
  describe("TemperatureMap") {
    import AverageTemperature._
    it("should format cities") {
      format(cities) shouldBe results
    }
  }
}
