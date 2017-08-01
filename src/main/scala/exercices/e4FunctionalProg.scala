package exercices

import java.text.SimpleDateFormat
import java.util.Date

import project.projections.domain._

object e4FunctionalProg {

  object JSON {

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

  object Projections {
    def numberOfEvents(events: Seq[Event]): Int =
      events.length

    def registredPlayers(events: Seq[Event]): Int =
      events.collect { case e: PlayerHasRegistered => e }.length

    def registredPlayersPerMonth(events: Seq[Event]): Map[String, Int] =
      events
        .collect { case e: PlayerHasRegistered => e }
        .groupBy(getMonth)
        .mapValues(_.length)

    def popularQuizs(events: Seq[Event]): Seq[(QuizId, String, Int)] = {
      def quizName(quizs: Map[QuizId, Seq[QuizWasCreated]], id: QuizId): Option[String] =
        quizs.get(id).flatMap(_.headOption).map(_.payload.quiz_title)

      val createdQuizs = events.collect { case e: QuizWasCreated => e }.groupBy(_.payload.quiz_id)
      val startedGames = events.collect { case e: GameWasStarted => e }.groupBy(_.payload.game_id)
      events
        .collect { case e: GameWasOpened => e }
        .filter(e => startedGames.get(e.payload.game_id).isDefined)
        .groupBy(_.payload.quiz_id)
        .map { case (quiz_id, games) => (quiz_id, quizName(createdQuizs, quiz_id).getOrElse(""), games.length) }
        .toSeq
        .sortBy(e => (-e._3, e._2, e._1.toString))
        .take(10)
    }

    def inactivePlayers(events: Seq[Event], date: Date): Seq[(PlayerId, String, Int)] = {
      def playerName(players: Map[PlayerId, Seq[PlayerHasRegistered]], id: PlayerId): Option[String] =
        players.get(id).flatMap(_.headOption).map(p => p.payload.first_name + " " + p.payload.last_name)

      val registeredPlayers = events.collect { case e: PlayerHasRegistered => e }.groupBy(_.payload.player_id)
      val month = getMonth(date)
      events
        .collect { case e: PlayerJoinedGame if getMonth(e) == month => e }
        .groupBy(_.payload.player_id)
        .map { case (playerId, joinedGames) => (playerId, playerName(registeredPlayers, playerId).getOrElse(""), joinedGames.length) }
        .toSeq
        .sortBy(e => (e._3, e._2, e._1.toString))
        .take(10)
    }

    def activePlayers(events: Seq[Event], date: Date): Seq[(PlayerId, String, Int)] = {
      def playerName(players: Map[PlayerId, Seq[PlayerHasRegistered]], id: PlayerId): Option[String] =
        players.get(id).flatMap(_.headOption).map(p => p.payload.first_name + " " + p.payload.last_name)

      val registeredPlayers = events.collect { case e: PlayerHasRegistered => e }.groupBy(_.payload.player_id)
      val deadline = addDays(date, -7)
      events
        .collect { case e: PlayerJoinedGame if e.timestamp.after(deadline) && e.timestamp.before(date) => e }
        .groupBy(_.payload.player_id)
        .map { case (playerId, joinedGames) => (playerId, playerName(registeredPlayers, playerId).getOrElse(""), joinedGames.length) }
        .toSeq
        .sortBy(e => (-e._3, e._2, e._1.toString))
        .take(10)
    }

    val dayFormat = new SimpleDateFormat("yyyy-MM-dd")
    val monthFormat = new SimpleDateFormat("yyyy-MM")

    def getMonth(d: Date): String = monthFormat.format(d)

    def getMonth(e: Event): String = getMonth(e.timestamp)

    def addDays(date: Date, days: Int): Date = {
      import java.util.Calendar
      val cal: Calendar = Calendar.getInstance
      cal.setTime(date)
      cal.add(Calendar.DATE, days)
      cal.getTime
    }
  }

}
