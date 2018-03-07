import java.util.concurrent.Executors

import akka.dispatch.ExecutionContexts

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

/**
  * Une worksheet permet d'expérimenter avec le code scala.
  * Le résultat de chaque ligne sera affiché dans la partie de droite
  * et l'ensemble du code recompilé et exécuté à chaque modification :)
  *
  * Have fun !
  */
// val ex = "example"

/*val si: Iterator[Seq[Int]] = Seq(1, 2, 3, 4).sliding(2, 1)

si.foreach{println}

val s2: Iterator[Seq[Int]] = Seq(1, 2, 3, 4).sliding(2, 2)

s2.foreach{println}

val arr = 1 to 100
//val Seq(a, b, c, d, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, e @ _*) = arr

val map = Map("a" -> 1)
map("b")*/

// import scala.concurrent.ExecutionContext.Implicits.global
implicit val customContext =
  ExecutionContext.fromExecutor(Executors.newCachedThreadPool())

val a = Future.successful(1)

val b = a.map(r => r+2)

println("res: " + Await.result(b, Duration.Inf))
