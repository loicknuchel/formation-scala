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

    case class ::[A](head: A, tail: List[A])
      extends List[A]

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

    val h1: Int = nums.head // 1
    val h2: Int = nil.head // NoSuchElementException
    val t1: List[Int] = nums.tail // List(2, 3)
    val t2: List[Int] = nil.tail // UnsupportedOperationException

    val o1: Option[Int] = nums.headOption // Some(1)
    val o2: Option[Int] = nil.headOption // None

    val l1: Int = nums.length // 3
    val l2: Int = nil.length // 0
    val i1: Boolean = nums.isEmpty // false
    val i2: Boolean = nil.isEmpty // true

    val s1: String = nums.mkString(", ") // "1, 2, 3"
    val s2: String = nil.mkString(", ") // ""
  }

  object ListMap {
    // transformer les éléments
    List(1, 2, 3).map(n => n.toString) // List("1", "2", "3")

    // List[A].map(A => B): List[B]

    case class User(name: String, age: Int)

    val users = List(
      User("Jean", 24),
      User("Anna", 28),
      User("Loïc", 29))

    users.map(user => user.age) // List(24, 28, 29)
    users.map(_.age) // List(24, 28, 29)

    // List[User].map(User => Int): List[Int]
  }

  object ListFlatten {
    // fusioner deux niveaux de listes
    List(List(1, 2), List(), List(3)).flatten // List(1, 2, 3)

    // List[List[A]].flatten: List[A]

    case class Movie(id: Int, ratings: List[Int])

    val movies = List(
      Movie(1, List(2, 3, 3)),
      Movie(2, List(1, 3, 2)),
      Movie(3, List(5, 4)))

    val r: List[List[Int]] = movies.map(_.ratings)
    // List(List(2, 3, 3), List(1, 3, 2), List(5, 4))
    val ratings: List[Int] = r.flatten
    // List(2, 3, 3, 1, 3, 2, 5, 4)
    val avg: Int = ratings.sum / ratings.length
  }

  object ListFlatMap {
    // transformer les éléments
    List(1, 2, 3).map(n => 1 to n).flatten
    // List(1, 1, 2, 1, 2, 3)
    List(1, 2, 3).flatMap(n => 1 to n)

    // List[A].flatMap(A => List[B]): List[B]

    case class Movie(id: Int, ratings: List[Int])

    val movies: List[Movie] = List(
      Movie(1, List(2, 3, 3, 5, 4)),
      Movie(2, List(1, 3, 2, 1, 2, 2)),
      Movie(3, List(5, 4)))

    val r: List[Int] = movies.flatMap(_.ratings)
    // List(2, 3, 3, 1, 3, 2, 5, 4)
    val avg: Int = r.sum / r.length

    // List[Movie].flatMap(Movie=>List[Int]): List[Int]
  }

  object ListFilter {
    // sélectionner des éléments
    List(1, 2, 3).filter(n => n % 2 == 1) // List(1, 3)

    // List[A].filter(A => Boolean): List[A]

    case class User(name: String, age: Int)

    val users = List(
      User("Jean", 24),
      User("Anna", 28),
      User("Loïc", 29))

    users.filter(_.age > 25)
    // List(User("Anna", 28), User("Loïc", 29))

    // List[User].filter(User => Boolean): List[User]
  }

  object ListCollect {
    // ???
  }

  object ListFind {
    // ???
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
  }

  object ApiFonctionnelleOption {
    val some: Option[Int] = Some(1)

    // API fonctionnelle
    some.map(n => n + 1)           // Some(2)
    some.map(n => Some(3))         // Some(Some(3))
    some.map(n => Some(3)).flatten // Some(3)
    some.flatMap(n => Some(3))     // Some(3)
    some.filter(n => n > 0)        // Some(1)
    some.filter(n => n < 0)        // None
  }

  object MapUsage {
    val nums: Map[String, Int] = Map(
      "1" -> 1,
      "2" -> 2)
    val v1: Option[Int] = nums.get("1") // Some(1)
    val v2: Option[Int] = nums.get("3") // None
    val v3: Int = nums.getOrElse("1", 0) // 1
    val v4: Int = nums.getOrElse("3", 0) // 0
    val v5: Int = nums.size // 2
    val v6: Boolean = nums.contains("1") // true

    // Map[A, B] <=> List[(A, B)]

    val list: List[(String, Int)] = nums.toList
    val map: Map[String, Int] = list.toMap
  }

  object MapMap {
    val abv: Map[String, String] = Map(
      "fr" -> "France",
      "be" -> "Belgique",
      "en" -> "Angleterre")

    // Map[X].map(X => Y): Map[Y]
    // Map[A,B].map((A,B) => (C,D)): Map[C,D]

    abv.map(t => (t._1, t._2.length))
    abv.map({ case (k, v) => (k, v.length) })
    abv.map { case (k, v) => (k, v.length) }
    // Map("fr" -> 6, "be" -> 8, "en" -> 10)

    // Map[A, B].mapValues(B => C): Map[A, C]

    abv.mapValues(v => v.length)
  }

  object MapFilter {
    val abv: Map[String, String] = Map(
      "fr" -> "France",
      "be" -> "Belgique",
      "en" -> "Angleterre")

    // Map[A,B].filter((A,B) => Boolean): Map[A,B]

    val f1 = abv.filter(_._2.contains("r"))
    val f2 = abv.filter {case (_, v) => v.contains("r")}
    // Map("fr" -> "France", "en" -> "Angleterre")
  }

  object MapFind {
    val abv: Map[String, String] = Map(
      "fr" -> "France",
      "be" -> "Belgique",
      "en" -> "Angleterre")

    // Map[A,B].find((A,B) => Boolean): Option[(A,B)]

    val f1 = abv.find(_._2.contains("r"))
    val f2 = abv.find {case (_, v) => v.contains("r")}
    // Some(("fr", "France"))
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
    val r1: Iterator[List[Int]] = nums.sliding(2, 1) // Iterator(List(1, 3), List(3, 2), List(2, 2), List(2, 4))
    val r2: Map[Int, List[Int]] = nums.groupBy(n => n % 2) // Map(0 -> List(2, 2, 4), 1 -> List(1, 3))
    val r3: Boolean = nums.forall(_ < 5) // true
    val r4: Int = nums.min // 1
    val r5: Int = nums.minBy(_.toString) // 1
    val r6: Int = nums.sum // 12
    val r7: Int = nums.reduceLeft((acc, cur) => acc + cur) // 12
    val r8: Int = nums.foldLeft(0)((acc, cur) => acc + cur) // 12
    val r9: List[(Int, String)] = nums.zip(List("a", "b", "c")) // List((1,a), (3,b), (2,c))
  }

}
