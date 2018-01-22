package exercices.s1Basics

object FizzBuzz {
  def fizzBuzz2(i: Int): String =
    if (i % 15 == 0) "FizzBuzz"
    else if (i % 3 == 0) "Fizz"
    else if (i % 5 == 0) "Buzz"
    else i.toString

  def fizzBuzz(i: Int): String =
    (i % 3, i % 5) match {
      case (0, 0) => "FizzBuzz"
      case (0, _) => "Fizz"
      case (_, 0) => "Buzz"
      case _ => i.toString
    }

  def fizzBuzz3(i: Int): String =
    i match {
      case _ if i % 15 == 0 => "FizzBuzz"
      case _ if i % 5 == 0 => "Fizz"
      case _ if i % 3 == 0 => "Buzz"
      case _ => i.toString
    }
}
