package exercices.s1Basics

object FizzBuzz {
  /**
    * Code ici la fonction fizzBuzz qui prends un entier en paramètre et renvoit une String en résultat.
    * Le résultat devra être :
    *   - Fizz si le nombre en paramètre est divisible par 3
    *   - Buzz si le nombre en paramètre est divisible par 5
    *   - FizzBuzz si le nombre en paramètre est divisible par 3 et par 5
    *   - le nombre sous forme de String sinon
    */

  def fizzbuzz(n: Int): String =
    if (n % 15 == 0) "FizzBuzz"
    else if (n % 5 == 0) "Buzz"
    else if (n % 3 == 0) "Fizz"
    else n.toString

  def fizzBuzzNoIf(n: Int): String =
    (n % 3, n % 5) match {
      case (0, 0) => "FizzBuzz"
      case (_, 0) => "Buzz"
      case (0, _) => "Fizz"
      case _ => n.toString
    }
}
