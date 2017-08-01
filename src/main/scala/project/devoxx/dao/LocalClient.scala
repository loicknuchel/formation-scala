package project.devoxx.dao

import helpers.Converters._
import helpers.FileClient.Path
import helpers.{FileClient, TryUtils}
import io.circe.generic.auto._
import io.circe.{Decoder, parser}
import project.devoxx.domain._

import scala.util.Try

class LocalClient(fileClient: FileClient, datasource: Path) extends Dao[Try] {
  def getRooms(): Try[Seq[Room]] = read[RoomList](s"rooms.json").map(_.rooms)

  def getRoom(id: RoomId): Try[Room] = getRooms().flatMap(_.find(_.id == id).toTry(new NoSuchElementException))

  def getSpeakers(): Try[Seq[Speaker]] = readAll[Speaker]("speakers")

  def getSpeaker(id: SpeakerId): Try[Speaker] = read[Speaker](s"speakers/$id.json")

  def getSpeaker(link: LinkWithName): Try[Speaker] = getSpeaker(asId(link))

  def getTalks(): Try[Seq[Talk]] = readAll[Talk]("talks")

  def getTalk(id: TalkId): Try[Talk] = read[Talk](s"talks/$id.json")

  def getTalk(link: LinkWithName): Try[Talk] = getTalk(asId(link))

  def getScheduleLinks(): Try[Seq[Link]] = read[ScheduleList](s"schedules.json").map(_.links)

  def getScheduleDays(): Try[Seq[Day]] = getScheduleLinks().map(_.map(asId))

  def getSchedule(day: Day): Try[Seq[Slot]] = read[Schedule](s"schedules/$day.json").map(_.slots)

  def getSchedule(link: Link): Try[Seq[Slot]] = getSchedule(asId(link))

  def getSchedules(): Try[Map[Day, Seq[Slot]]] = getScheduleDays().flatMap { days =>
    TryUtils.sequence(days.map { day =>
      getSchedule(day).map(s => (day, s))
    }).map(_.toMap)
  }

  def getModel(): (Seq[Speaker], Seq[Talk], Seq[Room], Seq[Slot]) = {
    (for {
      rooms <- getRooms()
      schedules <- getScheduleDays()
      slots <- TryUtils.sequence(schedules.map(getSchedule)).map(_.flatten)
      talks <- TryUtils.sequence(slots.flatMap(_.talk).map(_.id).distinct.map(getTalk))
      speakers <- TryUtils.sequence(talks.flatMap(_.speakers).distinct.map(getSpeaker))
    } yield (speakers, talks, rooms, slots)).get
  }

  private def read[A](path: Path)(implicit decoder: Decoder[A]): Try[A] = {
    fileClient.read(s"$datasource/$path")
      .flatMap { file => parser.decode[A](file).toTry }
  }

  private def readAll[A](path: Path)(implicit decoder: Decoder[A]): Try[Seq[A]] = {
    fileClient.listFiles(s"$datasource/$path")
      .map(_.filter(_.endsWith(".json")))
      .flatMap { paths => TryUtils.sequence(paths.map(fileClient.read)) }
      .flatMap { files => TryUtils.sequence(files.map(file => parser.decode[A](file).toTry)) }
  }

  private def asId(link: Link): String = link.href.split("/").last

  private def asId(link: LinkWithName): String = asId(link.link)
}
