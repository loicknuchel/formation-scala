package project.projections

import shapeless.tag.@@

package object domain {
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
}
