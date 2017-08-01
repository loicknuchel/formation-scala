package project.stackoverflow.domain

case class Question(
                     question_id: QuestionId,
                     title: String,
                     body: String,
                     link: Link,
                     owner: UserEmbed,
                     is_answered: Boolean,
                     creation_date: Timestamp,
                     last_activity_date: Timestamp,
                     tags: Seq[Tag],
                     score: Int,
                     view_count: Int,
                     answer_count: Int
                   ) extends Entity {
  val id: String = question_id.toString
}

