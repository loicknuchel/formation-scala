package exercices.s2Collections

import java.text.SimpleDateFormat
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


  def numberOfEvents(events: Seq[Event]): Int =
    events.length

  def registredPlayers(events: Seq[Event]): Int =
    events.collect { case e: PlayerHasRegistered => e }.length

  def registredPlayersPerMonth(events: Seq[Event]): Map[String, Int] =
    events
      .collect { case e: PlayerHasRegistered => e }
      .groupBy(getMonth)
      .mapValues(_.length)

  def popularQuizs(events: Seq[Event]): Seq[(QuizId, String, Int)] = {
    def quizName(quizs: Map[QuizId, Seq[QuizWasCreated]], id: QuizId): Option[String] =
      quizs.get(id).flatMap(_.headOption).map(_.payload.quiz_title)

    val createdQuizs = events.collect { case e: QuizWasCreated => e }.groupBy(_.payload.quiz_id)
    val startedGames = events.collect { case e: GameWasStarted => e }.groupBy(_.payload.game_id)
    events
      .collect { case e: GameWasOpened => e }
      .filter(e => startedGames.get(e.payload.game_id).isDefined)
      .groupBy(_.payload.quiz_id)
      .map { case (quiz_id, games) => (quiz_id, quizName(createdQuizs, quiz_id).getOrElse(""), games.length) }
      .toSeq
      .sortBy(e => (-e._3, e._2, e._1.toString))
      .take(10)
  }

  def inactivePlayers(events: Seq[Event], date: Date): Seq[(PlayerId, String, Int)] = {
    def playerName(players: Map[PlayerId, Seq[PlayerHasRegistered]], id: PlayerId): Option[String] =
      players.get(id).flatMap(_.headOption).map(p => p.payload.first_name + " " + p.payload.last_name)

    val registeredPlayers = events.collect { case e: PlayerHasRegistered => e }.groupBy(_.payload.player_id)
    val month = getMonth(date)
    events
      .collect { case e: PlayerJoinedGame if getMonth(e) == month => e }
      .groupBy(_.payload.player_id)
      .map { case (playerId, joinedGames) => (playerId, playerName(registeredPlayers, playerId).getOrElse(""), joinedGames.length) }
      .toSeq
      .sortBy(e => (e._3, e._2, e._1.toString))
      .take(10)
  }

  def activePlayers(events: Seq[Event], date: Date): Seq[(PlayerId, String, Int)] = {
    def playerName(players: Map[PlayerId, Seq[PlayerHasRegistered]], id: PlayerId): Option[String] =
      players.get(id).flatMap(_.headOption).map(p => p.payload.first_name + " " + p.payload.last_name)

    val registeredPlayers = events.collect { case e: PlayerHasRegistered => e }.groupBy(_.payload.player_id)
    val deadline = addDays(date, -7)
    events
      .collect { case e: PlayerJoinedGame if e.timestamp.after(deadline) && e.timestamp.before(date) => e }
      .groupBy(_.payload.player_id)
      .map { case (playerId, joinedGames) => (playerId, playerName(registeredPlayers, playerId).getOrElse(""), joinedGames.length) }
      .toSeq
      .sortBy(e => (-e._3, e._2, e._1.toString))
      .take(10)
  }

  val dayFormat = new SimpleDateFormat("yyyy-MM-dd")
  val monthFormat = new SimpleDateFormat("yyyy-MM")

  def getMonth(d: Date): String = monthFormat.format(d)

  def getMonth(e: Event): String = getMonth(e.timestamp)

  def addDays(date: Date, days: Int): Date = {
    import java.util.Calendar
    val cal: Calendar = Calendar.getInstance
    cal.setTime(date)
    cal.add(Calendar.DATE, days)
    cal.getTime
  }

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
