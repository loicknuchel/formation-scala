package project.devoxx.dao

import helpers.Converters._
import helpers.HttpClient
import io.circe.generic.auto._
import project.devoxx.domain._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps

// docs http://cfp.devoxx.fr/api
class ApiClient(httpClient: HttpClient) extends Dao[Future] {
  private implicit val useCache = true
  val baseUrl = "http://cfp.devoxx.fr/api"
  val conference = "DevoxxFR2017"
  val conferenceUrl = s"$baseUrl/conferences/$conference"
  val roomsUrl = s"$conferenceUrl/rooms/"
  val schedulesUrl = s"$conferenceUrl/schedules/"

  def scheduleUrl(day: String) = s"$conferenceUrl/schedules/$day"

  val speakersUrl = s"$conferenceUrl/speakers"

  def speakerUrl(id: SpeakerId) = s"$conferenceUrl/speakers/$id"

  def talkUrl(id: TalkId) = s"$conferenceUrl/talks/$id"

  def getRooms(): Future[Seq[Room]] =
    httpClient.get(roomsUrl).flatMap(res => parseJson[RoomList](res).toFuture).map(_.rooms)

  def getRoom(id: RoomId): Future[Room] =
    getRooms().flatMap(_.find(_.id == id).toFuture(new NoSuchElementException))

  def getSpeakers(): Future[Seq[Speaker]] =
    httpClient.get(speakersUrl).flatMap(res => parseJson[Seq[Speaker]](res).toFuture)

  def getSpeaker(id: SpeakerId): Future[Speaker] =
    getSpeakerByUrl(speakerUrl(id))

  def getSpeaker(link: LinkWithName): Future[Speaker] =
    getSpeakerByUrl(link.link.href)

  private def getSpeakerByUrl(url: String): Future[Speaker] =
    httpClient.get(url).flatMap(res => parseJson[Speaker](res).toFuture)

  def getTalk(id: TalkId): Future[Talk] =
    getTalkByUrl(talkUrl(id))

  def getTalk(link: LinkWithName): Future[Talk] =
    getTalkByUrl(link.link.href)

  private def getTalkByUrl(url: String): Future[Talk] =
    httpClient.get(url).flatMap(res => parseJson[Talk](res).toFuture)

  def getScheduleLinks(): Future[Seq[Link]] =
    httpClient.get(schedulesUrl).flatMap(res => parseJson[ScheduleList](res).toFuture).map(_.links)

  def getScheduleDays(): Future[Seq[Day]] = getScheduleLinks().map(_.map(asId))

  def getSchedule(day: Day): Future[Seq[Slot]] =
    getScheduleByUrl(scheduleUrl(day))

  def getSchedule(link: Link): Future[Seq[Slot]] =
    getScheduleByUrl(link.href)

  private def getScheduleByUrl(url: String): Future[Seq[Slot]] =
    httpClient.get(url).flatMap(res => parseJson[Schedule](res).toFuture).map(_.slots)

  def getSchedules(): Future[Map[Day, Seq[Slot]]] = getScheduleDays().flatMap { days =>
    Future.sequence(days.map { day =>
      getSchedule(day).map(s => (day, s))
    }).map(_.toMap)
  }

  def getModel(): (Seq[Speaker], Seq[Talk], Seq[Room], Seq[Slot]) = {
    Await.result(for {
      rooms <- getRooms()
      schedules <- getScheduleLinks()
      slots <- Future.sequence(schedules.map(getSchedule)).map(_.flatten)
      talks <- Future.sequence(slots.flatMap(_.talk).map(_.id).distinct.map(getTalk))
      speakers <- Future.sequence(talks.flatMap(_.speakers).distinct.map(getSpeaker))
    } yield (speakers, talks, rooms, slots), 5 seconds)
  }

  private def asId(link: Link): String = link.href.split("/").last
}
