package project.stackoverflow

import helpers.FakeFileClient
import io.circe.ParsingFailure
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}
import project.stackoverflow.dao.LocalClient
import project.stackoverflow.domain.{Comment, UserEmbed}

import scala.util.{Failure, Success}

class LocalClientSpec extends FunSpec with Matchers with BeforeAndAfterEach {
  val fileClient = new FakeFileClient()
  val localClient = new LocalClient(fileClient, "path")

  val user = UserEmbed(1, "test", "name", "avatar", "url", 1, None)
  val comment = Comment(1, 2, "body", user, 3, edited = false, 4)
  val commentJson =
    """{
      |  "comment_id" : 1,
      |  "post_id" : 2,
      |  "body" : "body",
      |  "owner" : {
      |    "user_id" : 1,
      |    "user_type" : "test",
      |    "display_name" : "name",
      |    "profile_image" : "avatar",
      |    "link" : "url",
      |    "reputation" : 1,
      |    "accept_rate" : null
      |  },
      |  "score" : 3,
      |  "edited" : false,
      |  "creation_date" : 4
      |}
    """.stripMargin.trim

  override def beforeEach(): Unit = {
    fileClient.init()
  }

  describe("write comment") {
    it("should write json in required path") {
      localClient.write(comment).isSuccess shouldBe true
      fileClient.fs shouldBe Map("path/comments/1.json" -> Success(commentJson))
    }

    it("should fail if fileClient fail") {
      fileClient.fs = null
      an[NullPointerException] should be thrownBy localClient.write(comment).get
    }
  }

  describe("readComments") {
    it("should fail if a file is badly encoded") {
      fileClient.fs = Map("test.json" -> Success(""))
      an[ParsingFailure] should be thrownBy localClient.getComments().get
    }

    it("should ignore files with extension different from .json") {
      fileClient.fs = Map(
        "test.txt" -> Success(""),
        "test.json" -> Success(commentJson)
      )
      localClient.getComments() shouldBe Success(Seq(comment))
    }

    it("should fail if a file can't be read") {
      val err = new Exception("fail")
      fileClient.fs = Map(
        "test.json" -> Failure(err)
      )
      localClient.getComments() shouldBe Failure(err)
    }
  }
}
