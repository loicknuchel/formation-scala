package slides

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

    object Match {
      val value = "test"
      val cond = false

      value match {
        case "a" => println("found a")
        case "test" if cond => println("found test 1")
        case "test" => println("found test 2")
        case _ => println(s"$value not found")
      }
      // > found test 2
    }

  }

  object ExpressionEverywhere {

    object Slide1 {
      val v1 = "test"
      println("v1: " + v1) // > v1: "test"
      val v2 = {
        "test"
      }
      println("v2: " + v2) // > v2: "test"
      val v3 = {
        "val";
        "test"
      }
      println("v3: " + v3) // > v3: "test"

      val v4 = if (v3.length > 4) "long" else "short"
      println("v4: " + v4) // > v4: "short"
    }

    object Slide2 {
      val v5 = if ("text".length > 4) "long" // else ()
      println("v5: " + v5) // > v5: ()

      var x = 0
      val v6 = while (x < 3) {
        x += 1
      }
      println("v6: " + v6) // > v6: ()

      val v7 = for (c <- "test") {
        c.toUpper
      }
      println("v7: " + v7) // > v7: ()
    }

    object Slide3 {
      val v8 = for (c <- "test") yield {
        c.toUpper
      }
      println("v8: " + v8) // > v8: "TEST"

      val v9 = "test" match {
        case "a" => {
          println("First !")
          "found a"
        }
        case "test" => "found test"
        case _ => "not found"
      }
      println("v9: " + v9) // > v9: "found test"
    }

    object Slide4 {
      def up(s: String): String = s.toUpperCase

      val v10 = up({
        val text = "Salut"
        println(text)
        text
      })
      println("v10: " + v10) // > v10: "SALUT"
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

    /*
class Test(a: Int, val b: Int, var c: Int, private var d: Int) {
  val e: Int = a
  def move(dc: Int): Unit =
    c += dc
}

$ scala -Xprint:typer -e "..."
class Test extends scala.AnyRef {
  <paramaccessor> private[this] val a: Int = _;
  <paramaccessor> private[this] val b: Int = _;
  <stable> <accessor> <paramaccessor> def b: Int = Test.this.b;
  <paramaccessor> private[this] var c: Int = _;
  <accessor> <paramaccessor> def c: Int = Test.this.c;
  <accessor> <paramaccessor> def c_=(x$1: Int): Unit = Test.this.c = x$1;
  <paramaccessor> private[this] var d: Int = _;
  <accessor> <paramaccessor> private def d: Int = Test.this.d;
  <accessor> <paramaccessor> private def d_=(x$1: Int): Unit = Test.this.d = x$1;
  def <init>(a: Int, b: Int, c: Int, d: Int): this.Test = {
    Test.super.<init>();
    ()
  };
  private[this] val e: Int = Test.this.a;
  <stable> <accessor> def e: Int = Test.this.e;
  def move(dc: Int): Unit = Test.this.c_=(Test.this.c.+(dc))
}

$ scalac Test.scala && javap -p Test.class
public class Test {
  private final int b;
  private int c;
  private int d;
  private final int e;
  public int b();
  public int c();
  public void c_$eq(int);
  private int d();
  private void d_$eq(int);
  public int e();
  public void move(int);
  public Test(int, int, int, int);
}
     */

    /*
case class Test(value: Int)

$ scala -Xprint:typer -e "..."
case class Test extends AnyRef with Product with Serializable {
  <caseaccessor> <paramaccessor> private[this] val value: Int = _;
  <stable> <caseaccessor> <accessor> <paramaccessor> def value: Int = Test.this.value;
  def <init>(value: Int): this.Test = {
    Test.super.<init>();
    ()
  };
  <synthetic> def copy(value: Int = value): this.Test = new Test(value);
  <synthetic> def copy$default$1: Int = Test.this.value;
  override <synthetic> def productPrefix: String = "Test";
  <synthetic> def productArity: Int = 1;
  <synthetic> def productElement(x$1: Int): Any = x$1 match {
    case 0 => Test.this.value
    case _ => throw new IndexOutOfBoundsException(x$1.toString())
  };
  override <synthetic> def productIterator: Iterator[Any] = runtime.this.ScalaRunTime.typedProductIterator[Any](Test.this);
  <synthetic> def canEqual(x$1: Any): Boolean = x$1.$isInstanceOf[this.Test]();
  override <synthetic> def hashCode(): Int = {
    <synthetic> var acc: Int = -889275714;
    acc = Statics.this.mix(acc, value);
    Statics.this.finalizeHash(acc, 1)
  };
  override <synthetic> def toString(): String = ScalaRunTime.this._toString(Test.this);
  override <synthetic> def equals(x$1: Any): Boolean = Test.this.eq(x$1.asInstanceOf[Object]).||(x$1 match {
case (_: this.Test) => true
case _ => false
}.&&({
    <synthetic> val Test$1: this.Test = x$1.asInstanceOf[this.Test];
    Test.this.value.==(Test$1.value).&&(Test$1.canEqual(Test.this))
  }))
};
<synthetic> private object Test extends scala.runtime.AbstractFunction1[Int,this.Test] with Serializable {
  def <init>(): this.Test.type = {
    Test.super.<init>();
    ()
  };
  final override <synthetic> def toString(): String = "Test";
  case <synthetic> def apply(value: Int): this.Test = new Test(value);
  case <synthetic> def unapply(x$0: this.Test): Option[Int] = if (x$0.==(null))
    scala.this.None
  else
    Some.apply[Int](x$0.value);
  <synthetic> private def readResolve(): Object = $anon.this.Test
}

$ scalac Test.scala && javap -p Test.class
public class Test implements scala.Product,scala.Serializable {
  private final int value;
  public static scala.Option<java.lang.Object> unapply(Test);
  public static Test apply(int);
  public static <A> scala.Function1<java.lang.Object, A> andThen(scala.Function1<Test, A>);
  public static <A> scala.Function1<A, Test> compose(scala.Function1<A, java.lang.Object>);
  public int value();
  public Test copy(int);
  public int copy$default$1();
  public java.lang.String productPrefix();
  public int productArity();
  public java.lang.Object productElement(int);
  public scala.collection.Iterator<java.lang.Object> productIterator();
  public boolean canEqual(java.lang.Object);
  public int hashCode();
  public java.lang.String toString();
  public boolean equals(java.lang.Object);
  public Test(int);
}
     */
  }

}
