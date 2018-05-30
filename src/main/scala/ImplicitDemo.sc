trait JSONWriter[T] {
  def write(t: T): String
}

object JSONWriter{
  implicit val IntWriter: JSONWriter[Int] =
    (t: Int) => t.toString

  implicit val DoubleWriter: JSONWriter[Double] =
    (t: Double) => t.toString

  implicit val StringWriter: JSONWriter[String] =
    (t: String) => s""""$t""""

  implicit def listWriter[T](implicit writer: JSONWriter[T]): JSONWriter[List[T]] =
    (l: List[T]) => l.map(JSONWriter.writes(_)).mkString("[", ",", "]")

  def writes[T](t: T)(implicit jSONWriter: JSONWriter[T]) =
    jSONWriter.write(t)
}

case class Toto(name: String, age: Int)
object Toto {
  implicit val TotoWriter: JSONWriter[Toto] =
    (t: Toto) => s"""{"name":${JSONWriter.writes(t.name)},"age":${JSONWriter.writes(t.age)}}"""
}

JSONWriter.writes(1.2)
JSONWriter.writes("a string")
JSONWriter.writes(List(1, 2))
JSONWriter.writes(Toto("mathieu", 30))
