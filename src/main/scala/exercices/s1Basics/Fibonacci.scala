package exercices.s1Basics

import scala.annotation.tailrec
import scala.collection.mutable

object Fibonacci {
  def fibonacci(n: Int): BigInt = {
    if (n <= 1) n
    else fibonacci(n - 1) + fibonacci(n - 2)
  }

  def fibonacciFast(n: Int): BigInt = {
    val arr = new Array[BigInt](n + 1)
    arr(0) = 0
    arr(1) = 1
    for (i <- 2 to n) {
      arr(i) = arr(i - 1) + arr(i - 2)
    }
    arr(n)
  }

  def fibonacciTail(n: Int): BigInt = {
    @tailrec
    def internal(n2: BigInt, n1: BigInt, n: Int): BigInt = {
      if (n > 0) internal(n1, n2 + n1, n - 1)
      else n2
    }

    internal(0, 1, n)
  }

  def memoize[I, O](f: I => O): I => O = new mutable.HashMap[I, O]() {
    override def apply(key: I): O = getOrElseUpdate(key, f(key))
  }

  val fibonacciMemo: Int => BigInt = memoize((n: Int) => {
    if (n <= 1) n
    else fibonacciMemo(n - 1) + fibonacciMemo(n - 2)
  })
}
