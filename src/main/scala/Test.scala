/**
  * scalac Test.scala     : compile
  * scalap -private Test  : show generated scala code for class
  * javap -p Test         : show generated java code for class with private members
  * scalap -private Test$ : show generated scala code for module
  * javap -p Test$        : show generated java code for module with private members
  *
  * javap -c : byte code
  */
case class Test(id: Int, name: String) {
  def show: String = name
}
