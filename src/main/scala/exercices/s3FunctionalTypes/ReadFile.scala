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
  def parseFile(lines: Seq[String]): Seq[Map[Header, String]] =
    lines match {
      case Seq(head, tail@_*) =>
        val headers = head.split(",")
        tail.map { line =>
          headers.zip(line.split(",")).toMap
        }
      case _ => Seq()
    }

  case class User(id: Int, firstName: String, lastName: String, email: String, gender: String, ip: Option[String])

  // transforme les données d'une ligne de csv en case class User
  def formatLine(line: Map[Header, String]): Try[User] = {
    Try(User(
      id = line("id").toInt,
      firstName = line("first_name"),
      lastName = line("last_name"),
      email = line("email"),
      gender = line("gender"),
      ip = line.get("ip_address").filter(_.nonEmpty)))
  }

  // combine les méthodes précédentes pour obtenir les résultats et erreurs
  def loadData(path: String): Try[(Seq[User], Seq[(Int, Throwable)])] = {
    readFile(path).map { lines =>
      val users: Seq[Try[User]] = parseFile(lines).map(formatLine)
      val usersWithLines: Seq[(Try[User], Int)] = users.zipWithIndex

      // two pass way
      val success: Seq[User] = usersWithLines
        .collect { case (Success(user), _) => user }
      val failures: Seq[(Int, Throwable)] = usersWithLines
        .collect { case (Failure(e), i) => (i + 1, e) }

      // one pass way
      val init = (Seq[User](), Seq[(Int, Throwable)]())
      val res = usersWithLines
        .foldLeft(init)((acc, cur) => {
          cur match {
            case (Success(user), _) => (user +: acc._1, acc._2)
            case (Failure(e), i) => (acc._1, (i + 1, e) +: acc._2)
          }
        })

      // imperative way
      val usrs = scala.collection.mutable.ArrayBuffer[User]()
      val errs = scala.collection.mutable.ArrayBuffer[(Int, Throwable)]()
      for (u <- usersWithLines) {
        if (u._1.isFailure) {
          errs += u._2 + 1 -> u._1.failed.get
        } else {
          usrs += u._1.get
        }
      }

      (success, failures)
    }
  }
}
