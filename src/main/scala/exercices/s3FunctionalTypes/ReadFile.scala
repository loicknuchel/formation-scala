package exercices.s3FunctionalTypes

import scala.io.Source
import scala.util.{Failure, Try}

object ReadFile {
  type Header = String

  def readFile(path: String): Try[Seq[String]] =
    Try(Source.fromFile(path).getLines().toList) // .map(s => s.getLines().toSeq)

  def parseFile(path: String): Try[Seq[Map[Header, String]]] = {
    readFile(path).map {
      case head +: tail => {
        val header = head.split(",")
        tail.map { line =>
          header.zip(line.split(",")).toMap
        }
      }
    }
  }

  case class User(id: Int, firstName: String, lastName: String, email: String, gender: String, ip: Option[String])

  def formatLine(line: Map[Header, String]): Try[User] = {
    Try(User(line("id").toInt, line("first_name"), line("last_name"), line("email"), line("gender"), line.get("ip_address").filter(_.nonEmpty)))
  }

  def formatFile(path: String): (Seq[User], Seq[(Int, Throwable)]) = {
    parseFile(path).map { lines =>
      val parsedLines: Seq[Try[User]] = lines
        .map(formatLine)

      val indexedParsedLined: Seq[(Try[User], Int)] = parsedLines
        .zipWithIndex

      val (users: Seq[(Try[User], Int)], errors: Seq[(Try[User], Int)]) =
        indexedParsedLined
          .partition { case (t, _) => t.isSuccess }
      users.map(_._1.get) -> errors.map { case (Failure(t), i) => (i + 1) -> t }
    }.getOrElse(Nil -> Nil)
  }
}
