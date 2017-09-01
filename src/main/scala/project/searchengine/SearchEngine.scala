package project.searchengine

import scala.util.Try

object SearchEngine {

  /**
    * Utility for later...
    */

  type UserId = Long
  type UserType = String
  type QuestionId = Long
  type Timestamp = Long
  type Tag = String
  type Link = String

  case class UserEmbed(user_id: UserId, user_type: UserType, display_name: String, profile_image: Link, link: Link, reputation: Int, accept_rate: Option[Int])

  case class Question(question_id: QuestionId, title: String, body: String, link: Link, owner: UserEmbed, is_answered: Boolean, creation_date: Timestamp, last_activity_date: Timestamp, tags: Seq[Tag], score: Int, view_count: Int, answer_count: Int)

  def loadDb(): Try[Seq[Question]] = {
    import java.io.File

    import io.circe.generic.auto._
    import io.circe.{Decoder, parser}

    import scala.io.Source

    def listFiles(folder: String): Try[Seq[String]] =
      Try(new File(folder).listFiles.toSeq.map(_.getPath))

    def readFile(path: String): Try[String] =
      Try(Source.fromFile(path).mkString)

    def readEntity[A](path: String)(implicit decoder: Decoder[A]): Try[A] =
      readFile(path).flatMap { file => parser.decode[A](file).toTry }

    def readEntities[A](path: String)(implicit decoder: Decoder[A]): Try[Seq[A]] =
      listFiles(path).map(_.filter(_.endsWith(".json")))
        .flatMap { paths => Try(paths.map(path => readEntity(path).get)) }

    readEntities[Question]("src/main/resources/stackoverflow/questions")
  }
}
