package exercices.codingame

import scala.util.{Failure, Success, Try}

// from https://www.codingame.com/ide/puzzle/horse-racing-duals
object HorseRacingDuals {
  def main(args: Array[String]): Unit = {
    val horses: Seq[Int] = (0 until readInt).map { _ => readInt }
    getNearestPowerDiff(horses).foreach(println)
  }

  def getNearestPowerDiff(horses: Seq[Int]): Try[Int] = {
    if(horses.length < 2) {
      Failure(new Exception("Not enough horses"))
    } else {
      val result = horses
        .sorted
        .sliding(2, 1)
        .map { case Seq(low, high) => high - low }
        .min
      Success(result)
    }
  }
}
