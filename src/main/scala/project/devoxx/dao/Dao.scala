package project.devoxx.dao

import project.devoxx.domain._

trait Dao[M[_]] {
  def getRooms(): M[Seq[Room]]
  def getRoom(id: RoomId): M[Room]
  def getSpeakers(): M[Seq[Speaker]]
  def getSpeaker(id: SpeakerId): M[Speaker]
  def getTalk(id: TalkId): M[Talk]
  def getScheduleDays(): M[Seq[Day]]
  def getSchedules(): M[Map[Day, Seq[Slot]]]
  def getSchedule(day: Day): M[Seq[Slot]]
  def getModel():(Seq[Speaker], Seq[Talk], Seq[Room], Seq[Slot])
}
