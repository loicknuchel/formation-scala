package exercices.s3FunctionalTypes

import scala.io.Source
import scala.util.{Failure, Success, Try}

object ReadFile {
  type Header = String

  def readFile(path: String): Try[Seq[String]] = ???

  def parseFile(path: String): Try[Seq[Map[Header, String]]] = ???

  case class User(id: Int, firstName: String, lastName: String, email: String, gender: String, ip: Option[String])

  def formatLine(line: Map[Header, String]): Try[User] = ???

  def formatFile(path: String): (Seq[User], Seq[(Int, Throwable)]) = ???
}
