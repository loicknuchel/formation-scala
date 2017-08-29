package project.stackoverflow

import io.circe.generic.auto._
import io.circe.{Decoder, parser}
import project.stackoverflow.domain.{Answer, ApiResponse, Comment, Question}
import project.stackoverflow.helpers.{FutureUtils, HttpClient}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// docs https://api.stackexchange.com/docs
class ApiClient(httpClient: HttpClient) {
  private val baseUrl = "https://api.stackexchange.com/2.2"
  private val defaultParams = Map(
    "key" -> "U4DMV*8nvpm3EOpvf69Rxw((",
    "site" -> "stackoverflow",
    "filter" -> "withbody",
    "page" -> "1",
    "pagesize" -> "100",
    "sort" -> "creation",
    "order" -> "desc"
  )

  def lastQuestions(limit: Int, offset: Int = 0): Future[Seq[Question]] = {
    val pagesize = if (limit > 100) 100 else limit
    val firstpage = if (offset == 0) 1 else offset / pagesize

    listAll[Question]("/questions", Map(
      "page" -> firstpage.toString,
      "pagesize" -> pagesize.toString
    ), _.length > limit)
  }

  def answers(id: QuestionId): Future[Seq[Answer]] =
    listAll[Answer](s"/questions/$id/answers")

  def answers(ids: Seq[QuestionId]): Future[Seq[Answer]] =
    batch[QuestionId, Answer](100)(ids => (s"/questions/${ids.mkString(";")}/answers", Map()))(ids)

  def comments(id: AnswerId): Future[Seq[Comment]] =
    listAll[Comment](s"/answers/$id/comments")

  def comments(ids: Seq[AnswerId]): Future[Seq[Comment]] =
    batch[AnswerId, Comment](100)(ids => (s"/answers/${ids.mkString(";")}/comments", Map()))(ids)

  def user(id: UserId): Future[Option[User]] =
    listAll[User](s"/users/$id").map(_.headOption)

  def users(ids: Seq[UserId]): Future[Seq[User]] =
    batch[UserId, User](100)(ids => (s"/users/${ids.mkString(";")}", Map()))(ids)

  private def apiUrl(endpoint: String, params: Map[String, String] = Map()): String =
    baseUrl + endpoint + "?" + (defaultParams ++ params).map { case (key, value) => s"$key=$value" }.mkString("&")

  private def listAll[A](
                          endpoint: String,
                          params: Map[String, String] = Map(),
                          stop: Seq[A] => Boolean = (_: Seq[A]) => false
                        )(implicit decoder: Decoder[A]): Future[Seq[A]] = {
    def internal(page: Int, acc: Seq[A]): Future[Seq[A]] = {
      httpClient.get(apiUrl(endpoint, params ++ Map("page" -> page.toString))).flatMap { response =>
        parser.decode[ApiResponse[A]](response) match {
          case Left(err) =>
            if (acc.isEmpty) {
              Future.failed(err)
            } else {
              println("listAll error: " + err)
              Future.successful(acc)
            }
          case Right(res) =>
            val fetched = acc ++ res.items
            if (res.quota_remaining > 1 && res.has_more && !stop(fetched))
              internal(page + 1, fetched)
            else
              Future.successful(fetched)
        }
      }
    }

    internal(params.getOrElse("page", "1").toInt, Seq())
  }

  private def batch[A, B](size: Int)(buildUrl: Seq[A] => (String, Map[String, String]))(ids: Seq[A])(implicit decoder: Decoder[B]): Future[Seq[B]] = {
    FutureUtils.execSeq(ids.sliding(size, size).toSeq, (block: Seq[A]) => {
      val (endpoint, params) = buildUrl(block)
      listAll[B](endpoint, params)
    }).map(_.flatten)
  }
}
