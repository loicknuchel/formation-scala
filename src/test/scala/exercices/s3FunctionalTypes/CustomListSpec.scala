package exercices.s3FunctionalTypes

import org.scalatest.{FunSpec, Matchers}

class CustomListSpec extends FunSpec with Matchers {
  describe("CustomList") {
    import CustomList._
    it("should tell if empty") {
      Nil.isEmpty shouldBe true
      ::(1, Nil).isEmpty shouldBe false
    }
    it("should return head") {
      assertThrows[NoSuchElementException] {
        Nil.head
      }
      ::(1, Nil).head shouldBe 1
      ::(1, ::(2, Nil)).head shouldBe 1
    }
    it("should return tail") {
      assertThrows[UnsupportedOperationException] {
        Nil.tail
      }
      ::(1, Nil).tail shouldBe Nil
      ::(1, ::(2, Nil)).tail shouldBe ::(2, Nil)
    }
    it("should eventually retun the first element") {
      Nil.headOption shouldBe None
      ::(1, Nil).headOption shouldBe Some(1)
      ::(1, ::(2, Nil)).headOption shouldBe Some(1)
    }
    it("should return the size") {
      Nil.size shouldBe 0
      ::(1, Nil).size shouldBe 1
      ::(1, ::(2, Nil)).size shouldBe 2
    }
    it("should retun the n first elements") {
      Nil.take(2) shouldBe Nil
      ::(1, Nil).take(2) shouldBe ::(1, Nil)
      ::(1, ::(2, Nil)).take(2) shouldBe ::(1, ::(2, Nil))
      ::(1, ::(2, ::(3, Nil))).take(2) shouldBe ::(1, ::(2, Nil))
    }
    it("should transform list values") {
      Nil.map(_.toString) shouldBe Nil
      ::(1, Nil).map(_.toString) shouldBe ::("1", Nil)
      ::(1, ::(2, Nil)).map(_.toString) shouldBe ::("1", ::("2", Nil))
    }
    it("should find the first value") {
      Nil.find(_ => true) shouldBe None
      Nil.find(_ => false) shouldBe None
      ::(1, Nil).find(_ => true) shouldBe Some(1)
      ::(1, Nil).find(_ => false) shouldBe None
      ::(1, ::(2, Nil)).find(_ => true) shouldBe Some(1)
      ::(1, ::(2, Nil)).find(_ => false) shouldBe None
    }
    it("should filter list values") {
      Nil.filter(_ => true) shouldBe Nil
      Nil.filter(_ => false) shouldBe Nil
      ::(1, Nil).filter(_ => true) shouldBe ::(1, Nil)
      ::(1, Nil).filter(_ => false) shouldBe Nil
      ::(1, ::(2, Nil)).filter(_ => true) shouldBe ::(1, ::(2, Nil))
      ::(1, ::(2, Nil)).filter(_ => false) shouldBe Nil
      ::(1, ::(2, Nil)).filter(n => n % 2 == 0) shouldBe ::(2, Nil)
    }
    it("should concat two lists") {
      Nil ++ Nil shouldBe Nil
      ::(1, Nil) ++ Nil shouldBe ::(1, Nil)
      Nil ++ ::(1, Nil) shouldBe ::(1, Nil)
      ::(1, Nil) ++ ::(2, Nil) shouldBe ::(1, ::(2, Nil))
      ::(1, ::(2, Nil)) ++ ::(3, Nil) shouldBe ::(1, ::(2, ::(3, Nil)))
    }
    it("should flatMap the list") {
      Nil.flatMap(_ => Nil) shouldBe Nil
      (Nil: List[Int]).flatMap(n => ::(n + 1, Nil)) shouldBe Nil
      ::(1, Nil).flatMap(_ => Nil) shouldBe Nil
      ::(1, Nil).flatMap(n => ::(n + "1", ::(n + "2", Nil))) shouldBe ::("11", ::("12", Nil))
      ::(1, ::(2, Nil)).flatMap(_ => Nil) shouldBe Nil
      ::(1, ::(2, Nil)).flatMap(n => ::(n + "1", ::(n + "2", Nil))) shouldBe ::("11", ::("12", ::("21", ::("22", Nil))))
    }
    it("should aggregate the values") {
      Nil.foldLeft("a")((acc, it) => acc + it) shouldBe "a"
      ::(1, Nil).foldLeft("a")((acc, it) => acc + it) shouldBe "a1"
      ::(1, ::(2, Nil)).foldLeft("a")((acc, it) => acc + it) shouldBe "a12"
    }
    it("should aggregate the values without default value") {
      assertThrows[UnsupportedOperationException] {
        (Nil: List[Int]).reduceLeft((acc, it) => acc + it * 2)
      }
      ::(1, Nil).reduceLeft((acc, it) => acc + it * 2) shouldBe 1
      ::(1, ::(2, Nil)).reduceLeft((acc, it) => acc + it * 2) shouldBe 5
    }
    it("should aggregate the values without default value and explicit operation") {
      (Nil: List[Int]).sum shouldBe 0
      ::(1, Nil).sum shouldBe 1
      ::(1, ::(2, Nil)).sum shouldBe 3
    }
    it("should have syntactic sugar to build") {
      List() shouldBe Nil
      List(1) shouldBe ::(1, Nil)
      List(1, 2) shouldBe ::(1, ::(2, Nil))
      List.empty[Int] shouldBe Nil
    }
  }
}
