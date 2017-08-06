package exercices.s3FunctionalTypes

import org.scalatest.{FunSpec, Matchers}

class CustomTreeSpec extends FunSpec with Matchers {
  describe("CustomTree") {
    import CustomTree._
    it("should tell if empty") {
      Leaf.isEmpty shouldBe true
      Node(1, Seq()).isEmpty shouldBe false
      Node(1, Seq(Node(2, Seq()))).isEmpty shouldBe false
    }
    it("should return root") {
      assertThrows[NoSuchElementException] {
        Leaf.root
      }
      Node(1, Seq()).root shouldBe 1
      Node(1, Seq(Node(2, Seq()))).root shouldBe 1
    }
    it("should return children") {
      assertThrows[UnsupportedOperationException] {
        Leaf.children
      }
      Node(1, Seq()).children shouldBe Seq()
      Node(1, Seq(Node(2, Seq()))).children shouldBe Seq(Node(2, Seq()))
    }
    it("should have optional children parameter") {
      Node(1) shouldBe Node(1, Seq())
    }
    it("should return the size") {
      Leaf.size shouldBe 0
      Node(1).size shouldBe 1
      Node(1, Seq(Node(2))).size shouldBe 2
      Node(1, Seq(Node(2), Node(3))).size shouldBe 3
      Node(1, Seq(Node(2, Seq(Node(3))))).size shouldBe 3
    }
    it("should return the height") {
      Leaf.height shouldBe 0
      Node(1).height shouldBe 1
      Node(1, Seq(Node(2))).height shouldBe 2
      Node(1, Seq(Node(2), Node(3))).height shouldBe 2
      Node(1, Seq(Node(2, Seq(Node(3))))).height shouldBe 3
      Node(1, Seq(Node(2, Seq(Node(3), Node(4))))).height shouldBe 3
    }
    it("should transform tree values") {
      Leaf.map(_.toString) shouldBe Leaf
      Node(1).map(_.toString) shouldBe Node("1")
      Node(1, Seq(Node(2))).map(_.toString) shouldBe Node("1", Seq(Node("2")))
      Node(1, Seq(Node(2), Node(3))).map(_.toString) shouldBe Node("1", Seq(Node("2"), Node("3")))
      Node(1, Seq(Node(2, Seq(Node(3))))).map(_.toString) shouldBe Node("1", Seq(Node("2", Seq(Node("3")))))
    }
    it("should find in depth the first value") {
      Leaf.find(_ => true) shouldBe None
      Leaf.find(_ => false) shouldBe None
      Node(1).find(_ => true) shouldBe Some(1)
      Node(1).find(_ => false) shouldBe None
      Node(1, Seq(Node(2))).find(_ => true) shouldBe Some(1)
      Node(1, Seq(Node(2))).find(_ % 2 == 0) shouldBe Some(2)
      Node(1, Seq(Node(3, Seq(Node(2))), Node(4))).find(_ % 2 == 0) shouldBe Some(2)
    }
    it("should filter in depth values") {
      Leaf.filter(_ => true) shouldBe Leaf
      Leaf.filter(_ => false) shouldBe Leaf
      Node(1).filter(_ => true) shouldBe Node(1)
      Node(1).filter(_ => false) shouldBe Leaf
      Node(1, Seq(Node(2))).filter(_ => true) shouldBe Node(1, Seq(Node(2)))
      Node(1, Seq(Node(2))).filter(_ % 2 == 0) shouldBe Leaf
      Node(2, Seq(Node(4, Seq(Node(3))), Node(6))).filter(_ % 2 == 0) shouldBe Node(2, Seq(Node(4), Node(6)))
    }
    it("should flatMap values") {
      Leaf.flatMap(_ => Node(1)) shouldBe Leaf
      Node(1).flatMap(_ => Node(2)) shouldBe Node(2)
      Node(1, Seq(Node(10))).flatMap(n => Node(n, Seq(Node(n * 2), Node(n * 3)))) shouldBe Node(1, Seq(Node(2), Node(3), Node(10, Seq(Node(20), Node(30)))))
    }
    it("should aggregate values") {
      (Leaf: Tree[Int]).fold("a")((acc, it) => acc + it) shouldBe "a"
      Node(1).fold("a")((acc, it) => acc + it) shouldBe "a1"
      Node(1, Seq(Node(2, Seq(Node(3))), Node(4))).fold("a")((acc, it) => acc + it) shouldBe "a1234"
    }
    it("should have list converter in depth") {
      Leaf.toList shouldBe Nil
      Node(1).toList shouldBe List(1)
      Node(1, Seq(Node(2))).toList shouldBe List(1, 2)
      Node(1, Seq(Node(2, Seq(Node(3))), Node(4))).toList shouldBe List(1, 2, 3, 4)
      Node(1, Seq(Node(2, Seq(Node(3), Node(4))), Node(5), Node(6, Seq(Node(7), Node(8))))).toList shouldBe List(1, 2, 3, 4, 5, 6, 7, 8)
      Node(1, Seq(Node(2, Seq(Node(3, Seq(Node(4, Seq(Node(5, Seq(Node(6))))))))), Node(7), Node(8))).toList shouldBe List(1, 2, 3, 4, 5, 6, 7, 8)
    }
    it("should have syntactic sugar to build") {
      Tree() shouldBe Leaf
      Tree(1) shouldBe Node(1)
      Tree(1, 2) shouldBe Node(1, Seq(Node(2)))
      Tree(1, 2, 3) shouldBe Node(1, Seq(Node(2), Node(3)))
      Tree(1, 2, 3, 4) shouldBe Node(1, Seq(Node(2), Node(3), Node(4)))
      Tree(1, Tree(2)) shouldBe Node(1, Seq(Node(2)))
      Tree(1, Tree(2), Tree(3)) shouldBe Node(1, Seq(Node(2), Node(3)))
      Tree(1, Tree(2), Tree(3), Tree(4)) shouldBe Node(1, Seq(Node(2), Node(3), Node(4)))
      Tree(1, Tree(2, Tree(3)), Tree(4)) shouldBe Node(1, Seq(Node(2, Seq(Node(3))), Node(4)))
      Tree.empty[Int] shouldBe Leaf
    }
  }
}
