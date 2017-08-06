package exercices.s4FunctionalProgramming

object JsonAdt {

  sealed trait JsValue {
    def stringify: String
  }

  object JsValue {
    def from(in: Any): Option[JsValue] = in match {
      case null => Some(JsNull)
      case b: Boolean => Some(JsBoolean(b))
      case i: Int => Some(JsNumber(i))
      case l: Long => Some(JsNumber(l))
      case f: Float => Some(JsNumber(f))
      case d: Double => Some(JsNumber(d))
      case s: String => Some(JsString(s))
      case m: Map[String, Any] => Some(JsObject(m.flatMap { case (key, value) => from(value).map(r => (key, r)) }))
      case s: Seq[Any] => s.headOption match {
        case Some((_: String, _: Any)) => Some(JsObject(s.asInstanceOf[Seq[(String, Any)]].flatMap { case (key, value) => from(value).map(r => (key, r)) }))
        case Some(_: Any) => Some(JsArray(s.flatMap(from)))
        case None => Some(JsArray())
      }
      case Some(value) => from(value)
      case None => Some(JsNull)
      case _ => None
    }
  }

  case object JsNull extends JsValue {
    def stringify: String = "null"
  }

  case class JsBoolean(underlying: Boolean) extends JsValue {
    def stringify: String = s"$underlying"
  }

  case class JsNumber(underlying: Double) extends JsValue {
    def stringify: String = if (math.round(underlying).toDouble == underlying) s"${math.round(underlying)}" else s"$underlying"
  }

  case class JsString(underlying: String) extends JsValue {
    def stringify: String = "\"" + underlying + "\""
  }

  case class JsArray(underlying: Seq[JsValue]) extends JsValue {
    def stringify: String = underlying.map(_.stringify).mkString("[", ",", "]")
  }

  object JsArray {
    def apply(): JsArray = JsArray(Seq())

    def apply(v: JsValue): JsArray = JsArray(Seq(v))

    def apply(v: JsValue, o: JsValue*): JsArray = JsArray(Seq(v) ++ o)
  }

  case class JsObject(underlying: Seq[(String, JsValue)]) extends JsValue {
    def stringify: String = underlying.map { case (key, value) => "\"" + key + "\"" + s":${value.stringify}" }.mkString("{", ",", "}")
  }

  object JsObject {
    def apply(map: Map[String, JsValue]): JsObject = JsObject(map.toSeq)

    def apply(): JsObject = JsObject(Seq())

    def apply(v: (String, JsValue)): JsObject = JsObject(Seq(v))

    def apply(v: (String, JsValue), o: (String, JsValue)*): JsObject = JsObject(Seq(v) ++ o)
  }

}
