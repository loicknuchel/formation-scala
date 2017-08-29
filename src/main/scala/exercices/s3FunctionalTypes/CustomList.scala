package exercices.s3FunctionalTypes

object CustomList {

  trait List[+A] {
    def isEmpty: Boolean

    def head: A

    def tail: List[A]

    def headOption: Option[A] // = if(isEmpty) None else Some(head)
    def size: Int // = if(isEmpty) 0 else tail.size + 1
    def take(n: Int): List[A] // = if(isEmpty || n <= 0) Nil else ::(head, tail.take(n - 1))
    def map[B](f: A => B): List[B] // = if(isEmpty) Nil else ::(f(head), tail.map(f))
    def find(p: A => Boolean): Option[A] // = if(isEmpty) None else if(p(head)) Some(head) else tail.find(p)
    def filter(p: A => Boolean): List[A] // = if(isEmpty) Nil else if(p(head)) ::(head, tail.filter(p)) else tail.filter(p)
    def ++[B >: A](that: List[B]): List[B] // = if(isEmpty) that else ::(head, tail ++ that)
    def flatMap[B](f: A => List[B]): List[B] // = if(isEmpty) Nil else f(head) ++ tail.flatMap(f)
    def foldLeft[B](z: B)(op: (B, A) => B): B // = if(isEmpty) z else tail.foldLeft(op(z, head))(op)
    def reduceLeft[B >: A](op: (B, A) => B): B // = if(isEmpty) throw new UnsupportedOperationException else tail.foldLeft[B](head)(op)
    def sum[B >: A](implicit num: Numeric[B]): B = foldLeft(num.zero)(num.plus)
  }

  object List {
    def apply[A](values: A*): List[A] = {
      def inner(values: Seq[A], acc: List[A]): List[A] =
        if (values.isEmpty) acc
        else inner(values.tail, ::(values.head, acc))

      inner(values.reverse, Nil)
    }

    def empty[A]: List[A] = Nil
  }

  case class ::[A](head: A, tail: List[A]) extends List[A] {
    def isEmpty: Boolean = false

    def headOption: Option[A] = Some(head)

    def size: Int = 1 + tail.size

    def take(n: Int): List[A] = if (n > 0) ::(head, tail.take(n - 1)) else Nil

    def map[B](f: A => B): List[B] = ::(f(head), tail.map(f))

    def find(p: A => Boolean): Option[A] = if (p(head)) Some(head) else tail.find(p)

    def filter(p: A => Boolean): List[A] = if (p(head)) ::(head, tail.filter(p)) else tail.filter(p)

    def ++[B >: A](that: List[B]): List[B] = ::(head, tail ++ that)

    def flatMap[B](f: A => List[B]): List[B] = f(head) ++ tail.flatMap(f)

    def foldLeft[B](z: B)(op: (B, A) => B): B = tail.foldLeft(op(z, head))(op)

    def reduceLeft[B >: A](op: (B, A) => B): B = tail.foldLeft[B](head)(op)
  }

  case object Nil extends List[Nothing] {
    def isEmpty: Boolean = true

    def head: Nothing = throw new NoSuchElementException

    def tail: List[Nothing] = throw new UnsupportedOperationException

    def headOption: Option[Nothing] = None

    def size: Int = 0

    def take(n: Int): List[Nothing] = this

    def map[B](f: Nothing => B): List[B] = this

    def find(p: Nothing => Boolean): Option[Nothing] = None

    def filter(p: Nothing => Boolean): List[Nothing] = this

    def ++[B >: Nothing](that: List[B]): List[B] = that

    def flatMap[B](f: Nothing => List[B]): List[B] = this

    def foldLeft[B](z: B)(op: (B, Nothing) => B): B = z

    def reduceLeft[B >: Nothing](op: (B, Nothing) => B): B = throw new UnsupportedOperationException
  }

}
