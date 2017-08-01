package helpers

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Future

class FutureUtilsSpec extends FunSpec with Matchers with ScalaFutures {
  implicit val defaultPatience = PatienceConfig(timeout = Span(5, Seconds), interval = Span(500, Millis))

  describe("execPar") {
    it("should run all futures and return their results") {
      val f = FutureUtils.execPar[Int, Int](Seq(1, 2), n => Future.successful(n * 2))
      whenReady(f) { result =>
        result shouldBe Seq(2, 4)
      }
    }

    it("should return the first failure") {
      val f = FutureUtils.execPar[Int, Int](Seq(1, 2, 3, 4), n => {
        if (n % 2 == 0) Future.failed(new Exception(s"$n is even"))
        else Future.successful(n * 2)
      })
      whenReady(f.failed) { err =>
        err.getMessage shouldBe "2 is even"
      }
    }

    it("should run all the futures, even if some fails") {
      var cpt = 0
      val f = FutureUtils.execPar[Int, Int](Seq(1, 2, 3, 4), n => {
        cpt = cpt + 1
        if (n % 2 == 0) Future.failed(new Exception(s"$n is even"))
        else Future.successful(n * 2)
      })
      whenReady(f.failed) { err =>
        err.getMessage shouldBe "2 is even"
        cpt shouldBe 4
      }
    }
  }

  describe("execSeq") {
    it("should run all futures and return their results") {
      val f = FutureUtils.execSeq[Int, Int](Seq(1, 2), n => Future.successful(n * 2))
      whenReady(f) { result =>
        result shouldBe Seq(2, 4)
      }
    }

    it("should return the first failure") {
      val f = FutureUtils.execSeq[Int, Int](Seq(1, 2, 3, 4), n => {
        if (n % 2 == 0) Future.failed(new Exception(s"$n is even"))
        else Future.successful(n * 2)
      })
      whenReady(f.failed) { err =>
        err.getMessage shouldBe "2 is even"
      }
    }

    it("should run all the futures, even if some fails") {
      var cpt = 0
      val f = FutureUtils.execSeq[Int, Int](Seq(1, 2, 3, 4), n => {
        cpt = cpt + 1
        if (n % 2 == 0) Future.failed(new Exception(s"$n is even"))
        else Future.successful(n * 2)
      })
      whenReady(f.failed) { err =>
        err.getMessage shouldBe "2 is even"
        cpt shouldBe 2
      }
    }

    it("should work for long collections") {
      val f = FutureUtils.execSeq[Int, Int](0 to 10000, n => Future.successful(n * 2))
      whenReady(f) { result =>
        result shouldBe (0 to 20000 by 2)
      }
    }
  }
}
