package corrections

import org.scalatest.{FunSpec, Matchers}

class Correction20171204 extends FunSpec with Matchers {
  describe("FizzBuzz") {
    def fizzbuzz(i: Int): String = {
      i match {
        case i if i % 15 == 0 => "FizzBuzz"
        case i if i % 3 == 0 => "Fizz"
        case i if i % 5 == 0 => "Buzz"
        case _ => i.toString
      }
    }

    it("should return correct value") {
      fizzbuzz(1) shouldBe "1"
      fizzbuzz(3) shouldBe "Fizz"
      fizzbuzz(5) shouldBe "Buzz"
      fizzbuzz(30) shouldBe "FizzBuzz"
    }
  }

  describe("AverageAge") {
    import exercices.s1Basics.AverageAge.{Employee, Team}

    def meanAge(employees: Seq[Employee], minAge: Int = 0, team: Team = null): Int = {
      var total = 0
      var count = 0
      for (e <- employees) {
        if (e.age > minAge && (team == null || team.has(e))) {
          total += e.age
          count += 1
        }
      }
      total / count
    }

    val employees: Seq[Employee] = Seq(
      Employee("Jean", 22),
      Employee("Corinne", 54),
      Employee("Fanny", 32),
      Employee("Claude", 40),
      Employee("Cécile", 25))
    val RnD: Team = Team(employees.take(3))

    it("should compute mean") {
      meanAge(employees) shouldBe 34
    }
    it("should compute mean with min age") {
      meanAge(employees, 30) shouldBe 42
    }
    it("should compute mean with min age and team") {
      meanAge(employees, 30, RnD) shouldBe 43
    }
  }
}
