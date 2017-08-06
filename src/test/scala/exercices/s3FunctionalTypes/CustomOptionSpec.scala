package exercices.s3FunctionalTypes

import org.scalatest.{FunSpec, Matchers}

class CustomOptionSpec extends FunSpec with Matchers {
  describe("CustomOption") {
    import CustomOption._
    it("should tell if empty") {
      Some(1).isEmpty shouldBe false
      None.isEmpty shouldBe true
    }
    it("should extract the value") {
      Some(1).get shouldBe 1
      assertThrows[NoSuchElementException] {
        None.get
      }
    }
    it("should extract the value with default") {
      Some(1).getOrElse(2) shouldBe 1
      None.getOrElse(2) shouldBe 2
    }
    it("should transform the value") {
      Some(1).map(_.toString) shouldBe Some("1")
      None.map(_.toString) shouldBe None
    }
    it("should find the value") {
      Some(1).find(_ => true) shouldBe Some(1)
      Some(1).find(_ => false) shouldBe None
      None.find(_ => true) shouldBe None
      None.find(_ => false) shouldBe None
    }
    it("should filter the value") {
      Some(1).filter(_ => true) shouldBe Some(1)
      Some(1).filter(_ => false) shouldBe None
      None.filter(_ => true) shouldBe None
      None.filter(_ => false) shouldBe None
    }
    it("should flatMap the value") {
      Some(1).flatMap(n => Some(n + 1)) shouldBe Some(2)
      Some(1).flatMap(_ => None) shouldBe None
      (None: Option[Int]).flatMap(n => Some(n + 1)) shouldBe None
      None.flatMap(_ => None) shouldBe None
    }
    it("should aggregate the value") {
      Some(1).foldLeft("a")((acc, it) => acc + it) shouldBe "a1"
      None.foldLeft("a")((acc, it) => acc + it) shouldBe "a"
    }
    it("should exec on value") {
      var x = 0
      Some(1).foreach(n => x += n)
      x shouldBe 1
      var y = 0
      (None: Option[Int]).foreach(n => y += n)
      y shouldBe 0
    }
    it("should test existence") {
      Some(1).exists(_ => true) shouldBe true
      Some(1).exists(_ => false) shouldBe false
      None.exists(_ => true) shouldBe false
      None.exists(_ => false) shouldBe false
    }
    it("should test existence for all") {
      Some(1).forall(_ => true) shouldBe true
      Some(1).forall(_ => false) shouldBe false
      None.forall(_ => true) shouldBe true
      None.forall(_ => false) shouldBe true
    }
    it("should have syntactic sugar to build") {
      Option(1) shouldBe Some(1)
      Option(null) shouldBe None
      Option.empty[Int] shouldBe None
    }
    it("should have list converter") {
      Some(1).toList shouldBe List(1)
      None.toList shouldBe List()
    }
  }
}
