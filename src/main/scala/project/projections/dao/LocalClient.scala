package project.projections.dao

import project.projections.domain._

import scala.io.Source
import scala.util.Try
import julienrf.json.derived
import play.api.libs.json._
import shapeless.tag
import shapeless.tag.@@

object LocalClient {
  implicit def taggedStringFormat[T]: Format[String @@ T] = new Format[String @@ T] {
    def reads(json: JsValue): JsResult[String @@ T] = json match {
      case JsString(v) => JsSuccess(tag[T](v))
      case unknown => JsError(s"String value expected, got: $unknown")
    }
    def writes(v: String @@ T): JsValue = JsString(v.toString)
  }
  implicit val formatPlayerHasRegisteredPayload: Format[PlayerHasRegisteredPayload] = Json.format[PlayerHasRegisteredPayload]
  implicit val formatQuizWasCreatedPayload: Format[QuizWasCreatedPayload] = Json.format[QuizWasCreatedPayload]
  implicit val formatQuestionAddedToQuizPayload: Format[QuestionAddedToQuizPayload] = Json.format[QuestionAddedToQuizPayload]
  implicit val formatQuestionWasCompletedPayload: Format[QuestionWasCompletedPayload] = Json.format[QuestionWasCompletedPayload]
  implicit val formatQuizWasPublishedPayload: Format[QuizWasPublishedPayload] = Json.format[QuizWasPublishedPayload]
  implicit val formatGameWasOpenedPayload: Format[GameWasOpenedPayload] = Json.format[GameWasOpenedPayload]
  implicit val formatPlayerJoinedGamePayload: Format[PlayerJoinedGamePayload] = Json.format[PlayerJoinedGamePayload]
  implicit val formatGameWasStartedPayload: Format[GameWasStartedPayload] = Json.format[GameWasStartedPayload]
  implicit val formatQuestionWasAskedPayload: Format[QuestionWasAskedPayload] = Json.format[QuestionWasAskedPayload]
  implicit val formatAnswerWasGivenPayload: Format[AnswerWasGivenPayload] = Json.format[AnswerWasGivenPayload]
  implicit val formatTimerHasExpiredPayload: Format[TimerHasExpiredPayload] = Json.format[TimerHasExpiredPayload]
  implicit val formatGameWasCancelledPayload: Format[GameWasCancelledPayload] = Json.format[GameWasCancelledPayload]
  implicit val formatGameWasFinishedPayload: Format[GameWasFinishedPayload] = Json.format[GameWasFinishedPayload]
  implicit val formatEvent: OFormat[Event] = derived.flat.oformat((__ \ "type").format[String])

  def readFile(path: String): String =
    Source.fromFile(path).mkString

  def getEvents(path: String): Try[Seq[Event]] =
    Try(Json.parse(readFile(path)).as[Seq[Event]])
}
