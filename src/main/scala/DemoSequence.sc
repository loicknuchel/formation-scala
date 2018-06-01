import scala.util.{Failure, Success, Try}

def sequence[A](in: Seq[Try[A]]): Try[Seq[A]] =
  Try(in.map(_.get))

class MultiException(val exceptions: Seq[Throwable]) extends Exception

def sequence2[A](in: Seq[Try[A]]): Try[Seq[A]] =
  in.foldLeft(Try(Seq[A]())) { (acc, cur) =>
    (acc, cur) match {
      case (Success(seq), Success(elt)) => Success(seq :+ elt)
      case (Success(_), Failure(e)) => Failure(new MultiException(Seq(e)))
      case (Failure(err), Success(_)) => Failure(err)
      // TODO case (Failure(err), Failure(e)) => Failure(new MultiException(err +: e))
    }
  }