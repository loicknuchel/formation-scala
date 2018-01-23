object Main {
  def main(args: Array[String]): Unit = {
    println("Hello World")
    test(Seq("a", "b", "c"))
    test2("a")
    test2("a", "b", "c")
  }

  def test(a: Seq[String]): String = ???
  def test2(a: String*): String = ???
}
