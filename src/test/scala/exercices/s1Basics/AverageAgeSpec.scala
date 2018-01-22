package exercices.s1Basics

import exercices.s1Basics.AverageAge.Team
import org.scalatest.{FunSpec, Matchers}

class AverageAgeSpec extends FunSpec with Matchers {

  def averageAge(employees: Seq[AverageAge.Employee], min: Int = 0, team: Team = null): Double = {
    var total: Double = 0
    var count = 0
    for (e <- employees) {
      if (e.age > min && (team == null || team.has(e))) {
        total = total + e.age
        count = count + 1
      }
    }
    total / count
  }

  describe("AverageAge") {
    import AverageAge._

    val employees: Seq[Employee] = Seq(
      Employee("Jean", 22),
      Employee("Corinne", 54),
      Employee("Fanny", 32),
      Employee("Claude", 40),
      Employee("Cécile", 25))
    val RnD: Team = Team(employees.take(3))

    it("should return 34.6 when all employees") {
      averageAge(employees) shouldBe 34.6
    }
    it("should return 42 when employees above 25") {
      averageAge(employees, 25) shouldBe 42
    }
    it("should return 43 when employees above 25 and in RnD") {
      averageAge(employees, 25, RnD) shouldBe 43
    }
  }
}
