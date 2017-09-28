package exercices.s4FunctionalProgramming

import java.sql.Connection

import scala.util.{Failure, Random, Success, Try}

object GetConnectionRefactoring {
  /**
    * Spec: get a connection from an url
    *
    * Problems:
    *   - do not open more than one successful connection
    *   - use functional style
    */
  def connect(urls: Seq[String]): Option[Connection] = {
    Random.shuffle(urls).find { url =>
      getConnection(url) match {
        case Success(connection) =>
          connection.close()
          true
        case Failure(_) =>
          false
      }
    } match {
      case Some(url) => getConnection(url).toOption
      case _ => None
    }
  }

  private def getConnection(url: String): Try[Connection] = {
    ???
  }
}
