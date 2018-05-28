package exercices.codingame

import scala.util.{Failure, Success, Try}

// from https://www.codingame.com/ide/puzzle/horse-racing-duals
object HorseRacingDuals {
  def main(args: Array[String]): Unit = {
    val horses: Seq[Int] = (0 until readInt).map { _ => readInt }
    getNearestPowerDiff(horses).foreach(println)
  }

  def getNearestPowerDiff(horses: Seq[Int]): Try[Int] = {
    if (horses.length < 2) {
      Failure(new Exception("Unable to get diff on list with less than two elements"))
    } else {
      val sortedHorses: Seq[Int] = horses.sorted
      val slidingHorses: Iterator[Seq[Int]] = sortedHorses.sliding(2, 1)
      val horseDiffs: Iterator[Int] = slidingHorses.map { case Seq(a, b) => b - a }
      Success(horseDiffs.min)
    }
  }
}
