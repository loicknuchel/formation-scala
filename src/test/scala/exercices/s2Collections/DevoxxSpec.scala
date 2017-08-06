package exercices.s2Collections

import java.util.Date

import org.scalatest.{FunSpec, Matchers}

class DevoxxSpec extends FunSpec with Matchers {
  describe("Devoxx") {
    import Devoxx._

    val (talks, speakers, slots, rooms) = loadData().get
    val talkId = "XPI-0919"
    val speakerId = "09a79f4e4592cf77e5ebf0965489e6c7ec0438cd"
    val roomId = "par224M-225M"

    describe("frenchTalkPercentage") {
      it("should calculate the percentage of french talks") {
        math.round(frenchTalkPercentage(talks) * 100) shouldBe 90
      }
    }
    describe("talksOfSpeaker") {
      it("should list talks for a speaker") {
        talksOfSpeaker(speakers, talks, speakerId).map(_.id) shouldBe Seq(talkId)
      }
    }
    describe("roomSchedule") {
      it("should return the schedule for a room") {
        roomSchedule(slots, talks, roomId).length shouldBe 4
      }
    }
    describe("isSpeaking") {
      it("should eventually return the Room where the speaker is at the asked time") {
        isSpeaking(slots, talks, rooms, speakerId, new Date(1491491200000L)).map(_.id) shouldBe Some(roomId)
      }
    }
    describe("loadData") {
      it("should read devoxx data and return them") {
        talks.length shouldBe 236
        speakers.length shouldBe 297
        slots.length shouldBe 234
        rooms.length shouldBe 18
      }
    }
  }
}
