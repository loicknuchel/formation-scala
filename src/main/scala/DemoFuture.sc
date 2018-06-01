import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

def simulateLatency() = {
  Thread.sleep(1000)
}

def p[T](x: T) = {
  println("p : " + x)
  x
}

def f[T](x: T) = Future {
  simulateLatency()
  p(x)
}

val f1 = f(1)
val f2 = f(2)






