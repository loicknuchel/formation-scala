package utils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FutureUtils {
  def execPar[A, B](items: Seq[A], f: A => Future[B]): Future[Seq[B]] = {
    Future.sequence(items.map(f))
  }

  def execSeq[A, B](items: Seq[A], f: A => Future[B]): Future[Seq[B]] = {
    def internal(from: Seq[A], to: Seq[B]): Future[Seq[B]] = {
      from match {
        case Seq() => Future.successful(to)
        case Seq(head, tail@_*) => f(head).flatMap { result =>
          internal(tail, to :+ result)
        }
      }
    }

    internal(items, Seq())
  }

  def execBatch[A, B](items: Seq[A], f: A => Future[B], size: Int = 10): Future[Seq[B]] = {
    execSeq(
      items.grouped(size).toSeq,
      (group: Seq[A]) => execPar(group, f)
    ).map(_.flatten)
  }
}
