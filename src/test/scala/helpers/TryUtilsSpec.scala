package helpers

import org.scalatest.{FunSpec, Matchers}

import scala.util.{Failure, Success}

class TryUtilsSpec extends FunSpec with Matchers {
  describe("sequence") {
    it("should success when all success") {
      TryUtils.sequence(Seq(Success(1), Success(2))) shouldBe Success(Seq(1, 2))
    }

    it("should fail if one is failed") {
      val err = new Exception("fail")
      TryUtils.sequence(Seq(Success(1), Failure(err))) shouldBe Failure(err)
    }

    it("should keep only the first failure") {
      val err1 = new Exception("fail1")
      val err2 = new Exception("fail2")
      TryUtils.sequence(Seq(Success(1), Failure(err1), Failure(err2))) shouldBe Failure(err1)
    }
  }
}
