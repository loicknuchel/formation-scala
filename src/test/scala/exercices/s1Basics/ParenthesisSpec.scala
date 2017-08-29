package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class ParenthesisSpec extends FunSpec with Matchers {
  describe("Parenthesis") {
    import Parenthesis._
    describe("count") {
      it("should count non-closed parenthesis") {
        count("(") shouldBe 1
        count(")") shouldBe -1
        count("(())") shouldBe 0
        count(")(") shouldBe 0
      }
      it("should work with other chars") {
        count("(a,b,c))()") shouldBe -1
      }
    }
    describe("validate") {
      it("should be valid when all parenthesis are correctly closed") {
        validate("(())") shouldBe true
      }
      it("should not be valid when a parenthesis is not closed") {
        validate("(") shouldBe false
        validate("(()") shouldBe false
      }
      it("should not be valid when a parenthesis was not opened") {
        validate(")") shouldBe false
        validate(")(") shouldBe false
        validate("())") shouldBe false
      }
      it("should validate complex cases") {
        validate("()((())())") shouldBe true
      }
      it("should work with other chars") {
        validate("(1 + 3) * 2 + (6 / 2)") shouldBe true
        validate("(1 + 3 * 2 + (6 / 2)") shouldBe false
      }
    }
  }
}
