package exercices.web.utils

import com.twitter.{util => twitter}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

object Extensions {

  implicit class TryConverter[T](t: Try[T]) {
    def toFuture: Future[T] = t match {
      case Success(v) => Future.successful(v)
      case Failure(e) => Future.failed(e)
    }

    def toTwitter: twitter.Try[T] = t match {
      case Success(v) => twitter.Return(v)
      case Failure(e) => twitter.Throw(e)
    }
  }

  implicit class FutureConverter[T](f: Future[T]) {
    def toTwitter(implicit ec: ExecutionContext): twitter.Future[T] = {
      val promise = twitter.Promise[T]()
      f.onComplete(t => promise.update(t.toTwitter))
      promise
    }
  }

}
