package exercices

import org.scalatest.{FunSpec, Matchers}

class e1BasicsSpec extends FunSpec with Matchers {

  describe("FizzBuzz") {
    import e1Basics.FizzBuzz._
    it("should work") {
      fizzBuzz(-1) shouldBe "-1"
      fizzBuzz(0) shouldBe "FizzBuzz"
      fizzBuzz(1) shouldBe "1"
      fizzBuzz(2) shouldBe "2"
      fizzBuzz(3) shouldBe "Fizz"
      fizzBuzz(4) shouldBe "4"
      fizzBuzz(5) shouldBe "Buzz"
      fizzBuzz(6) shouldBe "Fizz"
      fizzBuzz(7) shouldBe "7"
      fizzBuzz(8) shouldBe "8"
      fizzBuzz(9) shouldBe "Fizz"
      fizzBuzz(10) shouldBe "Buzz"
      fizzBuzz(11) shouldBe "11"
      fizzBuzz(12) shouldBe "Fizz"
      fizzBuzz(13) shouldBe "13"
      fizzBuzz(14) shouldBe "14"
      fizzBuzz(15) shouldBe "FizzBuzz"
      fizzBuzz(16) shouldBe "16"
      fizzBuzz(17) shouldBe "17"
      fizzBuzz(18) shouldBe "Fizz"
      fizzBuzz(19) shouldBe "19"
      fizzBuzz(20) shouldBe "Buzz"
    }
  }

  describe("AverageAge") {
    import e1Basics.AverageAge._
    val employees = Seq(
      Employee("Jean", 41),
      Employee("Corinne", 49),
      Employee("Fanny", 36),
      Employee("Claude", 45),
      Employee("Cécile", 34)
    )
    val RnD = Team(employees.take(3))
    val sales = Team(Seq())

    it("should fail if empty list") {
      assertThrows[IllegalArgumentException] {
        averageAge(Seq(), 0, null)
      }
    }
    it("should fail if no employee are old enough") {
      assertThrows[IllegalArgumentException] {
        averageAge(employees, 55, null)
      }
    }
    it("should fail if team has no employee") {
      assertThrows[IllegalArgumentException] {
        averageAge(employees, 0, sales)
      }
    }
    it("should return the global average if age is low and team is null") {
      averageAge(employees, 0, null) shouldBe 41
    }
    it("should return the average of oldest employees") {
      averageAge(employees, 40, null) shouldBe 45
    }
    it("should return average age of people in team") {
      averageAge(employees, 0, RnD) shouldBe 42
    }
    it("should return average age of oldest people in team") {
      averageAge(employees, 45, RnD) shouldBe 49
    }
  }

  describe("Factoriel") {
    import e1Basics.Factoriel._
    describe("factoriel") {
      it("should return 1 when 1") {
        factoriel(1) shouldBe 1
      }
      it("should be correct for the firsts results") {
        factoriel(2) shouldBe 2
        factoriel(3) shouldBe 6
        factoriel(4) shouldBe 24
        factoriel(5) shouldBe 120
        factoriel(6) shouldBe 720
        factoriel(7) shouldBe 5040
        factoriel(8) shouldBe 40320
        factoriel(9) shouldBe 362880
      }
      it("should return 1 when < 1") {
        factoriel(0) shouldBe 1
        factoriel(-1) shouldBe 1
        factoriel(-678) shouldBe 1
      }
      it("should work for high numbers") {
        factoriel(16) shouldBe 2004189184
      }
      it("should throw for too high numbers") {
        assertThrows[IllegalArgumentException] {
          factoriel(20)
        }
      }
    }
    describe("factorielRec") {
      ignore("should exceed maximum call stack size") {
        val res = factorielRec(40) // why does it returns 0 instead of throw ???
        println("res: " + res)
        res shouldBe 1
      }
    }
  }

  describe("Kebab") {
    import e1Basics.Kebab._
    describe("isVegetarian") {
      it("should be vegetarian when at least one ingredient is vegetarian") {
        Kebab(Seq()).isVegetarian shouldBe true
        Kebab(Seq(Ingredient("lettuce", true, true))).isVegetarian shouldBe true
        Kebab(Seq(Ingredient("beef", false, false))).isVegetarian shouldBe false
        Kebab(Seq(Ingredient("lettuce", true, true), Ingredient("fish", false, true))).isVegetarian shouldBe false
      }
    }
    describe("isPescetarianism") {
      it("should be vegetarian when at least one ingredient is vegetarian") {
        Kebab(Seq()).isPescetarianism shouldBe true
        Kebab(Seq(Ingredient("lettuce", true, true))).isPescetarianism shouldBe true
        Kebab(Seq(Ingredient("beef", false, false))).isPescetarianism shouldBe false
        Kebab(Seq(Ingredient("lettuce", true, true), Ingredient("fish", false, true))).isPescetarianism shouldBe true
      }
    }
    describe("doubleCheddar") {
      it("should double cheddar without changing ingredient order") {
        Kebab(Seq()).doubleCheddar shouldBe Kebab(Seq())
        Kebab(Seq(Ingredient("lettuce", true, true))).doubleCheddar shouldBe Kebab(Seq(Ingredient("lettuce", true, true)))
        Kebab(Seq(Ingredient("cheddar", true, true))).doubleCheddar shouldBe Kebab(Seq(Ingredient("cheddar", true, true), Ingredient("cheddar", true, true)))
        Kebab(Seq(
          Ingredient("lettuce", true, true),
          Ingredient("cheddar", true, true),
          Ingredient("beef", false, false))
        ).doubleCheddar shouldBe Kebab(Seq(
          Ingredient("lettuce", true, true),
          Ingredient("cheddar", true, true),
          Ingredient("cheddar", true, true),
          Ingredient("beef", false, false)
        ))
      }
    }
    describe("removeOnion") {
      it("should remove onions without changing ingredient order") {
        Kebab(Seq()).removeOnion shouldBe Kebab(Seq())
        Kebab(Seq(Ingredient("lettuce", true, true))).removeOnion shouldBe Kebab(Seq(Ingredient("lettuce", true, true)))
        Kebab(Seq(Ingredient("onion", true, true))).removeOnion shouldBe Kebab(Seq())
        Kebab(Seq(
          Ingredient("lettuce", true, true),
          Ingredient("onion", true, true),
          Ingredient("beef", false, false))
        ).removeOnion shouldBe Kebab(Seq(
          Ingredient("lettuce", true, true),
          Ingredient("beef", false, false)
        ))
      }
    }
  }

  describe("Parenthesis") {
    import e1Basics.Parenthesis._
    describe("count") {
      it("should count non-closed parenthesis") {
        count("(") shouldBe 1
        count(")") shouldBe -1
        count("(())") shouldBe 0
        count(")(") shouldBe 0
      }
      it("should work with other chars") {
        count("(a,b,c))()") shouldBe -1
      }
    }
    describe("validate") {
      it("should be valid when all parenthesis are correctly closed") {
        validate("(())") shouldBe true
      }
      it("should not be valid when a parenthesis is not closed") {
        validate("(") shouldBe false
        validate("(()") shouldBe false
      }
      it("should not be valid when a parenthesis was not opened") {
        validate(")") shouldBe false
        validate(")(") shouldBe false
        validate("())") shouldBe false
      }
      it("should validate complex cases") {
        validate("()((())())") shouldBe true
      }
      it("should work with other chars") {
        validate("(1 + 3) * 2 + (6 / 2)") shouldBe true
        validate("(1 + 3 * 2 + (6 / 2)") shouldBe false
      }
    }
  }

  describe("ReversePolishNotation") {
    import e1Basics.ReversePolishNotation._
    it("should add") {
      eval("3 4 +") shouldBe 7
    }
    it("should multiply") {
      eval("3 4 *") shouldBe 12
    }
    it("should substract") {
      eval("3 4 -") shouldBe -1
    }
    it("should divide") {
      eval("4 2 /") shouldBe 2
    }
    it("should do several operations without parenthesis") {
      eval("3 4 - 5 +") shouldBe 4
    }
    it("should do several operations with priority") {
      eval("3 4 5 * +") shouldBe 23
    }
  }
}
