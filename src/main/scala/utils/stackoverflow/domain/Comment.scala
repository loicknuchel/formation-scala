package utils.stackoverflow.domain

case class Comment(
                    comment_id: CommentId,
                    post_id: AnswerId,
                    body: String,
                    owner: UserEmbed,
                    score: Int,
                    edited: Boolean,
                    creation_date: Timestamp
                  ) extends Entity {
  val id: String = comment_id.toString
}
