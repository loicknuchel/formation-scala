package exercices.s4FunctionalProgramming

import org.scalatest.{BeforeAndAfterAll, FunSpec, Matchers}

import scala.concurrent.duration._
import scala.language.postfixOps

class SchedulerRefactoringSpec extends FunSpec with Matchers with BeforeAndAfterAll {

  import SchedulerRefactoring._

  val scheduler: Scheduler = Scheduler.newSingleThreadScheduler()

  override def afterAll(): Unit = {
    scheduler.shutdown()
  }

  describe("SchedulerRefactoring") {
    describe("scheduleOnce") {
      it("should run once") {
        var x = 0
        scheduler.scheduleOnce(100 millis) {
          x = x + 1
        }
        x shouldBe 0
        Thread.sleep(50)
        x shouldBe 0
        Thread.sleep(100)
        x shouldBe 1
        Thread.sleep(100)
        x shouldBe 1
      }
    }
    describe("scheduleEvery") {
      it("should call a function periodically") {
        var x = 0
        scheduler.scheduleAtFixedRate(100 millis) {
          x = x + 1
        }
        Thread.sleep(150)
        x shouldBe 2
      }
      it("should be able to be cancelled") {
        var x = 0
        val cancelable = scheduler.scheduleAtFixedRate(100 millis) {
          x = x + 1
        }
        Thread.sleep(150)
        cancelable.cancel()
        Thread.sleep(100)
        x shouldBe 2
      }
      it("should starts with a delay") {
        var x = 0
        scheduler.scheduleAtFixedRate(100 millis, 100) {
          x = x + 1
        }
        x shouldBe 0
        Thread.sleep(50)
        x shouldBe 0
        Thread.sleep(100)
        x shouldBe 1
      }
      it("should be able to schedule multiple things") {
        var x = 0
        var y = 0
        var z = 0
        scheduler.scheduleOnce(100 millis) {
          x = x + 1
        }
        scheduler.scheduleAtFixedRate(100 millis) {
          y = y + 1
        }
        val zCancelable = scheduler.scheduleAtFixedRate(100 millis) {
          z = z + 1
        }
        Thread.sleep(50)
        x shouldBe 0
        y shouldBe 1
        z shouldBe 1
        zCancelable.cancel()
        Thread.sleep(100)
        x shouldBe 1
        y shouldBe 2
        z shouldBe 1
      }
    }
  }
}
