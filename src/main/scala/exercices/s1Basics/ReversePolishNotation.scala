package exercices.s1Basics

import scala.annotation.tailrec

object ReversePolishNotation {
  def eval(str: String): Int = {
    @tailrec
    def internal(args: List[String], stack: List[Int]): Int = {
      args.headOption match {
        case Some("+") => internal(args.tail, (stack.tail.head + stack.head) :: stack.tail.tail)
        case Some("*") => internal(args.tail, (stack.tail.head * stack.head) :: stack.tail.tail)
        case Some("-") => internal(args.tail, (stack.tail.head - stack.head) :: stack.tail.tail)
        case Some("/") => internal(args.tail, (stack.tail.head / stack.head) :: stack.tail.tail)
        case Some(arg) => internal(args.tail, arg.toInt :: stack)
        case None => stack.head
      }
    }

    internal(str.split(" ").toList, List())
  }
}
