import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object DemoFuture {

  val startTime: Long = System.currentTimeMillis()

  def simulateLatency() = {
    Thread.sleep(1000)
  }

  def f[T](x: T) = Future {
    simulateLatency()
    val now = System.currentTimeMillis()
    println(s"f : $x (${now - startTime} ms)")
    x
  }

  def main(args: Array[String]): Unit = {
    val sf: Seq[Future[Int]] = (1 to 10).map(f)
    val fs: Future[Seq[Int]] = Future.sequence(sf)

    // val fs: Future[Seq[Int]] = Future.traverse(1 to 10)(f)
    //      sf.foldLeft(Future(Seq[Int]())){
    //        case (acc: Future[Seq[Int]], f) =>
    //          acc.zip(f).map{
    //            case (s, i) => s:+i
    //          }
    //      }

    val res: Seq[Int] = Await.result(fs, Duration.apply("10 second"))

    println("res : " + res)
  }


}

//val s: Seq[Future[Int]] = (1 to 100).map{f}
//
//val seq: Future[Seq[Int]] = Future.sequence(s)
//
//println("seq : " + seq)
//
//val res: Seq[Int] = Await.result(seq, Duration.apply("10 seconds"))
//
//println("res : " + res)
//
//Thread.sleep(10000)
