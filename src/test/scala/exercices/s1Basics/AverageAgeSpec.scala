package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class AverageAgeSpec extends FunSpec with Matchers {
  describe("AverageAge") {
    import AverageAge._

    val employees = Seq(
      Employee("Jean", 22),
      Employee("Corinne", 54),
      Employee("Fanny", 32),
      Employee("Claude", 40),
      Employee("Cécile", 25))
    val RnD = Team(employees.take(3))
  }
}
