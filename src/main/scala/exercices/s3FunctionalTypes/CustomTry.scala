package exercices.s3FunctionalTypes

import scala.util.control.NonFatal

object CustomTry {
  /**
    * Complète les fonctions avec ??? dans MySuccess et MyFailure pour faire passer les tests
    */

  sealed abstract class MyTry[+A] {
    def isSuccess: Boolean
    def isFailure: Boolean
    def get: A
    def getOrElse[B >: A](default: => B): B
    def map[B](f: A => B): MyTry[B]
    def flatMap[B](f: A => MyTry[B]): MyTry[B]
    def filter(p: A => Boolean): MyTry[A]
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
    def flatMap[B](f: A => MyTry[B]): MyTry[B] =
      try {
        f(value)
      } catch {
        case NonFatal(e) => MyFailure(e)
      }
    def filter(p: A => Boolean): MyTry[A] =
      try {
        if(p(value)) this else MyFailure(new Exception("filtered out"))
      } catch {
        case NonFatal(e) => MyFailure(e)
      }
    def exists(p: A => Boolean): Boolean = p(value)
    def toOption: Option[A] = Some(value)
    def toList: List[A] = List(value)
  }

  case class MyFailure[+A](error: Throwable) extends MyTry[A] {
    def isSuccess: Boolean = false
    def isFailure: Boolean = true
    def get: A = throw error
    def getOrElse[B >: A](default: => B): B = default
    def map[B](f: A => B): MyTry[B] = this.asInstanceOf[MyTry[B]]
    def flatMap[B](f: A => MyTry[B]): MyTry[B] = this.asInstanceOf[MyTry[B]]
    def filter(p: A => Boolean): MyTry[A] = this.asInstanceOf[MyTry[A]]
    def exists(p: A => Boolean): Boolean = false
    def toOption: Option[A] = None
    def toList: List[A] = List()
  }

}
