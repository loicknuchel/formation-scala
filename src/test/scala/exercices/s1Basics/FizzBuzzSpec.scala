package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class FizzBuzzSpec extends FunSpec with Matchers {

  describe("FizzBuzz") {
    import FizzBuzz._
    it("should return 2 when 2") {
      fizzBuzz(2) shouldBe "2"
    }
    it("should return Fizz when 3") {
      fizzBuzz(3) shouldBe "Fizz"
    }
    it("should return Fizz when 6") {
      fizzBuzz(6) shouldBe "Fizz"
    }
    it("should return Buzz when 5") {
      fizzBuzz(5) shouldBe "Buzz"
    }
    it("should return FizzBuzz when 15") {
      fizzBuzz(15) shouldBe "FizzBuzz"
    }
  }
}
