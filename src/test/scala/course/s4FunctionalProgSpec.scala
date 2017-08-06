package course

import org.scalatest.{FunSpec, Matchers}

class s4FunctionalProgSpec extends FunSpec with Matchers {
  describe("ContainSideEffects") {
    import s4FunctionalProg.ContainSideEffects._
    describe("IO") {
      it("should not run code on creation") {
        var x = ""
        val io = new IO(() => {x += "a"; 1})
        x shouldBe ""
      }
      it("should run code when call run()") {
        var x = ""
        val io = new IO(() => {x += "a"; 1})
        io.run() shouldBe 1
        x shouldBe "a"
      }
      it("should run code every time run() is called") {
        var x = ""
        val io = new IO(() => {x += "a"; 1})
        io.run() shouldBe 1
        io.run() shouldBe 1
        x shouldBe "aa"
      }
      it("should map without executing the code") {
        var x = ""
        val io = new IO(() => {x += "a"; 1})
          .map(i => { x += "b"; i + 1})
        x shouldBe ""
        io.run() shouldBe 2
        x shouldBe "ab"
      }
      it("should not modify the previous IO with map") {
        var x = ""
        val io = new IO(() => {x += "a"; 1})
        val io2 = io.map(i => { x += "b"; i + 1})
        x shouldBe ""
        io.run() shouldBe 1
        x shouldBe "a"
      }
      it("should flatMap without executing the code") {
        var x = ""
        val io = new IO(() => {x += "a"; 1})
          .flatMap(i => new IO(() => { x += "b"; i + 1}))
        x shouldBe ""
        io.run() shouldBe 2
        x shouldBe "ab"
      }
      it("should not modify the previous IO with flatMap") {
        var x = ""
        val io = new IO(() => {x += "a"; 1})
        val io2 = io.flatMap(i => new IO(() => { x += "b"; i + 1}))
        x shouldBe ""
        io.run() shouldBe 1
        x shouldBe "a"
      }
    }

    describe("sideEffectsEverywhere") {
      it("should execute") {
        sideEffectsEverywhere()
      }
    }
    describe("isolatedSideEffect") {
      it("should execute") {
        isolatedSideEffect()
      }
    }
    describe("isolatedSideEffectRefactored") {
      it("should execute") {
        isolatedSideEffectRefactored()
      }
    }
    describe("isolatedSideEffectDRY") {
      it("should execute") {
        isolatedSideEffectDRY()
      }
    }
    describe("breakingProgram") {
      it("should execute") {
        breakingProgram()
      }
    }
  }
}
