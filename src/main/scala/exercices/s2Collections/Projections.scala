package exercices.s2Collections

import java.util.Date

import shapeless.tag
import shapeless.tag.@@

import scala.util.Try

object Projections {

  trait EventIdTag

  type EventId = String @@ EventIdTag

  trait PlayerIdTag

  type PlayerId = String @@ PlayerIdTag

  trait QuizIdTag

  type QuizId = String @@ QuizIdTag

  trait GameIdTag

  type GameId = String @@ GameIdTag

  trait QuestionIdTag

  type QuestionId = String @@ QuestionIdTag

  sealed trait Event {
    val id: EventId
    val timestamp: Date
  }

  case class PlayerHasRegisteredPayload(player_id: PlayerId, last_name: String, first_name: String)

  case class PlayerHasRegistered(id: EventId, timestamp: Date, payload: PlayerHasRegisteredPayload) extends Event

  case class QuizWasCreatedPayload(quiz_id: QuizId, quiz_title: String, owner_id: PlayerId)

  case class QuizWasCreated(id: EventId, timestamp: Date, payload: QuizWasCreatedPayload) extends Event

  case class QuestionAddedToQuizPayload(quiz_id: QuizId, question_id: QuestionId, question: String, answer: String)

  case class QuestionAddedToQuiz(id: EventId, timestamp: Date, payload: QuestionAddedToQuizPayload) extends Event

  case class QuestionWasCompletedPayload(game_id: GameId, question_id: QuestionId)

  case class QuestionWasCompleted(id: EventId, timestamp: Date, payload: QuestionWasCompletedPayload) extends Event

  case class QuizWasPublishedPayload(quiz_id: QuizId)

  case class QuizWasPublished(id: EventId, timestamp: Date, payload: QuizWasPublishedPayload) extends Event

  case class GameWasOpenedPayload(quiz_id: QuizId, game_id: GameId)

  case class GameWasOpened(id: EventId, timestamp: Date, payload: GameWasOpenedPayload) extends Event

  case class PlayerJoinedGamePayload(game_id: GameId, player_id: PlayerId)

  case class PlayerJoinedGame(id: EventId, timestamp: Date, payload: PlayerJoinedGamePayload) extends Event

  case class GameWasStartedPayload(game_id: GameId)

  case class GameWasStarted(id: EventId, timestamp: Date, payload: GameWasStartedPayload) extends Event

  case class QuestionWasAskedPayload(game_id: GameId, question_id: QuestionId)

  case class QuestionWasAsked(id: EventId, timestamp: Date, payload: QuestionWasAskedPayload) extends Event

  case class AnswerWasGivenPayload(game_id: GameId, question_id: QuestionId, player_id: PlayerId, answer: String)

  case class AnswerWasGiven(id: EventId, timestamp: Date, payload: AnswerWasGivenPayload) extends Event

  case class TimerHasExpiredPayload(game_id: GameId, question_id: QuestionId, player_id: PlayerId)

  case class TimerHasExpired(id: EventId, timestamp: Date, payload: TimerHasExpiredPayload) extends Event

  case class GameWasCancelledPayload(game_id: GameId)

  case class GameWasCancelled(id: EventId, timestamp: Date, payload: GameWasCancelledPayload) extends Event

  case class GameWasFinishedPayload(game_id: GameId)

  case class GameWasFinished(id: EventId, timestamp: Date, payload: GameWasFinishedPayload) extends Event


  def numberOfEvents(events: Seq[Event]): Int = ???

  def registredPlayers(events: Seq[Event]): Int = ???

  def registredPlayersPerMonth(events: Seq[Event]): Map[String, Int] = ???

  def popularQuizs(events: Seq[Event]): Seq[(QuizId, String, Int)] = ???

  def inactivePlayers(events: Seq[Event], date: Date): Seq[(PlayerId, String, Int)] = ???

  def activePlayers(events: Seq[Event], date: Date): Seq[(PlayerId, String, Int)] = ???

  /**
    * Utilities
    */

  def loadData(file: String): Try[Seq[Event]] = {
    import julienrf.json.derived
    import play.api.libs.json._

    import scala.io.Source

    def readFile(path: String): Try[String] =
      Try(Source.fromFile(path).mkString)

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

    val path = s"src/main/resources/projections/$file"
    for {
      content <- readFile(path)
      events <- Try(Json.parse(content).as[Seq[Event]])
    } yield events
  }
}
