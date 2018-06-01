import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


trait Monad[A] {


  //  def uplift(a: A): Monad[A]

  def map[B](f: A => B): Monad[B]

  def flatMap[B](f: A => Monad[B]): Monad[B]

}


class MyCol[T](val v1: Option[T], val v2: Option[T]) {


  def foreach(f: T => Unit): Unit = {
    if (v1.isDefined) {
      f(v1.get)
    }
    if (v2.isDefined) {
      f(v2.get)
    }
  }

  def map[B](f: T => B): MyCol[B] = {
    (v1, v2) match {
      case (Some(a), Some(b)) => new MyCol(Some(f(a)), Some(f(b)))
      case (Some(a), None) => new MyCol(Some(f(a)), None)
      case (None, Some(b)) => new MyCol(None, Some(f(b)))
      case (None, None) => new MyCol(None, None)
    }
  }

  def withFilter(f: T => Boolean): MyCol[T] = {
    new MyCol(v1.filter(f), v2.filter(f))
  }

  def flatMap[B](f: T => MyCol[B]): MyCol[B] = {
    (v1, v2) match {
      case (Some(a), Some(b)) => new MyCol(f(a).v1, f(b).v2)
      case (Some(a), None) => new MyCol(f(a).v1, f(a).v2)
      case (None, Some(b)) => new MyCol(f(b).v1, f(b).v2)
      case (None, None) => new MyCol(None, None)
    }
  }

  override def toString: String = s"MyCol($v1, $v2)"
}

object DemoForComprehension {

  def main(args: Array[String]): Unit = {

    val myCol: MyCol[Int] = new MyCol[Int](Some(1), Some(2))

    val res: MyCol[String] =
      for {
        x: Int <- myCol
        y: Int <- myCol
        if x == 1
        z = x + y
      } yield {
        z.toString
      }
    println(res)


    //    myCol
    //      .flatMap{
    //        x =>
    //          myCol.map{
    //            y => x.toString + y.toString
    //          }
    //      }
  }


}


