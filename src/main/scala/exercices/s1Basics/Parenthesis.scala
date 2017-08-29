package exercices.s1Basics

object Parenthesis {
  def count(str: String): Int = {
    var cpt = 0
    for (c <- str) {
      if (c == '(') cpt = cpt + 1
      if (c == ')') cpt = cpt - 1
    }
    cpt
  }

  def validate(str: String): Boolean = {
    var cpt = 0
    for (c <- str) {
      if (c == '(') cpt = cpt + 1
      if (c == ')') cpt = cpt - 1
      if (cpt < 0) return false
    }
    cpt == 0
  }
}
