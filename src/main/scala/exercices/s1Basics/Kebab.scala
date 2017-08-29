package exercices.s1Basics

object Kebab {

  case class Ingredient(name: String, isVegetarian: Boolean, isPescetarianism: Boolean)

  case class Kebab(ingredients: Seq[Ingredient]) {
    def isVegetarian: Boolean = ingredients.forall(_.isVegetarian)

    def isPescetarianism: Boolean = ingredients.forall(_.isPescetarianism)

    def doubleCheddar: Kebab =
      Kebab(ingredients.flatMap {
        case c@Ingredient("cheddar", _, _) => Seq(c, c)
        case i => Seq(i)
      })

    def removeOnion: Kebab =
      Kebab(ingredients.flatMap {
        case Ingredient("onion", _, _) => None
        case i => Some(i)
      })
  }

}
