package slides

import org.scalatest.{FunSpec, Matchers}

class s4FunctionalProgSpec extends FunSpec with Matchers {
  describe("ContainSideEffects") {
    import s4FunctionalProg.ContainSideEffects._
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
