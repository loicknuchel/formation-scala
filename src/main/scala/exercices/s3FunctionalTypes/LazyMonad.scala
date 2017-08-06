package exercices.s3FunctionalTypes

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

object LazyMonad {

  class Lazy[+A](block: => A) {
    private var evaluated = false
    private lazy val underlying: A = {
      evaluated = true
      block
    }

    def get: A = underlying

    def getOrElse[B >: A](default: => B): B = asTry.getOrElse(default)

    def orElse[B >: A](default: => Lazy[B]): Lazy[B] = Lazy(asTry.orElse(default.asTry).get)

    def isEvaluated: Boolean = evaluated

    def map[B](f: A => B): Lazy[B] = Lazy(f(underlying))

    def flatMap[B](f: A => Lazy[B]): Lazy[B] = Lazy(f(underlying).get)

    def filter(p: A => Boolean): Lazy[A] = Lazy(if (p(underlying)) underlying else throw new NoSuchElementException("filtered Lazy"))

    def withFilter(p: A => Boolean): Lazy[A] = filter(p)

    def flatten[B](implicit ev: A <:< Lazy[B]): Lazy[B] = Lazy(ev(underlying).get)

    def collect[B](pf: PartialFunction[A, B]): Lazy[B] = Lazy(pf.applyOrElse(underlying, (v: A) => throw new NoSuchElementException("Lazy.collect not defined at " + v)))

    def compose[B](o: Lazy[B]): Lazy[A] = Lazy({
      o.get
      underlying
    })

    def sequence[B](o: Lazy[B]): Lazy[B] = Lazy({
      underlying
      o.get
    })

    def asOption: Option[A] = asTry.toOption

    def asTry: Try[A] = Try(underlying)

    def asEither: Either[Throwable, A] = asTry.toEither

    def asFuture(implicit ec: ExecutionContext): Future[A] = Future(underlying)(ec)

    override def toString: String =
      if (isEvaluated) s"Lazy($underlying)"
      else "Lazy(not evaluated)"
  }

  object Lazy {
    def apply[A](block: => A): Lazy[A] = new Lazy(block)

    def lazily[A](f: => A): Lazy[A] = new Lazy(f)

    def sequence[A](in: Seq[Lazy[A]]): Lazy[Seq[A]] = Lazy(in.map(_.get))

    //implicit def eval[A](lazy: Lazy[A]): A = lazy.get
  }

}
