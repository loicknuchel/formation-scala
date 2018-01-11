package course

object s1Basics {

  object DefineValues {

    object Variable {
      // déclarer une variable
      var city: String = "Paris";

      // ';' optionnel
      var town: String = "Nice"

      // inférence de type
      var name = "Aix"

      // immutabilité
      val lang = "Scala"
    }

    object Function {
      // déclarer une méthode
      def size(in: String): Int = {
        return in.length();
      }

      // 'return' implicite, ';' optionnel
      def up(in: String): String = {
        in.toUpperCase()
      }

      // expression simple
      def add(a: Int, b: Int): Int = a + b

      // inférence de type (non recommandé)
      def sub(a: Int, b: Int) = a - b
    }

    object UseFunction {
      var city = "Paris"
      var town = "Nice"

      def size(in: String): Int = in.length

      // exécuter une fonction
      val s = size(city)

      // exécuter une méthode
      val l1 = town.length()

      // '.' optionnel (non recommandé)
      val l2 = town length()

      // '()' optionnelles (non recommandé)
      val l3 = town.length
      val c1 = town.charAt(1)
      val c2 = town charAt 1
    }

    object FunctionAsValue {
      // définir une fonction dans une fonction
      def upperCase(s: String): String = {
        def upper(c: Char): Char = {
          c.toUpper
        }

        s.map(upper)
      }

      // affecter une fonction à une valeur
      val upper1: String => String = upperCase
      val upper2 = upperCase _

      // puis l'utiliser
      val res = upper1("test")
    }

  }

  object Goodies {

    object String {
      // définir une String
      val name = "World"

      // triple quote
      val json =
        """{"name": "Loïc"}"""

      // multi-ligne
      val text =
        """Bonjour à tous,
          |Bienvenu à la formation Scala !
          |A Scala guy !
        """.stripMargin

      // interpolation
      val hello = s"Hello $name !"
    }

    object Function {
      // paramètres nommés
      def sub(a: Int, b: Int): Int = a - b

      val s1 = sub(5, 3)
      val s2 = sub(b = 3, a = 5)

      // paramètres par défaut
      def add(a: Int, b: Int = 1): Int = a + b

      val a1 = add(3, 4)
      val a2 = add(5)

      // make it work !
      val t1 = ???
      val t2: Nothing = throw new NotImplementedError
    }

  }

  object UseSyntax {

    object If {
      val value = "test"

      if (value.length > 6) {
        println("long")
      } else if (value.length > 4) {
        println("intermediate")
      } else {
        println("short")
      }

      // > short
    }

    object For {
      for (i <- 3 to 5) {
        println("i: " + i)
      }
      // > i: 3
      // > i: 4
      // > i: 5

      for (c <- Seq("a", "b", "c")) {
        println("c: " + c)
      }
      // > c: a
      // > c: b
      // > c: c
    }

  }

  object ObjectStructure {
    // http://docs.scala-lang.org/tutorials/tour/unified-types.html
    // Any, AnyVal, AnyRef, Unit, Nothing
  }

  object ObjectOriented {

    object Basic {

      class Point(var x: Int, var y: Int) {
        def move(dx: Int, dy: Int): Unit = {
          x += dx
          y += dy
        }

        override def toString: String = s"Point($x, $y)"
      }

      val p = new Point(3, 4)
      p.move(2, -2)
      println(p) // > Point(5, 2)
    }

    object Immutable {

      class Point(val x: Int, val y: Int) {
        def move(dx: Int, dy: Int): Point = {
          new Point(x + dx, y + dy)
        }

        override def toString: String = s"Point($x, $y)"
      }

      val p = new Point(3, 4)
      val p2 = p.move(2, -2)
      println(p2) // > Point(5, 2)
    }

    object Object {

      // objet singleton remplaçant l'usage de static
      object Utils {
        def toUpper(str: String): String =
          str.toUpperCase
      }

      println(Utils.toUpper("test")) // > TEST

      // méthode "magique"
      object Upper {
        def apply(str: String): String =
          str.toUpperCase
      }

      println(Upper.apply("test")) // > TEST
      println(Upper("test")) // > TEST
    }

    object Trait {

      trait HasName {
        val name: String

        def fullName(): String

        val hasName: Boolean = true

        def initials(): String =
          name.split(" ").map(_.head).mkString
      }

      class User(val name: String) extends HasName {
        def fullName(): String = name
      }

    }

    object Sealed {

      sealed trait Size

      object S extends Size

      object M extends Size

      object L extends Size

      class Custom(val size: Int) extends Size

      sealed abstract class Lang(val abbr: String)

      object FR extends Lang("fr")

      object EN extends Lang("en")

      object ES extends Lang("es")

    }

    object CaseClass {

      case class Point(x: Int, y: Int) {
        def move(dx: Int, dy: Int): Point =
          Point(x + dx, y + dy)
      }

      val p = Point(2, 3)
      val p2 = p.copy(y = 4)
      println(p2.toString) // > Point(2, 4)

      p2 match {
        case Point(x, _) => print("x: " + x) // > x: 2
        case _ => println("no match")
      }
    }

    object ExplainedCaseClass {

      class Point(val x: Int, val y: Int) {
        def move(dx: Int, dy: Int): Point =
          Point.apply(x + dx, y + dy)

        def copy(x: Int = x, y: Int = y): Point =
          Point(x, y)

        override def toString: String = s"Point($x, $y)"
      }

      object Point {
        def apply(x: Int, y: Int): Point =
          new Point(x, y)

        def unapply(p: Point): Option[(Int, Int)] =
          Some(p.x -> p.y)
      }

    }

  }

  object Tuple {
    val tu: (String, Int) = ("a", 1)

    val v1: String = tu._1
    val v2: Int = tu._2

    val (t1, t2) = tu

    val tri: (Int, String, Boolean) = (1, "b", false)

    val u1: Int = tri._1
    val u2: String = tri._2
    val u3: Boolean = tri._3

    val (s1, s2, s3) = tri
  }

  object PatternMatching {

    object Match {

      case class Point(x: Int, y: Int)

      val value: Any = "test"

      val res: String = value match {
        case "a" => "exact match"
        case v@"b" => "exact match: " + v
        case 5 => "exact match"
        case (2, true) => "exact match"
        case i: String => "type match: " + i
        case _: Boolean => "type match"
        case (false, s) => "extract tuple: " + s
        case Point(0, y) => "extract case class: " + y
        case x: Int if x > 1 => "condition"
        case (true, Point(x, 3)) if x < 0 => "complex"
        case _ => "default"
      }
    }

  }

}
