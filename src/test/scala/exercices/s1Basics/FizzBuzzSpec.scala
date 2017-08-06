package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class FizzBuzzSpec extends FunSpec with Matchers {
  describe("FizzBuzz") {
    import FizzBuzz._
    it("should return 1 when 1") {
      fizzbuzz(1) shouldBe "1"
    }
    it("should return Fizz when 3") {
      fizzbuzz(3) shouldBe "Fizz"
    }
    it("should return Buzz when 5") {
      fizzbuzz(5) shouldBe "Buzz"
    }
    it("should return FizzBuzz when 15") {
      fizzbuzz(15) shouldBe "FizzBuzz"
    }
    it("should return FizzBuzz when divisible by 15") {
      fizzbuzz((math.random() * 1000).toInt * 15) shouldBe "FizzBuzz"
    }
  }
}
