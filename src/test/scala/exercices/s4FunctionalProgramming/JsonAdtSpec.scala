package exercices.s4FunctionalProgramming

import org.scalatest.{FunSpec, Matchers}

class JsonAdtSpec extends FunSpec with Matchers {
  describe("JsonAdt") {
    import JsonAdt._
    describe("JsArray") {
      it("should have alternative constructor") {
        JsArray(Seq()) shouldBe JsArray()
        JsArray(Seq(JsString("a"))) shouldBe JsArray(JsString("a"))
        JsArray(Seq(JsString("a"), JsNumber(1))) shouldBe JsArray(JsString("a"), JsNumber(1))
        JsArray(Seq(JsString("a"), JsNumber(1), JsBoolean(true))) shouldBe JsArray(JsString("a"), JsNumber(1), JsBoolean(true))
      }
    }
    describe("JsObject") {
      it("should have alternative constructors") {
        JsObject("a" -> JsNull, "b" -> JsNumber(1)) shouldBe JsObject(Map("a" -> JsNull, "b" -> JsNumber(1)))
        JsObject(Seq()) shouldBe JsObject()
        JsObject(Seq("a" -> JsString("a"))) shouldBe JsObject("a" -> JsString("a"))
        JsObject(Seq("a" -> JsString("a"), "b" -> JsNumber(1))) shouldBe JsObject("a" -> JsString("a"), "b" -> JsNumber(1))
        JsObject(Seq("a" -> JsString("a"), "b" -> JsNumber(1), "c" -> JsBoolean(true))) shouldBe JsObject("a" -> JsString("a"), "b" -> JsNumber(1), "c" -> JsBoolean(true))
      }
    }
    describe("stringify") {
      it("should stringify a JsValue") {
        JsNull.stringify shouldBe "null"
        JsBoolean(true).stringify shouldBe "true"
        JsBoolean(false).stringify shouldBe "false"
        JsNumber(1).stringify shouldBe "1"
        JsNumber(0).stringify shouldBe "0"
        JsNumber(-1).stringify shouldBe "-1"
        JsString("a").stringify shouldBe "\"a\""
        JsString("toto").stringify shouldBe "\"toto\""
        JsArray(JsNull, JsNumber(1), JsString("a")).stringify shouldBe "[null,1,\"a\"]"
        JsObject("a" -> JsNumber(1), "b" -> JsBoolean(true)).stringify shouldBe "{\"a\":1,\"b\":true}"
      }
    }
    describe("from") {
      it("should wrap scala values in JsValue") {
        JsValue.from(null) shouldBe Some(JsNull)
        JsValue.from(true) shouldBe Some(JsBoolean(true))
        JsValue.from(1) shouldBe Some(JsNumber(1))
        JsValue.from(1l) shouldBe Some(JsNumber(1))
        JsValue.from(1f) shouldBe Some(JsNumber(1))
        JsValue.from(1d) shouldBe Some(JsNumber(1))
        JsValue.from("a") shouldBe Some(JsString("a"))
        JsValue.from(Seq()) shouldBe Some(JsArray())
        JsValue.from(Seq(1, 2, 3)) shouldBe Some(JsArray(JsNumber(1), JsNumber(2), JsNumber(3)))
        JsValue.from(Seq("a" -> 1, "b" -> 2)) shouldBe Some(JsObject("a" -> JsNumber(1), "b" -> JsNumber(2)))
        JsValue.from(Map("a" -> 1, "b" -> 2)) shouldBe Some(JsObject("a" -> JsNumber(1), "b" -> JsNumber(2)))
      }
    }
  }
}
