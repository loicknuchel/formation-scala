package corrections

import java.util.concurrent.{ScheduledExecutorService, ScheduledFuture}

import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import scala.io.Source
import scala.util.control.NonFatal
import scala.util.{Failure, Try}

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

  describe("CustomTry") {
    sealed abstract class MyTry[+A] {
      def isSuccess: Boolean

      def isFailure: Boolean

      def get: A

      def getOrElse[B >: A](default: => B): B

      def map[B](f: A => B): MyTry[B]

      def flatMap[B](f: A => MyTry[B]): MyTry[B]

      def filter(p: A => Boolean): MyTry[A]

      def foldLeft[B](z: B)(op: (B, A) => B): B

      def exists(p: A => Boolean): Boolean

      def toOption: Option[A]

      def toList: List[A]
    }

    object MyTry {
      def apply[A](v: => A): MyTry[A] =
        try MySuccess(v) catch {
          case NonFatal(e) => MyFailure(e)
        }
    }

    case class MySuccess[+A](value: A) extends MyTry[A] {
      def isSuccess: Boolean = true

      def isFailure: Boolean = false

      def get: A = value

      def getOrElse[B >: A](default: => B): B = value

      def map[B](f: A => B): MyTry[B] = MyTry(f(value))

      def flatMap[B](f: A => MyTry[B]): MyTry[B] = f(value)

      def filter(p: A => Boolean): MyTry[A] = if (p(value)) this else MyFailure(new Exception)

      def foldLeft[B](z: B)(op: (B, A) => B): B = op(z, value)

      def exists(p: A => Boolean): Boolean = p(value)

      def toOption: Option[A] = Some(value)

      def toList: List[A] = List(value)
    }

    case class MyFailure[+A](error: Throwable) extends MyTry[A] {
      def isSuccess: Boolean = false

      def isFailure: Boolean = true

      def get: A = throw error

      def getOrElse[B >: A](default: => B): B = default

      def map[B](f: A => B): MyTry[B] = MyFailure(error)

      def flatMap[B](f: A => MyTry[B]): MyTry[B] = MyFailure(error)

      def filter(p: A => Boolean): MyTry[A] = this

      def foldLeft[B](z: B)(op: (B, A) => B): B = z

      def exists(p: A => Boolean): Boolean = false

      def toOption: Option[A] = None

      def toList: List[A] = List.empty
    }
  }

  describe("ReadFile") {
    type Header = String

    def readFile(path: String): Try[Seq[String]] =
      Try(Source.fromFile(path).getLines().toList) // .map(s => s.getLines().toSeq)

    def parseFile(path: String): Try[Seq[Map[Header, String]]] =
      readFile(path).map {
        case head +: tail => {
          val header = head.split(",")
          tail.map { line =>
            header.zip(line.split(",")).toMap
          }
        }
      }

    case class User(id: Int, firstName: String, lastName: String, email: String, gender: String, ip: Option[String])

    def formatLine(line: Map[Header, String]): Try[User] = {
      Try(User(line("id").toInt, line("first_name"), line("last_name"), line("email"), line("gender"), line.get("ip_address").filter(_.nonEmpty)))
    }

    def formatFile(path: String): (Seq[User], Seq[(Int, Throwable)]) = {
      parseFile(path).map { lines =>
        val parsedLines: Seq[Try[User]] = lines
          .map(formatLine)

        val indexedParsedLined: Seq[(Try[User], Int)] = parsedLines
          .zipWithIndex

        val (users: Seq[(Try[User], Int)], errors: Seq[(Try[User], Int)]) =
          indexedParsedLined
            .partition { case (t, _) => t.isSuccess }
        users.map(_._1.get) -> errors.map { case (Failure(t), i) => (i + 1) -> t }
      }.getOrElse(Nil -> Nil)
    }
  }

  describe("SchedulerRefactoring") {
    class CancelableFuture[T](promise: Promise[T], cancelMethod: Boolean => Boolean) {
      def cancel(mayInterruptIfRunning: Boolean = false): Boolean =
        cancelMethod(mayInterruptIfRunning)
    }

    class Scheduler(underlying: ScheduledExecutorService) {
      def scheduleOnce[T](delay: FiniteDuration)(operation: => T): CancelableFuture[T] = {
        scheduleGeneric(operation, r => underlying.schedule(r, delay.length, delay.unit))
      }

      def scheduleAtFixedRate(interval: FiniteDuration, delay: Long = 0)(operation: => Unit): CancelableFuture[Unit] = {
        scheduleGeneric[Unit](operation, r => underlying.scheduleAtFixedRate(r, delay, interval.length, interval.unit))
      }

      def scheduleGeneric[T](operation: => T, schedule: Runnable => ScheduledFuture[_]): CancelableFuture[T] = {
        val promise = Promise[T]()
        val scheduledFuture = schedule(() => promise.complete(Try(operation)))
        new CancelableFuture(promise, scheduledFuture.cancel)
      }

      def shutdown(): Unit =
        underlying.shutdown()
    }
  }
}
