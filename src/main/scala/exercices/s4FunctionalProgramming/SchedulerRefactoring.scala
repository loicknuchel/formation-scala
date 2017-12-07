package exercices.s4FunctionalProgramming

import java.util.concurrent.{Executors, ScheduledExecutorService, ScheduledFuture, TimeUnit}

import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

object SchedulerRefactoring {

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

    def scheduleGeneric[T](operation : => T, schedule: Runnable => ScheduledFuture[_]): CancelableFuture[T] = {
      val promise = Promise[T]()
      val scheduledFuture = schedule(() => promise.complete(Try(operation)))
      new CancelableFuture(promise, scheduledFuture.cancel)
    }

    def shutdown(): Unit =
      underlying.shutdown()
  }

  object Scheduler {
    def newSingleThreadScheduler(): Scheduler =
      new Scheduler(Executors.newSingleThreadScheduledExecutor())
  }

}
