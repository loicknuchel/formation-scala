package project.projections.domain

import java.util.Date

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
