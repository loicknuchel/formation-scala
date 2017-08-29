package exercices.s1Basics

import scala.annotation.tailrec

object Factorial {
  def factorial(n: Int): BigInt = {
    var res = BigInt(1)
    for (x <- 1 to n) {
      res *= x
    }
    res
  }

  def factorialRec(n: Int): BigInt =
    if (n < 1) 1
    else n * factorialRec(n - 1)

  @tailrec
  def factorialTailRec(n: Int, acc: BigInt = 1): BigInt =
    if (n < 1) acc
    else factorialTailRec(n - 1, acc * n)
}
