package exercices.codingame

import scala.util.{Failure, Success, Try}

// from https://www.codingame.com/ide/puzzle/horse-racing-duals
object HorseRacingDuals {
  def main(args: Array[String]): Unit = {
    val horses: Seq[Int] = (0 until readInt).map { _ => readInt }
    getNearestPowerDiff(horses).foreach(println)
  }

  def getNearestPowerDiff(horses: Seq[Int]): Try[Int] = {
    ???
  }
}
