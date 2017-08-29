package exercices.s1Basics

import org.scalatest.{FunSpec, Matchers}

class KebabSpec extends FunSpec with Matchers {
  describe("Kebab") {
    import Kebab._
    describe("isVegetarian") {
      it("should be vegetarian when at least one ingredient is vegetarian") {
        Kebab(Seq()).isVegetarian shouldBe true
        Kebab(Seq(Ingredient("lettuce", true, true))).isVegetarian shouldBe true
        Kebab(Seq(Ingredient("beef", false, false))).isVegetarian shouldBe false
        Kebab(Seq(Ingredient("lettuce", true, true), Ingredient("fish", false, true))).isVegetarian shouldBe false
      }
    }
    describe("isPescetarianism") {
      it("should be vegetarian when at least one ingredient is vegetarian") {
        Kebab(Seq()).isPescetarianism shouldBe true
        Kebab(Seq(Ingredient("lettuce", true, true))).isPescetarianism shouldBe true
        Kebab(Seq(Ingredient("beef", false, false))).isPescetarianism shouldBe false
        Kebab(Seq(Ingredient("lettuce", true, true), Ingredient("fish", false, true))).isPescetarianism shouldBe true
      }
    }
    describe("doubleCheddar") {
      it("should double cheddar without changing ingredient order") {
        Kebab(Seq()).doubleCheddar shouldBe Kebab(Seq())
        Kebab(Seq(Ingredient("lettuce", true, true))).doubleCheddar shouldBe Kebab(Seq(Ingredient("lettuce", true, true)))
        Kebab(Seq(Ingredient("cheddar", true, true))).doubleCheddar shouldBe Kebab(Seq(Ingredient("cheddar", true, true), Ingredient("cheddar", true, true)))
        Kebab(Seq(
          Ingredient("lettuce", true, true),
          Ingredient("cheddar", true, true),
          Ingredient("beef", false, false))
        ).doubleCheddar shouldBe Kebab(Seq(
          Ingredient("lettuce", true, true),
          Ingredient("cheddar", true, true),
          Ingredient("cheddar", true, true),
          Ingredient("beef", false, false)
        ))
      }
    }
    describe("removeOnion") {
      it("should remove onions without changing ingredient order") {
        Kebab(Seq()).removeOnion shouldBe Kebab(Seq())
        Kebab(Seq(Ingredient("lettuce", true, true))).removeOnion shouldBe Kebab(Seq(Ingredient("lettuce", true, true)))
        Kebab(Seq(Ingredient("onion", true, true))).removeOnion shouldBe Kebab(Seq())
        Kebab(Seq(
          Ingredient("lettuce", true, true),
          Ingredient("onion", true, true),
          Ingredient("beef", false, false))
        ).removeOnion shouldBe Kebab(Seq(
          Ingredient("lettuce", true, true),
          Ingredient("beef", false, false)
        ))
      }
    }
  }
}
