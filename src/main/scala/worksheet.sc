/**
  * Une worksheet permet d'expérimenter avec le code scala.
  * Le résultat de chaque ligne sera affiché dans la partie de droite
  * et l'ensemble du code recompilé et exécuté à chaque modification :)
  *
  * Have fun !
  */
// val ex = "example"

val s: Iterator[Seq[Int]] = Seq(1, 2, 3, 4).sliding(2, 1)

s.foreach{println}

val s2: Iterator[Seq[Int]] = Seq(1, 2, 3, 4).sliding(2, 2)

s2.foreach{println}
