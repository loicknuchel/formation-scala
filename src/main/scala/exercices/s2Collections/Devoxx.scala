package exercices.s2Collections

import java.util.Date

import scala.util.Try

// cf http://cfp.devoxx.fr/api
object Devoxx {
  type TalkId = String
  type SpeakerId = String
  type SlotId = String
  type RoomId = String

  case class Talk(id: TalkId, lang: String, title: String, summary: String, speakers: Seq[SpeakerId])

  case class Speaker(id: SpeakerId, name: String, bio: String, lang: String, talks: Seq[TalkId])

  case class Slot(id: SlotId, room: RoomId, start: Date, end: Date, talk: TalkId)

  case class Room(id: RoomId, name: String)

  def frenchTalkPercentage(talks: Seq[Talk]): Double =
    (talks.count(_.lang == "fr"): Double) / talks.length

  def talksOfSpeaker(talks: Seq[Talk], id: SpeakerId): Seq[Talk] =
    talks.filter(talk => talk.speakers.contains(id))

  def roomSchedule(slots: Seq[Slot], talks: Seq[Talk], id: RoomId): Seq[(Date, Date, Talk)] =
    slots
      .filter(_.room == id)
      .flatMap(slot => talks.find(_.id == slot.talk).map(talk => (slot.start, slot.end, talk)))
      //.map(slot => (slot.start, slot.end, talks.find(_.id == slot.talk)))
      //.collect { case (start, end, Some(talk)) => (start, end, talk) }

      //.filter(_._3.isDefined)
      //.map { case (start, end, talkOpt) => (start, end, talkOpt.get) }

  def isSpeaking(slots: Seq[Slot], talks: Seq[Talk], rooms: Seq[Room], id: SpeakerId, time: Date): Option[Room] = ???

  /**
    * Utilities
    */

  def loadData(): Try[(Seq[Talk], Seq[Speaker], Seq[Slot], Seq[Room])] = {
    import java.io.File

    import io.circe.generic.auto._
    import io.circe.{Decoder, parser}

    import scala.io.Source
    import scala.util.Try

    type BreakId = String
    case class DevoxxLink(title: String, href: String, rel: String)
    case class DevoxxLinkWithName(name: String, link: DevoxxLink)
    case class DevoxxTalk(id: TalkId, talkType: String, trackId: String, track: String, lang: String, title: String, summary: String, summaryAsHtml: String, speakers: Seq[DevoxxLinkWithName])
    case class DevoxxAcceptedTalk(id: TalkId, talkType: String, track: String, title: String, links: Seq[DevoxxLink])
    case class DevoxxSpeaker(uuid: SpeakerId, firstName: String, lastName: String, bio: Option[String], bioAsHtml: Option[String], avatarURL: Option[String], company: Option[String], blog: Option[String], twitter: Option[String], lang: Option[String], acceptedTalks: Option[Seq[DevoxxAcceptedTalk]])
    case class DevoxxRoomList(content: String, rooms: Seq[DevoxxRoom])
    case class DevoxxRoom(id: RoomId, name: String, setup: String, capacity: Int, recorded: Option[String])
    case class DevoxxBreak(id: BreakId, nameFR: String, nameEN: String, room: DevoxxRoom)
    case class DevoxxSchedule(slots: Seq[DevoxxSlot])
    case class DevoxxSlot(slotId: SlotId, day: String, roomId: String, roomName: String, roomSetup: String, roomCapacity: Int, fromTime: String, fromTimeMillis: Long, toTime: String, toTimeMillis: Long, notAllocated: Boolean, break: Option[DevoxxBreak], talk: Option[DevoxxTalk])

    def listFiles(folder: String): Try[Seq[String]] =
      Try(new File(folder).listFiles.toSeq.map(_.getPath))

    def readFile(path: String): Try[String] =
      Try(Source.fromFile(path).mkString)

    def readEntity[A](path: String)(implicit decoder: Decoder[A]): Try[A] =
      readFile(path).flatMap { file => parser.decode[A](file).toTry }

    def readEntities[A](path: String)(implicit decoder: Decoder[A]): Try[Seq[A]] =
      listFiles(path).map(_.filter(_.endsWith(".json")))
        .flatMap { paths => Try(paths.map(path => readEntity(path).get)) }

    def asTalk(t: DevoxxTalk): Talk =
      Talk(t.id, t.lang, t.title, t.summary, t.speakers.map(_.link.href.split("/").last))

    def asSpeaker(s: DevoxxSpeaker): Speaker =
      Speaker(s.uuid, s.firstName + " " + s.lastName, s.bio.getOrElse(""), s.lang.getOrElse(""), s.acceptedTalks.getOrElse(Seq()).map(_.id))

    def asSlot(s: DevoxxSlot): Option[Slot] =
      s.talk.map(t => Slot(s.slotId, s.roomId, new Date(s.fromTimeMillis), new Date(s.toTimeMillis), t.id))

    def asRoom(r: DevoxxRoom): Room =
      Room(r.id, r.name)

    val path = "src/main/resources/devoxx"
    for {
      talks <- readEntities[DevoxxTalk](s"$path/talks").map(_.map(asTalk))
      speakers <- readEntities[DevoxxSpeaker](s"$path/speakers").map(_.map(asSpeaker))
      slots <- readEntities[DevoxxSchedule](s"$path/schedules").map(_.flatMap(_.slots).flatMap(asSlot))
      rooms <- readEntity[DevoxxRoomList](s"$path/rooms.json").map(_.rooms.map(asRoom))
    } yield (talks, speakers, slots, rooms)
  }
}
