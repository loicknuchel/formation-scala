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
  }
}
