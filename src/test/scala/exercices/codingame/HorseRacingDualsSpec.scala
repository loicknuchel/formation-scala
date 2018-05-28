package exercices.codingame

import org.scalatest.{FunSpec, Matchers}

import scala.util.{Failure, Success}

// from https://www.codingame.com/ide/puzzle/horse-racing-duals
class HorseRacingDualsSpec extends FunSpec with Matchers {
  describe("HorseRacingDuals") {
    import HorseRacingDuals._
    describe("getNearestPowerDiff") {
      it("should work on simple case") {
        getNearestPowerDiff(Seq(3, 5, 8, 9)) shouldBe Success(1)
      }
      it("should work with unordered horses") {
        getNearestPowerDiff(Seq(10, 5, 15, 17, 3, 8, 11, 28, 6, 55, 7)) shouldBe Success(1)
      }
      it("should fail if less than two horses") {
        getNearestPowerDiff(Seq()) shouldBe a[Failure[_]]
      }
    }
  }
}
