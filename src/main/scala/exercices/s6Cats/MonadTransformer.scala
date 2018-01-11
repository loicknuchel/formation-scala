package exercices.s6Cats

import cats.data.EitherT
import cats.implicits._

import scala.concurrent.Future

object MonadTransformer {

  case class Error(code: Int, kind: String, message: String)

  private def acquireToken(): Future[Either[Error, String]] =
    Future.successful(Right("token"))

  private def fetchLocation(token: String, path: String): Future[Either[Error, String]] =
    Future.successful(Right(s"http://sharepoint.com/api$path"))

  private def download(location: String): Future[Either[Error, String]] =
    Future.successful(Right(
      """id;name
        |1;Alice
        |2;Bob
      """.stripMargin.trim))

  def downloadFile(path: String): Future[Either[Error, String]] = {
    ???
  }
}
