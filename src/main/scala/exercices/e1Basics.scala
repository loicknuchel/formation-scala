package exercices

import scala.annotation.tailrec

object e1Basics {

  object FizzBuzz {
    def fizzBuzz(n: Int): String =
      if (n % 15 == 0) "FizzBuzz"
      else if (n % 5 == 0) "Buzz"
      else if (n % 3 == 0) "Fizz"
      else n.toString

    def fizzBuzzNoIf(n: Int): String =
      (n % 3, n % 5) match {
        case (0, 0) => "FizzBuzz"
        case (_, 0) => "Buzz"
        case (0, _) => "Fizz"
        case _ => n.toString
      }
  }

  object AverageAge {

    case class Employee(name: String, age: Int)

    case class Team(employees: Seq[Employee]) {
      def has(employee: Employee): Boolean = employees.contains(employee)
    }

    def averageAge(employees: Seq[Employee], minAge: Int, team: Team): Int = {
      var total = 0
      var count = 0

      for (e <- employees) {
        if (e.age >= minAge && (team == null || team.has(e))) {
          total += e.age
          count += 1
        }
      }

      if (count == 0) throw new IllegalArgumentException("no employee matching criterias")
      else total / count
    }
  }

  object Factoriel {
    def factoriel(n: Int): Int = {
      var res = 1
      for (x <- 1 to n) {
        res *= x
        if (res < 1) {
          throw new IllegalArgumentException(s"factoriel($n) exceed Int.MaxValue !")
        }
      }
      res
    }

    def factorielRec(n: Int): Int =
      if (n < 1) 1
      else n * factorielRec(n - 1)

    def factorielNoIf(n: Int): Int = n match {
      case 0 => 1
      case _ => n * factorielNoIf(n - 1)
    }

    def factorielTailRec(n: Int): Int = {
      @tailrec
      def internal(n: Int, acc: Int = 1): Int =
        if (n < 2) acc
        else internal(n - 1, acc * n)

      internal(n)
    }
  }

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

  object Parenthesis {
    def count(str: String): Int = {
      var cpt = 0
      for (c <- str) {
        if (c == '(') cpt = cpt + 1
        if (c == ')') cpt = cpt - 1
      }
      cpt
    }

    def validate(str: String): Boolean = {
      var cpt = 0
      for (c <- str) {
        if (c == '(') cpt = cpt + 1
        if (c == ')') cpt = cpt - 1
        if (cpt < 0) return false
      }
      cpt == 0
    }
  }

  object ReversePolishNotation {
    def eval(str: String): Int = {
      @tailrec
      def inner(args: List[String], stack: List[Int]): Int = {
        args.headOption match {
          case Some("+") => inner(args.tail, (stack.tail.head + stack.head) :: stack.tail.tail)
          case Some("*") => inner(args.tail, (stack.tail.head * stack.head) :: stack.tail.tail)
          case Some("-") => inner(args.tail, (stack.tail.head - stack.head) :: stack.tail.tail)
          case Some("/") => inner(args.tail, (stack.tail.head / stack.head) :: stack.tail.tail)
          case Some(arg) => inner(args.tail, arg.toInt :: stack)
          case None => stack.head
        }
      }

      inner(str.split(" ").toList, List())
    }
  }

}
