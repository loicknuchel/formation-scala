package utils

import io.circe.{Decoder, parser}

import scala.util.Try

object Converters {
  def parseJson[T](json: String)(implicit decoder: Decoder[T]): Try[T] =
    parser.decode[T](json).toTry
}
