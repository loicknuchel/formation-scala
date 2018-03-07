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

    def averageAge(employees: Seq[AverageAge.Employee], min: Int, team: Team): Float = {
      var sum = 0
      var count = 0
      for (e <- employees) {
        if (e.age > min && (team == null || team.has(e))) {
          sum += e.age
          count += 1
        }
      }
      if (count > 0) sum.toFloat / count
      else 0
    }

    it("should return 34.6 for all employees") {
      averageAge(employees, 0, null) shouldBe 34.6f
    }
    it("should be 42 for employees above 25") {
      averageAge(employees, 25, null) shouldBe 42f
    }
    it("should be 43 for employees in RnD above 25") {
      averageAge(employees, 25, RnD) shouldBe 43f
    }
  }
}
