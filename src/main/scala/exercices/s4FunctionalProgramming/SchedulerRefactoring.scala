package exercices.s4FunctionalProgramming

import java.util.concurrent.{Executors, ScheduledExecutorService}

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
      val promise = Promise[T]()
      val scheduledFuture = underlying.schedule(new Runnable {
        override def run(): Unit = promise.complete(Try(operation))
      }, delay.length, delay.unit)
      new CancelableFuture(promise, scheduledFuture.cancel)
    }

    def scheduleAtFixedRate(interval: FiniteDuration, delay: Long = 0)(operation: => Unit): CancelableFuture[Unit] = {
      val promise = Promise[Unit]()
      val scheduledFuture = underlying.scheduleAtFixedRate(new Runnable {
        override def run(): Unit = promise.complete(Try(operation))
      }, delay, interval.length, interval.unit)
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
