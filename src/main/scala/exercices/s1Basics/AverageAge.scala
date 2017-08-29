package exercices.s1Basics

object AverageAge {

  case class Employee(name: String, age: Int)

  case class Team(employees: Seq[Employee]) {
    def has(employee: Employee): Boolean = employees.contains(employee)
  }

}
