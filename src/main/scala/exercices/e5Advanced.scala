package exercices

import scala.language.implicitConversions

object e5Advanced {

    /*object ParametreImplicite {

    import .JSON._

    object Json {

      def from[A](a: A)(implicit writer: Writer[A]): JsValue =
        writer.write(a)

      trait Writer[A] {
        def write(in: A): JsValue
      }

      implicit object NullWriter extends Writer[Null] {
        def write(in: Null): JsValue = JsNull
      }

      implicit object BooleanWriter extends Writer[Boolean] {
        def write(in: Boolean): JsValue = JsBoolean(in)
      }

      implicit object IntWriter extends Writer[Int] {
        def write(in: Int): JsValue = JsNumber(in)
      }

      implicit object LongWriter extends Writer[Long] {
        def write(in: Long): JsValue = JsNumber(in)
      }

      implicit object FloatWriter extends Writer[Float] {
        def write(in: Float): JsValue = JsNumber(in)
      }

      implicit object DoubleWriter extends Writer[Double] {
        def write(in: Double): JsValue = JsNumber(in)
      }

      implicit object StringWriter extends Writer[String] {
        def write(in: String): JsValue = JsString(in)
      }

      implicit def SeqWriter[A: Writer]: Writer[Seq[A]] =
        (in: Seq[A]) => JsArray(in.map(implicitly[Writer[A]].write))

      implicit def MapWriter[A: Writer]: Writer[Map[String, A]] =
        (in: Map[String, A]) => JsObject(in.mapValues(implicitly[Writer[A]].write))

      implicit def OptionWriter[A: Writer]: Writer[Option[A]] =
        (in: Option[A]) => in.map(implicitly[Writer[A]].write).getOrElse(JsNull)

    }

    case class User(id: Long, name: String)

    object User {

      import exercices.e5Advanced.ParametreImplicite.Json.Writer

      implicit object UserWriter extends Writer[User] {
        def write(in: User): JsValue = JsObject(Map(
          "id" -> JsNumber(in.id),
          "name" -> JsString(in.name)
        ))
      }

    }

  }

  object ClasseImplicite {

    // User.asJson

  }

  object FonctionImplicite {

    import .JSON._

    object Json {
      implicit def write(in: Null): JsValue = JsNull
      implicit def write(in: Boolean): JsValue = JsBoolean(in)
      implicit def write(in: Int): JsValue = JsNumber(in)
      implicit def write(in: Long): JsValue = JsNumber(in)
      implicit def write(in: Float): JsValue = JsNumber(in)
      implicit def write(in: Double): JsValue = JsNumber(in)
      implicit def write(in: String): JsValue = JsString(in)
      implicit def write(in: Seq[JsValue]): JsValue = JsArray(in)
      implicit def write(in: Map[String, JsValue]): JsValue = JsObject(in)
    }

  }*/

}
