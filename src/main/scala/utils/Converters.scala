package utils

import io.circe.{Decoder, parser}

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

object Converters {
  def parseJson[T](json: String)(implicit decoder: Decoder[T]): Try[T] =
    parser.decode[T](json).toTry

  implicit class OptionConverter[T](o: Option[T]) {
    def toTry(e: => Exception): Try[T] = o match {
      case Some(value) => Success(value)
      case None => Failure(e)
    }

    def toFuture(e: => Exception): Future[T] = o match {
      case Some(value) => Future.successful(value)
      case None => Future.failed(e)
    }
  }

  implicit class TryConverter[T](t: Try[T]) {
    def toFuture: Future[T] = t match {
      case Success(value) => Future.successful(value)
      case Failure(err) => Future.failed(err)
    }
  }

}
