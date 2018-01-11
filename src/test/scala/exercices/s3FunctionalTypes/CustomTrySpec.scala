package exercices.s3FunctionalTypes

import org.scalatest.{FunSpec, Matchers}

class CustomTrySpec extends FunSpec with Matchers {
  describe("CustomTry") {
    import CustomTry._
    val error = new IllegalArgumentException

    it("should be created") {
      MyTry[Int](1) shouldBe MySuccess(1)
      MyTry[Int](throw error) shouldBe MyFailure(error)
    }
    describe("isSuccess") {
      it("should return true for MySuccess") {
        MySuccess(1).isSuccess shouldBe true
        MyTry(1).isSuccess shouldBe true
      }
      it("should return false for MyFailure") {
        MyFailure(error).isSuccess shouldBe false
        MyTry(throw error).isSuccess shouldBe false
      }
    }
    describe("isFailure") {
      it("should return false for MySuccess") {
        MySuccess(1).isFailure shouldBe false
        MyTry(1).isFailure shouldBe false
      }
      it("should return true for MyFailure") {
        MyFailure(error).isFailure shouldBe true
        MyTry(throw error).isFailure shouldBe true
      }
    }
    describe("get") {
      it("should return the value for MySuccess") {
        MySuccess(1).get shouldBe 1
        MyTry(1).get shouldBe 1
      }
      it("should throw an exception for MyFailure") {
        an [IllegalArgumentException] should be thrownBy MyFailure(error).get
        an [IllegalArgumentException] should be thrownBy MyTry(throw error).get
      }
    }
    describe("getOrElse") {
      it("should return the value for MySuccess") {
        MySuccess(1).getOrElse(0) shouldBe 1
        MyTry(1).getOrElse(0) shouldBe 1
      }
      it("should return the default value for MyFailure") {
        MyFailure(error).getOrElse(0) shouldBe 0
        MyTry(throw error).getOrElse(0) shouldBe 0
      }
    }
    describe("map") {
      it("should transform the value for MySuccess") {
        MySuccess(1).map(_.toString) shouldBe MySuccess("1")
        MyTry(1).map(_.toString) shouldBe MySuccess("1")
      }
      it("should do nothing for MyFailure") {
        MyFailure(error).map(_.toString) shouldBe MyFailure(error)
        MyTry(throw error).map(_.toString) shouldBe MyFailure(error)
      }
    }
    describe("flatMap") {
      it("should flatten contexts and keep value if both are MySuccess") {
        MySuccess(1).flatMap(i => MySuccess(i.toString)) shouldBe MySuccess("1")
        MyTry(1).flatMap(i => MyTry(i.toString)) shouldBe MySuccess("1")
      }
      it("should ends with a MyFailure if there is one") {
        MySuccess(1).flatMap(i => MyFailure(error)) shouldBe MyFailure(error)
        MyTry(1).flatMap(i => MyTry(throw error)) shouldBe MyFailure(error)

        MyFailure(error).flatMap(i => MySuccess(i.toString)) shouldBe MyFailure(error)
        MyTry(throw error).flatMap(i => MyTry(i.toString)) shouldBe MyFailure(error)

        MyFailure(error).flatMap(i => MyFailure(error)) shouldBe MyFailure(error)
        MyTry(throw error).flatMap(i => MyTry(throw error)) shouldBe MyFailure(error)
      }
    }
    describe("filter") {
      it("should keep the value if predicate is true for MySuccess") {
        MySuccess(1).filter(_ => true) shouldBe MySuccess(1)
      }
      it("should transform to MyFailure if predicate is false for MySuccess") {
        MySuccess(1).filter(_ => false).isFailure shouldBe true
      }
      it("should do nothing for MyFailure") {
        MyFailure(error).filter(_ => true) shouldBe MyFailure(error)
        MyFailure(error).filter(_ => false) shouldBe MyFailure(error)
      }
    }
    describe("exists") {
      it("should return true if predicate is true for MySuccess") {
        MySuccess(1).exists(_ => true) shouldBe true
      }
      it("should return false if predicate is true for MySuccess") {
        MySuccess(1).exists(_ => false) shouldBe false
      }
      it("should return false for MyFailure") {
        MyFailure(error).exists(_ => true) shouldBe false
        MyFailure(error).exists(_ => false) shouldBe false
      }
    }
    describe("toOption") {
      it("should return Some for MySuccess") {
        MySuccess(1).toOption shouldBe Some(1)
      }
      it("should return None for MyFailure") {
        MyFailure(error).toOption shouldBe None
      }
    }
    describe("toList") {
      it("should return a List with one element for MySuccess") {
        MySuccess(1).toList shouldBe List(1)
      }
      it("should return an empty List for MyFailure") {
        MyFailure(error).toList shouldBe List()
      }
    }
  }
}
