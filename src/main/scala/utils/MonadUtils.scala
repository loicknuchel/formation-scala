package utils

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object MonadUtils {

  implicit class OptionExtension[A](o: Option[A]) {
    def toTry(e: => Exception): Try[A] =
      o match {
        case Some(value) => Success(value)
        case None => Failure(e)
      }

    def toFuture(e: => Exception): Future[A] =
      o match {
        case Some(value) => Future.successful(value)
        case None => Future.failed(e)
      }

    def toEither[B](left: => B): Either[B, A] =
      o match {
        case Some(value) => Right(value)
        case None => Left(left)
      }
  }

  implicit class TryExtension[A](t: Try[A]) {
    def toFuture: Future[A] =
      t match {
        case Success(value) => Future.successful(value)
        case Failure(err) => Future.failed(err)
      }

    def toEither[B](left: Throwable => B): Either[B, A] =
      t match {
        case Success(value) => Right(value)
        case Failure(err) => Left(left(err))
      }

    def toEither: Either[Throwable, A] =
      toEither(identity)
  }

  implicit class EitherSeqExtension[A, B](e: Either[Seq[B], A]) {
    def toFuture: Future[A] =
      e match {
        case Right(value) => Future.successful(value)
        case Left(errors) => Future.failed(new NoSuchElementException(errors.mkString(", ")))
      }

    def get: A =
      e match {
        case Right(value) => value
        case Left(errors) => throw new NoSuchElementException(errors.mkString(", "))
      }
  }

}
