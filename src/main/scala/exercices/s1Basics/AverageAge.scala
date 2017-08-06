package exercices.s1Basics

object AverageAge {

  case class Employee(name: String, age: Int)

  case class Team(employees: Seq[Employee]) {
    def has(employee: Employee): Boolean = employees.contains(employee)
  }

  def averageAge(employees: Seq[Employee], minAge: Int, team: Team): Int = {
    var total = 0
    var count = 0

    for (e <- employees) {
      if (e.age >= minAge && (team == null || team.has(e))) {
        total += e.age
        count += 1
      }
    }

    if (count == 0) throw new IllegalArgumentException("no employee matching criterias")
    else total / count
  }
}
