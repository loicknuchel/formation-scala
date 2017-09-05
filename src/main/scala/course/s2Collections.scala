package course

import scala.collection.immutable.List
import scala.util.control.NonFatal

// http://docs.scala-lang.org/overviews/collections/overview.html
object s2Collections {

  object ListDefinition {

    sealed abstract class List[A] {
      def head: A

      def tail: List[A]
    }

    final case class ::[A](
                            head: A,
                            tail: List[A]
                          ) extends List[A]

    case object Nil extends List[Nothing] {
      override def head: Nothing =
        throw new NoSuchElementException("head of empty list")

      override def tail: List[Nothing] =
        throw new UnsupportedOperationException("tail of empty list")
    }

  }

  object ListUsage {
    val nums: List[Int] = List(1, 2, 3)
    val nil: List[Int] = Nil

    val head: Int = nums.head // 1
    val elt: Int = nums(1) // 2
    val tail: List[Int] = nums.tail // List(2, 3)

    val first: Int = nil.head // NoSuchElementException
    val item: Int = nil(1) // IndexOutOfBoundsException
    val r: List[Int] = nil.tail // UnsupportedOperationException


    val l = List(2, 3)
    val r1: List[Int] = 1 :: l // List(1, 2, 3)
    val r2: List[Int] = List(0, 1) ::: l // List(0, 1, 2, 3)
    val r3: List[Int] = 1 +: l // List(1, 2, 3)
    val r4: List[Int] = l :+ 4 // List(2, 3, 4)
    val r5: List[Int] = l ++ List(4, 5) // List(2, 3, 4, 5)
  }

  object ListMatch {
    val nums: List[Int] = List(1, 2, 3)

    nums match {
      case head :: Nil => (head, List())
      case head :: tail => (head, tail)
    }
    // (1, List(2, 3))

    nums match {
      case List(a) => a
      case List(a, b) => a + b
      case List(a, b, c) => a + b + c
      case _ => -1
    }
    // 6
  }

  object SimpleFunctionalApi {
    val nums: List[Int] = List(1, 2, 3)

    // transformer les éléments
    val strings: List[String] = nums.map(n => n.toString)
    // List("1", "2", "3")
    val inc: List[Int] = nums.map(_ + 1) // List(2, 3, 4)

    // sélectionner des éléments
    val imp: List[Int] = nums.filter(n => n % 2 == 1) // List(1, 3)

    // chercher un élément
    val impair: Option[Int] = nums.find(_ % 2 == 1) // Some(1)
  }

  object OptionDefinition {

    sealed abstract class Option[A] {
      def isEmpty: Boolean

      def get: A
    }

    final case class Some[A](v: A) extends Option[A] {
      def isEmpty = false

      def get: A = v
    }

    case object None extends Option[Nothing] {
      def isEmpty = true

      def get =
        throw new NoSuchElementException("None.get")
    }

  }

  object OptionUsage {
    val some: Option[Int] = Some(1)

    // extraire le contenu
    val v1: Int = some.get // 1
    val v2: Int = None.get // NoSuchElementException
    val v3: Int = some.getOrElse(0) // 1
    val v4: Int = None.getOrElse(0) // 0
    val v5: Int = some match {
      case Some(value) => value
      case None => 0
    }

    // API fonctionnelle
    val inc: Option[Int] = some.map(n => n + 1) // Some(2)
    val digit: Option[Int] = some.filter(n => n < 10) // Some(1)
    val number: Option[Int] = some.find(n => n > 9) // None
  }

  object MapUsage {
    val nums: Map[String, Int] = Map(
      "1" -> 1,
      "2" -> 2
    )
    // accéder à un élément
    val v1: Option[Int] = nums.get("1") // Some(1)
    val v2: Option[Int] = nums.get("3") // None
    val v3: Int = nums("1") // 1
    val v4: Int = nums("3") // NoSuchElementException

    // modifier la Map
    val r1: Map[String, Int] = nums + ("3" -> 3)
    val r2: Map[String, Int] = nums + ("3" -> 3, "4" -> 4)
    val r3: Map[String, Int] = nums ++ Map("3" -> 3, "4" -> 4)
    val r4: Map[String, Int] = nums - "1"
    val r5: Map[String, Int] = nums - ("1", "2")
    val r6: Map[String, Int] = nums -- List("1", "2")
  }

  object Tuple {
    1 -> "a" == new Tuple2[Int, String](1, "a")
    1 -> "a" == (1, "a")

    (1, 2, 3) == new Tuple3[Int, Int, Int](1, 2, 3)

    val tri: (Int, String, Boolean) = (1, "b", false)

    val n: Int = tri match {
      case (a, _, true) => a + 1
      case (a, _, _) => a
    }

    val first: Int = tri._1 // 1
    val second: String = tri._2 // "b"
    val third: Boolean = tri._3 // false

    val (i, s, b) = tri
  }

  object MapApiFonctionnelle {
    val abv: Map[String, String] = Map(
      "fr" -> "France",
      "be" -> "Belgique",
      "en" -> "Angleterre"
    )

    val m1: Map[String, Int] = abv.map(t => (t._1, t._2.length))
    val m2: Map[String, Int] = abv.map({ case (k, v) => (k, v.length) })
    val f1: Map[String, String] = abv.filter(_._2.contains("r"))
    val f2: Map[String, String] = abv.filter({ case (_, v) => v(0) == 'F' })
    val f3: Option[(String, String)] = abv.find(_._2.contains("r"))
    val f4: Option[(String, String)] = abv.find({ case (_, v) => v(0) == 'F' })

    val list: List[(String, String)] = abv.toList
    val map: Map[String, String] = list.toMap
  }

  object ApiFonctionnelle1 {
    val nums = List(1, 3, 2, 2, 4)
    val r1: Option[Int] = nums.headOption // Some(1)
    val r2: Option[Int] = nums.lastOption // Some(4)
    val r3: List[Int] = nums.take(2) // List(1, 3)
    val r4: List[Int] = nums.takeRight(2) // List(2, 4)
    val r5: List[Int] = nums.drop(2) // List(2, 2, 4)
    val r6: List[Int] = nums.dropRight(2) // List(1, 3, 2)

    val r7: List[String] = nums.collect { case x if x % 2 == 1 => x.toString } // List("1", "3")

    val r8: List[Int] = nums.reverse // List(4, 2, 2, 3, 1)
    val r9: List[Int] = nums.distinct // List(1, 3, 2, 4)
    val r10: List[Int] = nums.sorted // List(1, 2, 2, 3, 4)
    val r11: List[Int] = nums.sortBy(n => n % 2) // List(2, 2, 4, 1, 3)
    val r12: List[Int] = nums.sortWith((a, b) => a > b) // List(4, 3, 2, 2, 1)
    val r13: String = nums.mkString(", ") // "1, 3, 2, 2, 4"
    val r14: String = nums.mkString("List(", ", ", ")") // "List(1, 3, 2, 2, 4)"
  }

  object ApiFonctionnelle2 {
    val nums = List(1, 3, 2, 2, 4)
    val r0: Iterator[List[Int]] = nums.grouped(2) // Iterator(List(1, 3), List(2, 2), List(4))
    val r1: Iterator[List[Int]] = nums.sliding(2, 3) // Iterator(List(1, 3), List(2, 4))
    val r2: Map[Int, List[Int]] = nums.groupBy(n => n % 2) // Map(0 -> List(2, 2, 4), 1 -> List(1, 3))
    val r3: Boolean = nums.forall(_ < 5) // true
    val r4: Int = nums.min // 1
    val r5: Int = nums.minBy(_.toString) // 1
    val r6: Int = nums.sum // 8
    val r7: Int = nums.reduceLeft((acc, cur) => acc + cur) // 8
    val r8: Int = nums.foldLeft(0)((acc, cur) => acc + cur) // 8
    val r9: List[(Int, String)] = nums.zip(List("a", "b", "c")) // List((1,a), (3,b), (2,c))
  }

  object ApiFonctionnelle3 {

    case class User(id: Int,
                    name: String,
                    mails: Seq[String])

    val users = Seq(
      User(1, "Jean", Seq("jean@me.fr")),
      User(2, "Lucie", Seq()),
      User(3, "Luc", Seq("luc@scala.io", "luc@me.fr")))

    val mails: Seq[Seq[String]] = users.map(_.mails)
    // Seq(Seq("jean@me.fr"),
    //     Seq(),
    //     Seq("luc@scala.io", "luc@me.fr"))

    val fMails: Seq[String] = users.flatMap(_.mails)
    // Seq("jean@me.fr", "luc@scala.io", "luc@me.fr")
  }

  object ApiFonctionnelle4 {
    val strs: List[String] = List("1", "foo", "4", "12", "bar")

    def toInt(str: String): Option[Int] = {
      try {
        Some(Integer.parseInt(str.trim))
      } catch {
        case NonFatal(_) => None
      }
    }

    val r1: List[Char] = strs.flatMap(_.toList) // List(1, f, o, o, 4, 1, 2, b, a, r)
    val r2: List[Int] = strs.flatMap(toInt) // List(1, 4, 12)
  }

}
