package exercices.s3FunctionalTypes

import scala.io.Source
import scala.util.{Failure, Success, Try}

object ReadFile {
  type Header = String

  def readFile(path: String): Try[Seq[String]] =
    Try(Source.fromFile(path).getLines().toSeq)

  def parseFile(path: String): Try[Seq[Map[Header, String]]] = {
    readFile(path).map { lines: Seq[String] =>
      val headers: Seq[Header] = lines.head.split(",")
      //.zipWithIndex.map { case (h, i) => s"$i-$h" }
      val data: Seq[String] = lines.tail
      data.map { line =>
        val values = line.split(",")
        headers.zip(values).toMap
      }
    }
  }

  case class User(id: Int, firstName: String, lastName: String, email: String, gender: String, ip: Option[String])

  def formatLine(line: Map[Header, String]): Try[User] =
    Try(User(
      id = line("id").toInt,
      firstName = line("first_name"),
      lastName = line("last_name"),
      email = line("email"),
      gender = line("gender"),
      ip = line.get("ip_address").filter(_.nonEmpty)))

  def formatFile(path: String): (Seq[User], Seq[(Int, Throwable)]) = {
    val file: Try[Seq[Map[Header, String]]] = parseFile(path)
    val lines: Seq[(Map[Header, String], Int)] = file.getOrElse(Seq()).zipWithIndex
    val formatedLines: Seq[(Try[User], Int)] = lines.map { case (line, i) => (formatLine(line), i) }
    val users: Seq[User] = formatedLines.collect { case (Success(user), _) => user }
    val errors: Seq[(Int, Throwable)] = formatedLines.collect { case (Failure(e), i) => (i + 1, e) }
    (users, errors)

    val f = parseFile(path)
      .getOrElse(Seq())
      .zipWithIndex
      .map { case (line, i) => (formatLine(line), i) }
    (
      f.collect { case (Success(user), _) => user },
      f.collect { case (Failure(e), i) => (i + 1, e) }
    )
  }
}
