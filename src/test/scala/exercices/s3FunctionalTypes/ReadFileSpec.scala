package exercices.s3FunctionalTypes

import org.scalatest.{FunSpec, Matchers}

import scala.util.{Failure, Success}

class ReadFileSpec extends FunSpec with Matchers {
  val path = "src/main/resources/read-file/users.csv"

  describe("ReadFile") {
    import ReadFile._
    describe("readFile") {
      it("should return a failure if file does not exists") {
        readFile("src/main/resources/read-file/notfound.csv").isFailure shouldBe true
      }
      it("should read file line by line") {
        readFile(path).map(_.length) shouldBe Success(101)
      }
    }
    describe("parseFile") {
      it("should parse lines with headers") {
        parseFile(path) match {
          case Failure(e) => fail(e.getMessage)
          case Success(lines) =>
            lines.length shouldBe 100
            lines.head.get("first_name") shouldBe Some("Meg")
        }
      }
    }
    describe("formatLine") {
      val line = Map(
        "id" -> "1",
        "first_name" -> "Meg",
        "last_name" -> "Leonida",
        "email" -> "mleonida0@wordpress.org",
        "gender" -> "Female",
        "ip_address" -> "68.238.178.152")
      val user = User(1, "Meg", "Leonida", "mleonida0@wordpress.org", "Female", Some("68.238.178.152"))
      it("should parse a line") {
        formatLine(line) shouldBe Success(user)
      }
      it("should should fail if id is not a number") {
        formatLine(line + ("id" -> "a")).isFailure shouldBe true
      }
      it("should should not fail if ip_address is absent") {
        formatLine(line - "ip_address") shouldBe Success(user.copy(ip = None))
      }
      it("should should not fail if ip_address is empty") {
        formatLine(line + ("ip_address" -> "")) shouldBe Success(user.copy(ip = None))
      }
    }
    describe("formatFile") {
      it("should return valid and invalid results with line index") {
        val (users, errors) = formatFile(path)
        users.length shouldBe 98
        errors.length shouldBe 2
        errors.map { case (i, e) => (i, e.getMessage) } shouldBe Seq(
          (19, "For input string: \"19a\""),
          (20, "For input string: \"\""))
      }
    }
  }
}
