package exercices.s3FunctionalTypes

object CustomOption {

  trait Option[+A] {
    def isEmpty: Boolean

    def get: A

    def getOrElse[B >: A](default: => B): B // = if(isEmpty) default else get
    def map[B](f: A => B): Option[B] // = if(isEmpty) None else Some(f(get))
    def find(p: A => Boolean): Option[A] // = if(isEmpty || p(get)) this else None
    def filter(p: A => Boolean): Option[A] // = if(isEmpty || p(get)) this else None
    def flatMap[B](f: A => Option[B]): Option[B] // = if(isEmpty) None else f(get)
    def foldLeft[B](z: B)(op: (B, A) => B): B // = if(isEmpty) z else op(z, get)
    def foreach[U](f: A => U): Unit // = if(isEmpty) () else f(get)
    def exists(p: A => Boolean): Boolean // = if(isEmpty) false else p(get)
    def forall(p: A => Boolean): Boolean // = if(isEmpty) true else p(get)
    def toList: List[A] // = if(isEmpty) Nil else get :: Nil
  }

  object Option {
    def apply[A](value: A): Option[A] = if (value == null) None else Some(value)

    def empty[A]: Option[A] = None
  }

  case class Some[A](value: A) extends Option[A] {
    def isEmpty: Boolean = false

    def get: A = value

    def getOrElse[B >: A](default: => B): B = value

    def map[B](f: A => B): Option[B] = Some(f(value))

    def find(p: A => Boolean): Option[A] = if (p(value)) this else None

    def filter(p: A => Boolean): Option[A] = if (p(value)) this else None

    def flatMap[B](f: A => Option[B]): Option[B] = f(value)

    def foldLeft[B](z: B)(op: (B, A) => B): B = op(z, value)

    def foreach[U](f: A => U): Unit = f(value)

    def exists(p: A => Boolean): Boolean = p(value)

    def forall(p: A => Boolean): Boolean = p(value)

    def toList: List[A] = value :: Nil
  }

  case object None extends Option[Nothing] {
    def isEmpty: Boolean = true

    def get: Nothing = throw new NoSuchElementException

    def getOrElse[B >: Nothing](default: => B): B = default

    def map[B](f: Nothing => B): Option[B] = this

    def find(p: Nothing => Boolean): Option[Nothing] = this

    def filter(p: Nothing => Boolean): Option[Nothing] = this

    def flatMap[B](f: Nothing => Option[B]): Option[B] = this

    def foldLeft[B](z: B)(op: (B, Nothing) => B): B = z

    def foreach[U](f: Nothing => U): Unit = ()

    def exists(p: Nothing => Boolean): Boolean = false

    def forall(f: Nothing => Boolean): Boolean = true

    def toList: List[Nothing] = Nil
  }

}
