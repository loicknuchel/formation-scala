package utils

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}
import utils.MonadUtils._

import scala.util.{Failure, Success, Try}

class MonadUtilsSpec extends FunSpec with Matchers with ScalaFutures {
  val exception = new Exception("fail")

  describe("OptionExtension") {
    it("should allow to convert Option to Try") {
      Some(1).toTry(exception) shouldBe Success(1)
      None.toTry(exception) shouldBe Failure(exception)
    }
    it("should allow to convert Option to Future") {
      whenReady(Some(1).toFuture(exception)) { result =>
        result shouldBe 1
      }
      whenReady(None.toFuture(exception).failed) { result =>
        result shouldBe exception
      }
    }
    it("should allow to convert Option to Either") {
      Some(1).toEither("err") shouldBe Right(1)
      None.toEither("err") shouldBe Left("err")
    }
  }

  describe("TryExtension") {
    it("should allow to convert Try to Future") {
      whenReady(Try(1).toFuture) { result =>
        result shouldBe 1
      }
      whenReady(Failure(exception).toFuture.failed) { result =>
        result shouldBe exception
      }
    }
    it("should allow to convert Try to Either") {
      Try(1).toEither shouldBe Right(1)
      Failure(exception).toEither shouldBe Left(exception)
    }
    /*it("should allow to convert Try to custom Either") {
      Try(1).toEither(_.getMessage) shouldBe Right(1)
      Failure(exception).toEither(_.getMessage) shouldBe Left("fail")
    }*/
  }

  describe("EitherSeqExtension") {
    it("should allow to convert Either to Future") {
      whenReady(Right(1).toFuture) { result =>
        result shouldBe 1
      }
      whenReady(Left(Seq("fail")).toFuture.failed) { result =>
        result.getMessage shouldBe "fail"
      }
    }
    it("should get value from Either") {
      Right(1).get shouldBe 1
      assertThrows[NoSuchElementException] {
        Left(Seq("fail")).get
      }
    }
  }
}
