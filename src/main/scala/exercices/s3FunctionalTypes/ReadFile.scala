package exercices.s3FunctionalTypes

import scala.io.Source
import scala.util.{Failure, Success, Try}

object ReadFile {
  /**
    * Complète les quatre fonctions avec des ??? pour faire passer les tests
    */
  type Header = String

  // lit un fichier et retourne son contenu ligne à ligne (Seq[String])
  def readFile(path: String): Try[Seq[String]] = ???

  // parse le csv, extrait les headers et combine les avec les données
  def parseFile(lines: Seq[String]): Seq[Map[Header, String]] = ???

  case class User(id: Int, firstName: String, lastName: String, email: String, gender: String, ip: Option[String])

  // transforme les données d'une ligne de csv en case class User
  def formatLine(line: Map[Header, String]): Try[User] = ???

  // combine les méthodes précédentes pour obtenir les résultats et erreurs
  def loadData(path: String): Try[(Seq[User], Seq[(Int, Throwable)])] = ???
}
