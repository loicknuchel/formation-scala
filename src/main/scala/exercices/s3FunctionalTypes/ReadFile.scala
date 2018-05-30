package exercices.s3FunctionalTypes

import scala.io.Source
import scala.util.{Failure, Success, Try}

object ReadFile {
  /**
    * Complète les quatre fonctions avec des ??? pour faire passer les tests
    */
  type Header = String

  // lit un fichier et retourne son contenu ligne à ligne (Seq[String])
  def readFile(path: String): Try[Seq[String]] =
    Try(Source.fromFile(path).getLines().toSeq)

  // parse le csv, extrait les headers et combine les avec les données
  def parseFile(lines: Seq[String]): Seq[Map[Header, String]] = {
    if (lines.isEmpty) {
      Seq()
    } else {
      val headers: Array[Header] = lines.head.split(",")
      lines.tail.map { line =>
        val values = line.split(",")
        headers.zip(values).toMap
      }
    }
  }

  case class User(id: Int, firstName: String, lastName: String, email: String, gender: String, ip: Option[String])

  // transforme les données d'une ligne de csv en case class User
  def formatLine(line: Map[Header, String]): Try[User] =
    Try(User(
      id = line("id").toInt,
      firstName = line("first_name"),
      lastName = line("last_name"),
      email = line("email"),
      gender = line("gender"),
      ip = line.get("ip_address").filter(_.nonEmpty)))

  // combine les méthodes précédentes pour obtenir les résultats et erreurs
  def loadData(path: String): Try[(Seq[User], Seq[(Int, Throwable)])] = {
    readFile(path).map(parseFile).map { data =>
      val tryUsers = data.map(formatLine).zipWithIndex

      //val (successUsers, failureUsers) = tryUsers.partition(_._1.isSuccess)

      val users = tryUsers.collect { case (Success(u), _) => u }
      val failures = tryUsers.collect { case (Failure(e), i) => (i + 2, e) }
      (users, failures)
    }
  }
}
