package exercices.s4FunctionalProgramming

import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture}

import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

object SchedulerRefactoring {

  class CancelableFuture[T](promise: Promise[T], cancelMethod: Boolean => Boolean) {
    def cancel(mayInterruptIfRunning: Boolean = false): Boolean =
      cancelMethod(mayInterruptIfRunning)
  }

  class Scheduler(underlying: ScheduledExecutorService) {
    def scheduleOnce[T](delay: FiniteDuration)(operation: => T): CancelableFuture[T] =
      schedule(operation, underlying.schedule(_, delay.length, delay.unit))

    def scheduleAtFixedRate(interval: FiniteDuration, delay: Long = 0)(operation: => Unit): CancelableFuture[Unit] =
      schedule(operation, underlying.scheduleAtFixedRate(_, delay, interval.length, interval.unit))

    def shutdown(): Unit =
      underlying.shutdown()

    private def schedule[T](operation: => T, sched: Runnable => ScheduledFuture[_]): CancelableFuture[T] = {
      val promise = Promise[T]()
      val scheduledFuture = sched(new Runnable {
        override def run(): Unit = promise.complete(Try(operation))
      })
      new CancelableFuture(promise, scheduledFuture.cancel)
    }
  }

  object Scheduler {
    def newSingleThreadScheduler(): Scheduler =
      new Scheduler(Executors.newSingleThreadScheduledExecutor())
  }

}
