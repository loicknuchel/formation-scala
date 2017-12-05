package corrections

import org.scalatest.{FunSpec, Matchers}

class Correction20171204 extends FunSpec with Matchers {
  describe("FizzBuzz") {
    def fizzbuzz(i: Int): String = {
      i match {
        case i if i % 15 == 0 => "FizzBuzz"
        case i if i % 3 == 0 => "Fizz"
        case i if i % 5 == 0 => "Buzz"
        case _ => i.toString
      }
    }

    it("should return correct value") {
      fizzbuzz(1) shouldBe "1"
      fizzbuzz(3) shouldBe "Fizz"
      fizzbuzz(5) shouldBe "Buzz"
      fizzbuzz(30) shouldBe "FizzBuzz"
    }
  }

  describe("AverageAge") {
    import exercices.s1Basics.AverageAge.{Employee, Team}

    def meanAge(employees: Seq[Employee], minAge: Int = 0, team: Team = null): Int = {
      var total = 0
      var count = 0
      for (e <- employees) {
        if (e.age > minAge && (team == null || team.has(e))) {
          total += e.age
          count += 1
        }
      }
      total / count
    }

    val employees: Seq[Employee] = Seq(
      Employee("Jean", 22),
      Employee("Corinne", 54),
      Employee("Fanny", 32),
      Employee("Claude", 40),
      Employee("Cécile", 25))
    val RnD: Team = Team(employees.take(3))

    it("should compute mean") {
      meanAge(employees) shouldBe 34
    }
    it("should compute mean with min age") {
      meanAge(employees, 30) shouldBe 42
    }
    it("should compute mean with min age and team") {
      meanAge(employees, 30, RnD) shouldBe 43
    }
  }

  describe("Devoxx") {
    import java.util.Date
    import exercices.s2Collections.Devoxx
    import exercices.s2Collections.Devoxx._

    val (talks, speakers, slots, rooms) = Devoxx.loadData().get
    val talkId = "XPI-0919"
    val speakerId = "09a79f4e4592cf77e5ebf0965489e6c7ec0438cd"
    val roomId = "par224M-225M"

    def frenchTalkPercentage(talks: Seq[Talk]): Double =
      talks.count(_.lang == "fr").toDouble / talks.length

    describe("frenchTalkPercentage") {
      it("should calculate the percentage of french talks") {
        math.round(frenchTalkPercentage(talks) * 100) shouldBe 90
      }
    }

    def talksOfSpeaker(speakers: Seq[Speaker], talks: Seq[Talk], id: SpeakerId): Seq[Talk] =
      talks.filter(_.speakers.contains(id))

    describe("talksOfSpeaker") {
      it("should list talks for a speaker") {
        talksOfSpeaker(speakers, talks, speakerId).map(_.id) shouldBe Seq(talkId)
      }
    }

    def roomSchedule(slots: Seq[Slot], talks: Seq[Talk], id: RoomId): Seq[(Date, Date, Talk)] = {
      val roomSlots: Seq[Slot] = slots.filter(_.room == id)
      val rommSlotsWithTalk: Seq[(Slot, Option[Talk])] = roomSlots.map(s => (s, talks.find(_.id == s.talk)))
      val maybeItems: Seq[Option[(Date, Date, Talk)]] = roomSlots
        .map { slot =>
          val maybeTalk: Option[Talk] = talks.find(_.id == slot.talk)
          val maybeResult: Option[(Date, Date, Talk)] = maybeTalk.map(talk => (slot.start, slot.end, talk))
          maybeResult
        }
      maybeItems.flatten
    }

    describe("roomSchedule") {
      it("should return the schedule for a room") {
        roomSchedule(slots, talks, roomId).length shouldBe 4
      }
    }

    def isSpeaking(slots: Seq[Slot], talks: Seq[Talk], rooms: Seq[Room], id: SpeakerId, time: Date): Option[Room] = {
      (for {
        slot <- slots if slot.start.before(time) && slot.end.after(time)
        talk <- talks.find(_.id == slot.talk) if talk.speakers.contains(id)
        room <- rooms.find(_.id == slot.room)
      } yield room).headOption
      /*slots
        .filter(s => s.start.before(time) && s.end.after(time))
        .find { s =>
          val talkOpt: Option[Talk] = talks.find(_.id == s.talk)
          talkOpt.exists(_.speakers.contains(id))
        }
        .flatMap(s => rooms.find(_.id == s.room))*/
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
