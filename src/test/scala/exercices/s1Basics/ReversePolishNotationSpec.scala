package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class ReversePolishNotationSpec extends FunSpec with Matchers {
  describe("ReversePolishNotation") {
    import ReversePolishNotation._
    it("should add") {
      eval("3 4 +") shouldBe 7
    }
    it("should multiply") {
      eval("3 4 *") shouldBe 12
    }
    it("should substract") {
      eval("3 4 -") shouldBe -1
    }
    it("should divide") {
      eval("4 2 /") shouldBe 2
    }
    it("should do several operations without parenthesis") {
      eval("3 4 - 5 +") shouldBe 4
    }
    it("should do several operations with priority") {
      eval("3 4 5 * +") shouldBe 23
    }
  }
}
