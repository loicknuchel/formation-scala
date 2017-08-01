package slides


import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.{implicitConversions, postfixOps}
import scala.math.Ordering
import scala.util.{Failure, Success, Try}

object s5Advanced {

  /**
    * Ce sont des paramètres, classes voire fonctions
    * qui peuvent être injectés, créées ou exécutées sans qu’on ait besoin des les spécifier.
    *
    * Ils sont choisis parmi ceux se trouvant dans le scope courant et ayant les types correspondant.
    * Si plusieurs implicites correspondent, il y a une priorisation en fonction de la proximité du scope
    * et si une ambiguïté demeure, la compilation échoue.
    *
    * C’est un mécanisme puissant mais pouvant être dangereux. À utiliser avec précaution !
    */
  object ParametreImplicite {
    def tva(price: Double)(implicit tva: Double): Double =
      price * (1 + tva)

    implicit val rate = 0.2
    val a: Double = tva(10) // 12
    val b: Double = tva(10)(0.18) // 11.8


    case class User(id: Int, name: String)

    def exec(url: String)(implicit user: User): Seq[String] =
      Seq(url, user.name)

    implicit val current = User(1, "Loïc")
    val res: Seq[String] = exec("url") // Seq(url, Loïc)
  }

  object ParametreImpliciteUsageReel {

    trait Future[T] {
      def map[S](f: T => S)
                (implicit ec: ExecutionContext): Future[S] = ???
    }

    trait SeqLike[A] {
      def sorted(implicit o: Ordering[A]): SeqLike[A] = ???
    }

    // see http://www.lihaoyi.com/post/ImplicitDesignPatternsinScala.html
  }

  object ClasseImpliciteInt {

    implicit class RichInt(i: Int) {
      def pow(n: Int): Int =
        math.pow(i.toDouble, n.toDouble).toInt
    }

    // appel "normal" de classe
    val a: Int = new RichInt(2).pow(2) // 4

    // classe implicite appelée
    val b: Int = 2.pow(2) // 4
  }

  object ClasseImpliciteTry {

    implicit class TryConverter[T](t: Try[T]) {
      def asFuture: Future[T] = t match {
        case Success(v) => Future.successful(v)
        case Failure(err) => Future.failed(err)
      }
    }

    def asyncOp(): Future[Int] = Future.successful(1)

    def syncOp(): Try[Int] = Success(2)

    val res: Future[Int] = for {
      a <- asyncOp()
      b <- syncOp().asFuture
    } yield a + b

    println("res: " + Await.result(res, 5 seconds))
    // 3
  }

  object FonctionImpliciteInt {

    class RichInt(i: Int) {
      def pow(n: Int): Int =
        math.pow(i.toDouble, n.toDouble).toInt
    }

    implicit def asRichInt(i: Int): RichInt = new RichInt(i)

    // appel "normal" de classe
    val a: Int = new RichInt(2).pow(2)

    // classe implicite appelée
    val b: Int = 2.pow(2)
  }

  object FonctionImplicite {

    case class Id(value: String)

    def fetch(id: Id): Unit = {}

    implicit def stringToId(id: String): Id = Id(id)

    fetch("123")
  }

}
