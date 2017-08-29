package exercices.s3FunctionalTypes

import scala.util.{Failure, Success, Try}

// inspirated from https://github.com/typelevel/cats/blob/master/core/src/main/scala/cats/data/Validated.scala
object ValidationMonad {

  sealed abstract class Validation[+A] extends Product with Serializable {
    def fold[B](fe: Seq[String] => B, fa: A => B): B =
      this match {
        case Invalid(errors) => fe(errors)
        case Valid(value) => fa(value)
      }

    def isValid: Boolean =
      fold(_ => false, _ => true)

    def isInvalid: Boolean =
      !isValid

    def get: A =
      fold(_ => throw new NoSuchElementException("Invalid.get"), identity)

    def getOrElse[B >: A](default: => B): B =
      fold(_ => default, identity)

    def getOrElse[B >: A](f: Seq[String] => B): B =
      fold(f, identity)

    def orElse[B >: A](default: => Validation[B]): Validation[B] =
      fold(_ => default, _ => this)

    def map[B](f: A => B): Validation[B] =
      fold(errors => Invalid(errors), value => Valid(f(value)))

    def combine[B](that: Validation[B]): Validation[B] =
      fold(
        errors => that.fold(
          errs => Invalid(errors ++ errs),
          _ => Invalid(errors)),
        _ => that)

    def +[B](that: Validation[B]): Validation[B] =
      combine(that)

    def flatMap[B](f: A => Validation[B]): Validation[B] =
      combine(fold(_ => f(null.asInstanceOf[A]), f))

    def exists(predicate: A => Boolean): Boolean =
      fold(_ => false, predicate)

    def forall(predicate: A => Boolean): Boolean =
      fold(_ => true, predicate)

    def toList: List[A] =
      fold(_ => Nil, List(_))

    def toOption: Option[A] =
      fold(_ => None, Some.apply)

    def toTry: Try[A] =
      fold(errors => Failure(new NoSuchElementException(errors.mkString(", "))), Success.apply)

    def toEither: Either[Seq[String], A] =
      fold(Left.apply, Right.apply)
  }

  case class Valid[A](value: A) extends Validation[A]

  case class Invalid[A](errors: Seq[String]) extends Validation[A]

  object Validation {
    def apply[A](value: => A, error: Throwable => String = _.getMessage): Validation[A] =
      Try(value) match {
        case Success(v) => Valid(v)
        case Failure(e) => e match {
          case _: NullPointerException => Invalid()
          case _ => Invalid(error(e))
        }
      }

    def apply[A](value: => A, message: String): Validation[A] =
      apply(value, _ => message)

    def sequence[A](seq: Seq[Validation[A]]): Validation[Seq[A]] =
      if (seq.forall(_.isValid)) Valid(seq.map(_.get))
      else Invalid(seq.collect { case Invalid(errors) => errors }.flatten)
  }

  object Invalid {
    def apply[A](errors: String*): Validation[A] =
      Invalid(errors)
  }

}
