package exercices.s1Basics

import java.util.Date

import org.scalatest.{FunSpec, Matchers}

class FibonacciSpec extends FunSpec with Matchers {
  describe("Fibonacci") {
    import Fibonacci._
    it("should be correct for the first results") {
      fibonacci(0) shouldBe 0
      fibonacci(1) shouldBe 1
      fibonacci(2) shouldBe 1
      fibonacci(3) shouldBe 2
      fibonacci(4) shouldBe 3
      fibonacci(5) shouldBe 5
      fibonacci(6) shouldBe 8
      fibonacci(7) shouldBe 13
      fibonacci(8) shouldBe 21
      fibonacci(9) shouldBe 34
    }
    it("should be fast") {
      val start = new Date().getTime
      fibonacci(30) shouldBe 832040
      val time = (new Date().getTime - start).toInt

      val startFast = new Date().getTime
      fibonacciFast(30) shouldBe 832040
      val timeFast = (new Date().getTime - startFast).toInt

      val startTail = new Date().getTime
      fibonacciTail(30) shouldBe 832040
      val timeTail = (new Date().getTime - startTail).toInt

      val startMemo = new Date().getTime
      fibonacciMemo(30) shouldBe 832040
      val timeMemo = (new Date().getTime - startMemo).toInt

      println(s"fibonacci(30)     in $time ms") // 109 ms
      println(s"fibonacciFast(30) in $timeFast ms") // 1 ms
      println(s"fibonacciTail(30) in $timeTail ms") // 0 ms
      println(s"fibonacciMemo(30) in $timeMemo ms") // 1 ms
    }
    it("should be really fast") {
      val startFast = new Date().getTime
      fibonacciFast(300) shouldBe BigInt("222232244629420445529739893461909967206666939096499764990979600")
      val timeFast = (new Date().getTime - startFast).toInt

      val startTail = new Date().getTime
      fibonacciTail(300) shouldBe BigInt("222232244629420445529739893461909967206666939096499764990979600")
      val timeTail = (new Date().getTime - startTail).toInt

      val startMemo = new Date().getTime
      fibonacciMemo(300) shouldBe BigInt("222232244629420445529739893461909967206666939096499764990979600")
      val timeMemo = (new Date().getTime - startMemo).toInt

      println(s"fibonacciFast(300) in $timeFast ms") // 19 ms
      println(s"fibonacciTail(300) in $timeTail ms") //  1 ms
      println(s"fibonacciMemo(300) in $timeMemo ms") // 13 ms
    }
  }
}
