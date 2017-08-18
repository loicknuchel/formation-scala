package exercices.s3FunctionalTypes

import org.scalatest.{FunSpec, Matchers}

class ValidationMonadSpec extends FunSpec with Matchers {
  describe("ValidationMonad") {
    import ValidationMonad._
    it("should tell if valid") {
      Valid(1).isValid shouldBe true
      Invalid().isValid shouldBe false
    }
    it("should extract the value") {
      Valid(1).get shouldBe 1
      assertThrows[NoSuchElementException] {
        Invalid().get
      }
    }
    it("should extract the value with default") {
      Valid(1).getOrElse(2) shouldBe 1
      Invalid().getOrElse(2) shouldBe 2
    }
    it("should transform the value") {
      Valid(1).map(_.toString) shouldBe Valid("1")
      Invalid[Int]().map(_.toString) shouldBe Invalid()
    }
    it("should flatMap the value") {
      Valid(1).flatMap(n => Valid(n + 1)) shouldBe Valid(2)
      Valid(1).flatMap(_ => Invalid()) shouldBe Invalid()
      Invalid[Int]().flatMap(n => Valid(n + 1)) shouldBe Invalid()
      Invalid[Int]().flatMap(_ => Invalid()) shouldBe Invalid()
    }
    it("should succeed when all validations are valid") {
      val map = Map(
        "a" -> 1,
        "b" -> 2,
        "c" -> 3,
        "d" -> 4)
      val result: Validation[Int] = for {
        a <- Validation(map("a"))
        b <- Validation(map("b"))
        c <- Validation(map("c"))
        d <- Validation(map("d"))
      } yield a + b + c + d
      result shouldBe Valid(10)
    }
    it("should return the error if exists") {
      val map = Map(
        "a" -> 1,
        "b" -> 2,
        "d" -> 4)
      val result: Validation[Int] = for {
        a <- Validation(map("a"))
        b <- Validation(map("b"))
        c <- Validation(map("c"))
        d <- Validation(map("d"))
      } yield a + b + c + d
      result shouldBe Invalid("key not found: c")
    }
    it("should be able to hold custom error message") {
      val map = Map(
        "a" -> 1,
        "b" -> 2,
        "d" -> 4)
      val result: Validation[Int] = for {
        a <- Validation(map("a"), "key 'a' is missing")
        b <- Validation(map("b"), "key 'b' is missing")
        c <- Validation(map("c"), "key 'c' is missing")
        d <- Validation(map("d"), "key 'd' is missing")
      } yield a + b + c + d
      result shouldBe Invalid("key 'c' is missing")
    }
    it("should return all errors") {
      val map = Map(
        "a" -> 1,
        "d" -> 4)
      val result: Validation[Int] = for {
        a <- Validation(map("a"))
        b <- Validation(map("b"))
        c <- Validation(map("c"))
        d <- Validation(map("d"))
      } yield a + b + c + d
      result shouldBe Invalid("key not found: b", "key not found: c")
    }
    it("should not return null errors") {
      val map = Map(
        "a" -> "a",
        "c" -> "c",
        "d" -> "d")
      val result: Validation[String] = for {
        a <- Validation(map("a"))
        b <- Validation(map("b"))
        c <- Validation(map(b.toString))
        d <- Validation(map("d"))
      } yield a + b + c + d
      result shouldBe Invalid("key not found: b")
    }
  }
}
