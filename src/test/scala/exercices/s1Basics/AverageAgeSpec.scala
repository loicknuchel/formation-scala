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
      *   - averageAge(employees) shouldBe 34.6
      *   - averageAge(employees, 25) shouldBe 42
      *   - averageAge(employees, 25, RnD) shouldBe 43
      */

    def averageAge(employees: Seq[Employee], minAge: Int, team: Team): Double = {
      var total = 0.0
      var count = 0
      for (e <- employees) {
        if (e.age > minAge && (team == null || team.has(e))) {
          total += e.age
          count += 1
        }
      }
      total / count
    }

    it("should compute average age for oldest people and team") {
      val a = Seq(1)
      averageAge(employees, 25, RnD) shouldBe 43
    }
    it("should compute average age for oldest people") {
      averageAge(employees, 25, null) shouldBe 42
    }
    it("should compute average age") {
      averageAge(employees, 0, null) shouldBe 34.6
    }
  }
}
