package exercices.s1Basics

object FizzBuzz {
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
