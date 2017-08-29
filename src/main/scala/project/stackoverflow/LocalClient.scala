package project.stackoverflow

import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, parser}
import project.stackoverflow.domain.{Answer, Comment, Entity, Question}
import project.stackoverflow.helpers.FileClient.Path
import project.stackoverflow.helpers.{FileClient, TryUtils}

import scala.util.Try

class LocalClient(fileClient: FileClient, datasource: Path) {
  def write(q: Question): Try[Unit] = write("questions")(q)

  def getQuestions(): Try[Seq[Question]] = readAll[Question]("questions")

  def write(a: Answer): Try[Unit] = write("answers")(a)

  def getAnswers(): Try[Seq[Answer]] = readAll[Answer]("answers")

  def write(c: Comment): Try[Unit] = write("comments")(c)

  def getComments(): Try[Seq[Comment]] = readAll[Comment]("comments")

  def write(u: User): Try[Unit] = write("users")(u)

  def getUsers(): Try[Seq[User]] = readAll[User]("users")

  private def write[A <: Entity](path: Path)(a: A)(implicit encoder: Encoder[A]): Try[Unit] =
    fileClient.write(s"$datasource/$path/${a.id}.json", a.asJson.toString)

  private def readAll[A](path: Path)(implicit decoder: Decoder[A]): Try[Seq[A]] = {
    fileClient.listFiles(s"$datasource/$path")
      .map(_.filter(_.endsWith(".json")))
      .flatMap { paths => TryUtils.sequence(paths.map(fileClient.read)) }
      .flatMap { files => TryUtils.sequence(files.map(file => parser.decode[A](file).toTry)) }
  }
}
