package project.stackoverflow.helpers

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.coding.{Deflate, Gzip, NoCoding}
import akka.http.scaladsl.model.headers.HttpEncodings
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

trait HttpClient {
  def get(url: String): Future[String]
}

class RealHttpClient() extends HttpClient {
  private implicit val system = ActorSystem()
  private implicit val materializer = ActorMaterializer()

  private def decodeResponse(response: HttpResponse): HttpResponse = {
    response.encoding match {
      case HttpEncodings.gzip => Gzip.decodeMessage(response)
      case HttpEncodings.deflate => Deflate.decodeMessage(response)
      case HttpEncodings.identity => NoCoding.decodeMessage(response)
      case _ => response
    }
  }

  def get(url: String): Future[String] = {
    println("GET " + url)
    Http().singleRequest(HttpRequest(uri = url))
      .map(decodeResponse)
      .flatMap(_.entity.toStrict(300.millis).map(_.data.utf8String))
  }
}
