package exercices.s3FunctionalTypes

import scala.util.control.NonFatal

object CustomTry {
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
    def isSuccess: Boolean = ???
    def isFailure: Boolean = ???
    def get: A = ???
    def getOrElse[B >: A](default: => B): B = ???
    def map[B](f: A => B): MyTry[B] = ???
    def flatMap[B](f: A => MyTry[B]): MyTry[B] = ???
    def filter(p: A => Boolean): MyTry[A] = ???
    def foldLeft[B](z: B)(op: (B, A) => B): B = ???
    def exists(p: A => Boolean): Boolean = ???
    def toOption: Option[A] = ???
    def toList: List[A] = ???
  }

  case class MyFailure[+A](error: Throwable) extends MyTry[A] {
    def isSuccess: Boolean = ???
    def isFailure: Boolean = ???
    def get: A = ???
    def getOrElse[B >: A](default: => B): B = ???
    def map[B](f: A => B): MyTry[B] = ???
    def flatMap[B](f: A => MyTry[B]): MyTry[B] = ???
    def filter(p: A => Boolean): MyTry[A] = ???
    def foldLeft[B](z: B)(op: (B, A) => B): B = ???
    def exists(p: A => Boolean): Boolean = ???
    def toOption: Option[A] = ???
    def toList: List[A] = ???
  }

}
