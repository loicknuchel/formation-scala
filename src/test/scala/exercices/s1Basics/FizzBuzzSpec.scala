package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class FizzBuzzSpec extends FunSpec with Matchers {
  describe("FizzBuzz") {
    import FizzBuzz._
    /**
      * Ecris les tests pour la fonction fizzBuzz ici.
      * Le fichier MainSpec.scala peut servir d'exemple pour la syntaxe d'un test.
      *
      * Voici quelques exemples d'utilisation :
      *   - fizzBuzz(1) => "1"
      *   - fizzBuzz(2) => "2"
      *   - fizzBuzz(3) => "Fizz"
      *   - fizzBuzz(4) => "4"
      *   - fizzBuzz(5) => "Buzz"
      *   - fizzBuzz(6) => "Fizz"
      *   - fizzBuzz(7) => "7"
      *   - fizzBuzz(8) => "8"
      *   - fizzBuzz(9) => "Fizz"
      *   - fizzBuzz(10) => "Buzz"
      *   - fizzBuzz(11) => "11"
      *   - fizzBuzz(12) => "Fizz"
      *   - fizzBuzz(15) => "FizzBuzz"
      *   - fizzBuzz(16) => "16"
      *   - fizzBuzz(20) => "Buzz"
      */

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
