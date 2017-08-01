package exercices

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

import scala.util.Success

class e3FunctionalTypesSpec extends FunSpec with Matchers with ScalaFutures {

  describe("Devoxx") {
    import e3FunctionalTypes.Devoxx._
    import helpers.RealFileClient
    import project.devoxx.dao.LocalClient

    val fileClient = new RealFileClient()
    val localClient = new LocalClient(fileClient, "src/main/resources/devoxx")

    val talkId = "XPI-0919"
    val talkTitle = "Scala class, bien démarrer avec Scala"
    val speakerId = "09a79f4e4592cf77e5ebf0965489e6c7ec0438cd"
    val roomId = "par224M-225M"

    describe("frenchTalkPercentage") {
      it("should calculate the percentage of french talks") {
        frenchTalkPercentage(localClient).map(r => math.round(r * 100)) shouldBe Success(90)
      }
    }
    describe("speakersOfTalk") {
      it("should list speaker for a talk") {
        speakersOfTalk(localClient)(talkId).map(_.map(_.uuid).sorted) shouldBe Success(Seq(speakerId, "1693d28c079e6c28269b9aa86ae04a4549ad3074", "d167a51898267ed3b5913c1789f1ae6110a6ecf5"))
      }
    }
    describe("talksOfSpeaker") {
      it("should list talks for a speaker") {
        talksOfSpeaker(localClient)(speakerId).map(_.map(_.id)) shouldBe Success(Seq(talkId))
      }
    }
    describe("roomSchedule") {
      it("should return the schedule for a room") {
        roomSchedule(localClient)(roomId).map(_.length) shouldBe Success(4)
      }
    }
    describe("isSpeaking") {
      it("should eventually return the Room where the speaker is at the asked time") {
        isSpeaking(localClient)(speakerId, 1491491200000L).map(_.map(_.id)) shouldBe Success(Some(roomId))
      }
    }
  }

  describe("LazyMonad") {
    import e3FunctionalTypes.LazyMonad._
    it("should not evaluate code on initialization") {
      var x = 0
      val l = Lazy({
        x += 1
      })
      x shouldBe 0
      l.get
      x shouldBe 1
    }
    it("should execute code only once") {
      var x = 0
      val l = Lazy({
        x += 1
      })
      x shouldBe 0
      l.get
      x shouldBe 1
      l.get
      x shouldBe 1
    }
    it("should be able to chain lazy executions") {
      var x = 0
      val l: Lazy[String] = Lazy({
        x += 1;
        "a"
      })
      val m: Lazy[String] = l.map(v => {
        x += 1;
        v + "b"
      })
      x shouldBe 0
      m.get shouldBe "ab"
      x shouldBe 2
    }
    it("should unstack only one context at a time") {
      var x = 0
      val l: Lazy[String] = Lazy({
        x += 1;
        "a"
      })
      val m: Lazy[Lazy[String]] = l.map(v => Lazy({
        x += 1;
        v + "b"
      }))
      x shouldBe 0
      val n: Lazy[String] = m.get
      x shouldBe 1
      n.get shouldBe "ab"
      x shouldBe 2
    }
    it("should be able to return a default value") {
      var x1 = 0
      val l1: Lazy[String] = Lazy({
        x1 += 1;
        "a"
      })
      val r1 = l1.getOrElse({
        x1 += 1;
        "b"
      })
      x1 shouldBe 1
      r1 shouldBe "a"

      var x2 = 0
      val l2: Lazy[String] = Lazy({
        x2 += 1;
        throw new Exception;
        "a"
      })
      val r2 = l2.getOrElse({
        x2 += 1;
        "b"
      })
      x2 shouldBe 2
      r2 shouldBe "b"
    }
    it("should have orElse") {
      var x1 = 0
      val l1: Lazy[String] = Lazy({
        x1 += 1;
        "a"
      })
      val m1 = l1.orElse(Lazy({
        x1 += 1;
        "b"
      }))
      x1 shouldBe 0
      val r1 = m1.get
      x1 shouldBe 1
      r1 shouldBe "a"

      var x2 = 0
      val l2: Lazy[String] = Lazy({
        x2 += 1;
        throw new Exception;
        "a"
      })
      val m2 = l2.orElse(Lazy({
        x2 += 1;
        "b"
      }))
      x2 shouldBe 0
      val r2 = m2.get
      x2 shouldBe 2
      r2 shouldBe "b"
    }
    it("should be able to flatMap") {
      var x = 0
      val l: Lazy[String] = Lazy({
        x += 1;
        "a"
      })
      x shouldBe 0
      val m: Lazy[String] = l.flatMap(v => Lazy({
        x += 1;
        v + "b"
      }))
      x shouldBe 0
      m.get shouldBe "ab"
      x shouldBe 2
    }
    it("should have syntaxic sugar") {
      import Lazy._
      val l1 = lazily {
        1
      }
      l1.get shouldBe 1
    }
    it("should tell if it was evaluated") {
      val l = Lazy(1)
      l.isEvaluated shouldBe false
      l.get shouldBe 1
      l.isEvaluated shouldBe true
    }
    it("should filter") {
      var x = 0
      val l = Lazy({
        x += 1;
        1
      })
      val f1 = l.filter(_ > 0)
      val f2 = l.filter(_ < 0)
      x shouldBe 0
      f1.asTry shouldBe Success(1)
      f2.asTry.isFailure shouldBe true
      x shouldBe 1
    }
    it("should comply to for-comprehension") {
      var x = 0
      val res = for {
        r1 <- Lazy({
          x += 1;
          "a"
        })
        r2 <- Lazy({
          x += 1;
          "b"
        })
        r3 <- Lazy({
          x += 1;
          "c"
        }) if r2 == "b"
      } yield r1 + r2 + r3
      x shouldBe 0
      res.get shouldBe "abc"
      x shouldBe 3
    }
    it("should be converted into a Try") {
      val l1 = Lazy(1)
      l1.asTry shouldBe Success(1)
      val l2 = Lazy(throw new Exception("fail"))
      l2.asTry.isFailure shouldBe true
    }
    it("should be converted into a Future") {
      import scala.concurrent.ExecutionContext.Implicits.global
      val l1 = Lazy(1)
      whenReady(l1.asFuture) { result =>
        result shouldBe 1
      }
      val l2 = Lazy(throw new Exception("fail"))
      whenReady(l2.asFuture.failed) { err =>
        err.getMessage shouldBe "fail"
      }
    }
    it("should toString") {
      val l = Lazy(1)
      l.toString shouldBe "Lazy(not evaluated)"
      l.get
      l.toString shouldBe "Lazy(1)"
    }
    it("should sequence a Lazy Seq") {
      var x = 0
      val seqLazy: Seq[Lazy[Int]] = Seq(Lazy({
        x += 1;
        1
      }), Lazy({
        x += 1;
        2
      }), Lazy({
        x += 1;
        3
      }))
      x shouldBe 0
      val lazySeq: Lazy[Seq[Int]] = Lazy.sequence(seqLazy)
      x shouldBe 0
      lazySeq.get shouldBe Seq(1, 2, 3)
      x shouldBe 3
    }
    it("should be able to flatten") {
      var x = 0
      val l: Lazy[String] = Lazy({
        x += 1;
        "a"
      })
      x shouldBe 0
      val m: Lazy[Lazy[String]] = l.map(v => Lazy({
        x += 1;
        v + "b"
      }))
      x shouldBe 0
      val n: Lazy[String] = m.flatten
      x shouldBe 0
      n.get shouldBe "ab"
      x shouldBe 2
    }
    it("should collect") {
      var x = 0
      val l: Lazy[Int] = Lazy({
        x += 1;
        1
      })
      val c1 = l.collect { case v if v > 0 => v.toString }
      val c2 = l.collect { case v if v < 0 => v.toString }
      x shouldBe 0
      c1.asTry shouldBe Success("1")
      c2.asTry.isFailure shouldBe true
      x shouldBe 1

      x = 0
      val m: Lazy[Option[Int]] = Lazy({
        x += 1;
        Some(1)
      })
      val c3 = m.collect { case Some(v) => v }
      val c4 = m.collect { case None => 2 }
      x shouldBe 0
      c3.asTry shouldBe Success(1)
      c4.asTry.isFailure shouldBe true
      x shouldBe 1
    }
    it("should compose Lazy") {
      var x = 0
      val l: Lazy[String] = Lazy({
        x += 1;
        "a"
      })
      val m: Lazy[String] = l.compose(Lazy({
        x += 1;
        2
      }))
      x shouldBe 0
      m.get shouldBe "a"
      x shouldBe 2
    }
    it("should sequence Lazy") {
      var x = 0
      val l: Lazy[String] = Lazy({
        x += 1;
        "a"
      })
      val m: Lazy[Int] = l.sequence(Lazy({
        x += 1;
        2
      }))
      x shouldBe 0
      m.get shouldBe 2
      x shouldBe 2
    }
    it("should be covarient") {
      assertCompiles(
        """val a: Lazy[List[Int]] = Lazy(List(2))
          |val b: Lazy[Seq[Int]] = a
        """.stripMargin)
    }
  }
}
