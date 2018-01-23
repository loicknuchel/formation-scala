package project.searchengine

import scala.util.Try

object SearchEngine {
  /**
    * Code ici le moteur un moteur de recherche.
    *   - étape 1: code un simple fonction qui cherche une requête dans un ensemble de documents
    *       def search(db: Seq[String], query: String): Seq[String]
    *   - étape 2: permet une recherche multi-mots, c'est à dire chercher les mots de la requête indépendemment les uns des autres
    *   - étape 3: crée une fonction de tokenization pour comparer les tokens présents dans la requête et les documents
    *   - étape 4: implémente une recherche pour un type générique A en utilisant un adapteur
    *       def search[A](db: Seq[A], query: String, adapter: A => Seq[String]): Seq[A]
    *   - étape 5: améliore la pertinence de la recherche en utilisant l'algorithme TFIDF
    *   - étape 6: attention aux performances !!!
    */

  /**
    *     ---------- Ne pas modifier ----------
    * Fonction utilitaire pour charger les données.
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
