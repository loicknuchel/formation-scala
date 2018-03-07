package exercices.codingame

object HorseRacingDuals {

  /**
    * Auto-generated code below aims at helping you parse
    * the standard input according to the problem statement.
    **/
  object Solution extends App {
    val horses: Seq[Int] = (0 until readInt).map(_ => readInt).sorted

    val slidingHorses: Iterator[Seq[Int]] = horses.sliding(2, 1)
    val diffs: Iterator[Int] =
      slidingHorses.map {
        case Seq(a, b) => b - a
      }

    println(diffs.min)
  }

}
