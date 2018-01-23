package exercices.s1Basics

object AverageAge {
  /**
    * Code ici la fonction averageAge qui prends en paramètre une liste d'employés et calcule leur âge moyen.
    * Une fois fait, ajoute un paramètre minAge pour ne pas prendre en compte les plus jeunes.
    * Enfin, ajoute un paramètre team qui permette de prendre en compte que les employés de l'équipe, cette fonctionnalités doit être facultative.
    *
    * Des données d'exemple sont fournies dans les tests ;)
    */

  case class Employee(name: String, age: Int)

  case class Team(employees: Seq[Employee]) {
    def has(employee: Employee): Boolean = employees.contains(employee)
  }

}
