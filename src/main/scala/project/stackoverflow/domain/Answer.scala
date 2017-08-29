package project.stackoverflow.domain

case class Answer(answer_id: AnswerId,
                  question_id: QuestionId,
                  body: String,
                  is_accepted: Boolean,
                  owner: UserEmbed,
                  score: Int,
                  creation_date: Timestamp,
                  last_activity_date: Timestamp) extends Entity {
  val id: String = answer_id.toString
}
