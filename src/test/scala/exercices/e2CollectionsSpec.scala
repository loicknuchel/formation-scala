package exercices

import org.scalatest.{FunSpec, Matchers}

class e2CollectionsSpec extends FunSpec with Matchers {
  describe("Factoriel") {
    import e2Collections.Factoriel._
    describe("factoriel") {
      it("should return 1 when 1") {
        factoriel(1) shouldBe 1
      }
      it("should be correct for the firsts results") {
        factoriel(2) shouldBe 2
        factoriel(3) shouldBe 6
        factoriel(4) shouldBe 24
        factoriel(5) shouldBe 120
        factoriel(6) shouldBe 720
        factoriel(7) shouldBe 5040
        factoriel(8) shouldBe 40320
        factoriel(9) shouldBe 362880
      }
      it("should return 1 when < 1") {
        factoriel(0) shouldBe 1
        factoriel(-1) shouldBe 1
        factoriel(-678) shouldBe 1
      }
      it("should work for high numbers") {
        factoriel(16) shouldBe 2004189184
      }
      ignore("should throw for too high numbers") {
        assertThrows[IllegalArgumentException] {
          factoriel(20)
        }
      }
    }
  }

  describe("Average") {
    import e2Collections.Average._
    describe("average") {
      it("should perform a simple average") {
        average(List(1, 2, 3)) shouldBe 2
      }
      it("should fail on empty list") {
        assertThrows[UnsupportedOperationException] {
          average(List())
        }
      }
    }
  }

  describe("Size") {
    import e2Collections.Size._
    describe("sizeOfRec") {
      it("should count elements") {
        sizeOfRec(List()) shouldBe 0
        sizeOfRec(List(4)) shouldBe 1
        sizeOfRec(List(4, 1)) shouldBe 2
        sizeOfRec(List(4, 1, 6)) shouldBe 3
      }
    }
    describe("sizeOfFun") {
      it("should count elements") {
        sizeOfFun(List()) shouldBe 0
        sizeOfFun(List(4)) shouldBe 1
        sizeOfFun(List(4, 1)) shouldBe 2
        sizeOfFun(List(4, 1, 6)) shouldBe 3
      }
    }
  }

  describe("TemperatureMap") {
    import e2Collections.TemperatureMap._
    describe("chartFormatImp") {
      it("should format data") {
        chartFormatImp(data) shouldBe results
      }
    }
    describe("chartFormatFun") {
      it("should format data") {
        chartFormatFun(data) shouldBe results
      }
    }
  }

  describe("Devoxx") {
    import project.devoxx.dao.LocalClient
    import helpers.RealFileClient
    import e2Collections.Devoxx._

    val fileClient = new RealFileClient()
    val client = new LocalClient(fileClient, "src/main/resources/devoxx")
    val (speakers, talks, rooms, slots) = client.getModel()

    val talkId = "XPI-0919"
    val talkTitle = "Scala class, bien démarrer avec Scala"
    val speakerId = "09a79f4e4592cf77e5ebf0965489e6c7ec0438cd"
    val roomId = "par224M-225M"

    describe("frenchTalkPercentage") {
      it("should calculate the percentage of french talks") {
        math.round(frenchTalkPercentage(talks) * 100) shouldBe 91
      }
    }
    describe("speakersOfTalk") {
      it("should list speaker for a talk") {
        speakersOfTalk(talks, speakers, talkId).map(_.uuid) shouldBe Seq(speakerId, "1693d28c079e6c28269b9aa86ae04a4549ad3074", "d167a51898267ed3b5913c1789f1ae6110a6ecf5")
      }
    }
    describe("talksOfSpeaker") {
      it("should list talks for a speaker") {
        talksOfSpeaker(speakers, talks, speakerId).map(_.id) shouldBe Seq(talkId)
      }
    }
    describe("roomSchedule") {
      it("should return the schedule for a room") {
        roomSchedule(slots, roomId).length shouldBe 4
      }
    }
    describe("isSpeaking") {
      it("should eventually return the Room where the speaker is at the asked time") {
        isSpeaking(slots, rooms, speakerId, 1491491200000L).map(_.id) shouldBe Some(roomId)
      }
    }
  }

  describe("CustomOption") {
    import e2Collections.CustomOption._
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

  describe("CustomList") {
    import e2Collections.CustomList._
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

  describe("CustomTree") {
    import e2Collections.CustomTree._
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
      Node(1, Seq(Node(2))).filter(_ % 2 == 0) shouldBe Node(2)
      Node(1, Seq(Node(3, Seq(Node(2))), Node(4))).filter(_ % 2 == 0) shouldBe Node(4, Seq(Node(2)))
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
