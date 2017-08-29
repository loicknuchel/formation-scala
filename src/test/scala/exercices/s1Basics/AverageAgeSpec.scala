package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class AverageAgeSpec extends FunSpec with Matchers {
  describe("AverageAge") {
    import AverageAge._

    val employees: Seq[Employee] = Seq(
      Employee("Jean", 22),
      Employee("Corinne", 54),
      Employee("Fanny", 32),
      Employee("Claude", 40),
      Employee("Cécile", 25))
    val RnD: Team = Team(employees.take(3))

    /**
      * Ecris les tests pour la fonction averageAge, voici quelques exemples :
      *   - averageAge(employees) => 34.6
      *   - averageAge(employees, 25) => 42
      *   - averageAge(employees, 25, RnD) => 43
      */

    it("should return the average age for all employees") {
      averageAge(employees, 0, null) shouldBe 34
    }
    it("should return the average age for oldest employees") {
      averageAge(employees, 30, null) shouldBe 42
    }
    it("should return the average age for RnD employees") {
      averageAge(employees, 0, RnD) shouldBe 36
    }
    it("should return the average age for oldest RnD employees") {
      averageAge(employees, 30, RnD) shouldBe 43
    }
    it("should fail if there is no employees") {
      assertThrows[IllegalArgumentException] {
        averageAge(Seq(), 0, null)
      }
    }
    it("should fail if there is no employee in team") {
      assertThrows[IllegalArgumentException] {
        averageAge(employees, 0, Team(Seq()))
      }
    }
    it("should fail if all employees are too yound") {
      assertThrows[IllegalArgumentException] {
        averageAge(employees, 55, null)
      }
    }
  }
}
