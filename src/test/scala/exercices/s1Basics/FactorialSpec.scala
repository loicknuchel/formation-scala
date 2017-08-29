package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class FactorialSpec extends FunSpec with Matchers {
  describe("Factorial") {
    import Factorial._
    it("should return 1 when 1") {
      factorial(1) shouldBe 1
    }
    it("should be correct for the firsts results") {
      factorial(2) shouldBe 2
      factorial(3) shouldBe 6
      factorial(4) shouldBe 24
      factorial(5) shouldBe 120
      factorial(6) shouldBe 720
      factorial(7) shouldBe 5040
      factorial(8) shouldBe 40320
      factorial(9) shouldBe 362880
    }
    it("should return 1 when < 1") {
      factorial(0) shouldBe 1
      factorial(-1) shouldBe 1
      factorial(-678) shouldBe 1
    }
    it("should work for high numbers") {
      factorial(12) shouldBe 479001600
      factorial(16) shouldBe 20922789888000l
      factorial(20) shouldBe 2432902008176640000l
      factorial(40) shouldBe BigInt("815915283247897734345611269596115894272000000000")
      factorial(100) shouldBe BigInt("93326215443944152681699238856266700490715968264381621468592963895217599993229915608941463976156518286253697920827223758251185210916864000000000000000000000000")
    }
  }
}
