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
    def fizzBuzz(i: Int): String = {
      (i % 3 == 0, i % 5 == 0) match {
        case (true, true) => "FizzBuzz"
        case (_, true) => "Buzz"
        case (true, _) => "Fizz"
        case _ => i.toString
      }
      /*if (i % 15 == 0) "FizzBuzz"
      else if (i % 3 == 0) "Fizz"
      else if (i % 5 == 0) "Buzz"
      else i.toString*/
    }

    val numValues = Seq(1, 2, 4, 7)

    it(s"should return num value") {
      numValues.foreach { i =>
        fizzBuzz(i) shouldBe i.toString
      }
    }
    it("should return Fizz on 3") {
      fizzBuzz(3) shouldBe "Fizz"
    }
    it("should return Fizz on 6") {
      fizzBuzz(6) shouldBe "Fizz"
    }
    it("should return Buzz on 5") {
      fizzBuzz(5) shouldBe "Buzz"
    }
    it("should return FizzBuzz on 15") {
      fizzBuzz(15) shouldBe "FizzBuzz"
    }
  }
}
