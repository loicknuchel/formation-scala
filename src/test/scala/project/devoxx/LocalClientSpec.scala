package project.devoxx

import helpers.RealFileClient
import project.devoxx.dao.LocalClient
import org.scalatest.{FunSpec, Matchers}

import scala.util.Success

class LocalClientSpec extends FunSpec with Matchers {
  val fileClient = new RealFileClient()
  val localClient = new LocalClient(fileClient, "src/main/resources/devoxx")

  describe("Rooms") {

  }
  describe("Speakers") {

  }
  describe("Talks") {
    it("should read talks") {
      localClient.getTalks().map(_.length) shouldBe Success(236)
    }
    it("should read a talk") {
      localClient.getTalk("AHF-1145").map(_.id) shouldBe Success("AHF-1145")
    }
  }
  describe("Slots") {

  }
}
