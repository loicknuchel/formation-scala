package exercices.s5Advanced

import org.scalatest.{FunSpec, Matchers}

class JsonImplicitsSpec extends FunSpec with Matchers {
  describe("JsonImplicits") {
    import JsonImplicits._
  }
  /*describe("ParametreImplicite") {
    import .JSON._
    import e5Advanced.ParametreImplicite._
    it("should convert basic type using implicit parameter") {
      Json.from(null) shouldBe JsNull
      Json.from(true) shouldBe JsBoolean(true)
      Json.from(1) shouldBe JsNumber(1)
      Json.from(1l) shouldBe JsNumber(1)
      Json.from(1f) shouldBe JsNumber(1)
      Json.from(1d) shouldBe JsNumber(1)
      Json.from("a") shouldBe JsString("a")
      Json.from(Seq(1, 2, 3)) shouldBe JsArray(JsNumber(1), JsNumber(2), JsNumber(3))
      Json.from(Map("a" -> 1, "b" -> 2)) shouldBe JsObject("a" -> JsNumber(1), "b" -> JsNumber(2))
    }
    it("should convert composite types using implicit parameter") {
      Json.from(Some(1): Option[Int]) shouldBe JsNumber(1)
      Json.from(None: Option[Int]) shouldBe JsNull
    }
    it("should convert custom object using implicit parameter") {
      Json.from(User(1, "Loïc")).stringify shouldBe """{"id":1,"name":"Loïc"}"""
      Json.from(Seq(User(1, "Loïc"), User(2, "Jean"))).stringify shouldBe """[{"id":1,"name":"Loïc"},{"id":2,"name":"Jean"}]"""
    }
  }

  describe("ClasseImplicite") {
    import .JSON._
    import e5Advanced.ClasseImplicite._

  }

  describe("FonctionImplicite") {
    import .JSON._
    import e5Advanced.FonctionImplicite.Json._
    it("should convert basic types implicitly") {
      (true: JsValue) shouldBe JsBoolean(true)
      (1: JsValue) shouldBe JsNumber(1)
      ("a": JsValue) shouldBe JsString("a")
      //(Seq(1): JsValue) shouldBe JsArray(JsNumber(1))
    }
  }*/
}
