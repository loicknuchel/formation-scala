package exercices.web

import com.twitter.finagle.http.{Response, Status}
import com.twitter.util.Future
import com.twitter.{util => twitter}

import scala.util.control.NonFatal

package object controllers {
  def handleError: PartialFunction[Throwable, twitter.Future[Response]] = {
    case NonFatal(e) =>
      val response = Response(Status.InternalServerError)
      response.setContentTypeJson()
      response.write(s"""{"error":"${e.getClass.getSimpleName}","message":"${escape(e.getMessage)}"}""")
      Future.value(response)
  }

  private def escape(in: String): String =
    in.replace("\n", "\\n").replace("\t", "\\t")
}
