package utils

import scala.util.Try

object TryUtils {
  def sequence[A](in: Seq[Try[A]]): Try[Seq[A]] = Try(in.map(_.get))
}
