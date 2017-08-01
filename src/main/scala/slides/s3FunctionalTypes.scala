package slides

import java.io.FileNotFoundException
import java.text.SimpleDateFormat
import java.util.Date

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.util.{Failure, Success, Try}

object s3FunctionalTypes {

  object Philosophie {
    // que dire de :
    def lastExec(): Date = {
      var content: String = null
      try {
        content = Source.fromFile(".lastExec").getLines.mkString
      } catch {
        case e: FileNotFoundException => return null
      }
      var date = new SimpleDateFormat("yyyy-MM-dd").parse(content)
      return date
    }

    // la signature ment : elle dit renvoyer une Date alors qu’elle peut aussi renvoyer "null" ou lancer une exception !
    // beaucoup de chemins d’exécution et de possibilités de sortie
    // utilisation des "var" non nécessaire
    // code pas réutilisable

    def read(path: String): Try[String] =
      Try(Source.fromFile(path).getLines.mkString)

    def parse(str: String, fmt: String): Try[Date] =
      Try(new SimpleDateFormat(fmt).parse(str))

    def lastExec2(): Try[Date] =
      read(".lastExec").flatMap(parse(_, "yyyy-MM-dd"))

    // signatures claire et exhaustive
    // un seul chemin d’exécution
    // immutabilité
    // code réutilisable
    // fonctions (presque) pures
    // séparation du code technique (read/parse) du code métier (lastExec)
    // code concis (3 lignes !)

    // Règles générales :
    //  - ne pas utiliser var
    //  - ne pas utiliser null
    //  - ne pas utiliser return
    //  - ne pas lancer d'exceptions
    //  - Exprimer les intentions et possibilités dans la signature
    //  - Une fonction :
    //    - prends des arguments
    //    - renvoi un résultat à la fin
  }

  object FunctionalTypes {
    /**
      * .List[A]     Option[A]                 Try[A]                          Future[A]             Either[A, B]
      * .  |            |                        |                                |                      |
      * .+-+-+       +--+--+             +-------+--------+              +--------+--------+         +---+---+
      * .|   |       |     |             |                |              |                 |         |       |
      * Nil ::[A]   None Some[A]   Failure[Throwable] Success[A]   Failure[Throwable] Success[A]   Left[A] Right[B]
      */
    // List: pluriel
    // Option: présence ou absence
    // Try: peut échouer
    // Future: asynchrone
    // Either: un type ou l'autre / résultat ou erreur
  }

  object UsageList {
    val list: List[String] = List("foo", "2", "42", "bar")

    val lengths: List[Int] = list.map(_.length) // List(3, 1, 2, 3)
    val chars: List[Char] = list.flatMap(_.toList) // List(f, o, o, 2, 4, 2, b, a, r)
    val nums: List[String] = list.filter(_ matches "\\d+") // List(2, 42)

    val size: Int = list match {
      case head :: tail => head.length
      case Nil => 0
    }
  }

  object UsageOption {
    def toIntOpt(s: String): Option[Int] =
      Try(s.toInt).toOption

    val opt: Option[String] = Some("foo")

    val up: Option[String] = opt.map(_.toUpperCase) // Some(FOO)
    val num: Option[Int] = opt.flatMap(toIntOpt) // None
    val n: Option[String] = opt.filter(_ matches "\\d+") // None

    val res: String = opt match {
      case Some(x) => x
      case None => "not found"
    }
  }

  object UsageTry {
    def toIntTry(s: String): Try[Int] =
      Try(s.toInt)

    val tri: Try[String] = Success("foo")

    val up: Try[String] = tri.map(_.toUpperCase) // Success(FOO)
    val num: Try[Int] = tri.flatMap(toIntTry) // Failure
    val n: Try[String] = tri.filter(_ matches "\\d+") // Failure

    val res: String = tri match {
      case Success(x) => x
      case Failure(e) => e.getMessage
    }
  }

  object UsageFuture {
    def toIntFut(s: String): Future[Int] =
      Try(s.toInt) match {
        case Success(i) => Future.successful(i)
        case Failure(e) => Future.failed(e)
      }

    val fut: Future[String] = Future.successful("foo")

    val up: Future[String] = fut.map(_.toUpperCase) // Success(FOO)
    val num: Future[Int] = fut.flatMap(toIntFut) // Failure
    val n: Future[String] = fut.filter(_ matches "\\d+") // Failure
  }

  object UsageEither {
    def toIntEth(s: String): Either[Int, Int] =
      Try(s.toInt) match {
        case Success(i) => Right(i)
        case Failure(_) => Left(1)
      }

    val eth: Either[Int, String] = Right("foo")

    val up: Either[Int, String] = eth.map(_.toUpperCase) // Right(FOO)
    val num: Either[Int, Int] = eth.flatMap(toIntEth) // Left(1)
    val n: Either[Int, String] = eth.filterOrElse(_ matches "\\d+", 1) // Left(1)

    val res: String = eth match {
      case Right(x) => x
      case Left(err) => err.toString
    }
  }

  object FonctorDef {

    // Définition :
    //  - F[A]: type paramétré
    //  - map[A, B](f: A => B): F[A] => F[B]
    // Propriétés :
    //  - map(identity) == identity
    //  - map(f compose g) == map(f) compose map(g)

    sealed trait May[+A] {
      def map[B](f: A => B): May[B]
    }

    case class Just[A](a: A) extends May[A] {
      override def map[B](f: (A) => B): May[B] = Just(f(a))
    }

    case object Not extends May[Nothing] {
      override def map[B](f: (Nothing) => B): May[B] = Not
    }

  }

  object MonadeDef {

    // Définition :
    //  - M[A]: type paramétré
    //  - pure[A](a: A): M[A]
    //  - flatMap[A, B](f: A => M[B]): M[A] => M[B]
    // Propriétés :
    //  - pure(a).flatMap(f) == f(a) : identité à gauche
    //  - ma.flatMap(pure) == ma     : identité à droite
    //  - ma.flatMap(x => f(x).flatMap(g)) == (ma.flatMap(f)).flatMap(g) : associativité
    // A noter :
    //  - flatMap compose pure = map

    sealed trait May[+A] {
      def flatMap[B](f: A => May[B]): May[B]

      def map[B](f: A => B): May[B] = flatMap(a => May.pure(f(a)))
    }

    object May {
      def pure[A](a: A): May[A] = Just(a)

      def apply[A](a: A): May[A] = if (a == null) Not else Just(a)
    }

    case class Just[A](a: A) extends May[A] {
      override def flatMap[B](f: A => May[B]): May[B] = f(a)
    }

    case object Not extends May[Nothing] {
      override def flatMap[B](f: Nothing => May[B]): May[B] = Not
    }

  }

  object ForComprehention {

    case class User(id: Int)

    case class Talk(title: String, by: Int)

    def currentUser(): Future[Int] = ???

    def getUser(id: Int): Future[User] = ???

    def getTalks(user: User): Future[List[Talk]] = ???

    val titles: Future[List[String]] =
      currentUser().flatMap { id =>
        getUser(id).flatMap { user =>
          getTalks(user).map { talks =>
            talks.map(_.title)
          }
        }
      }

    val titles2: Future[List[String]] =
      for {
        id <- currentUser()
        user <- getUser(id)
        talks <- getTalks(user)
      } yield talks.map(_.title)
  }

}
