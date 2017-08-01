package helpers

import java.io.FileNotFoundException

import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import scala.util.Success

class RealFileClientSpec extends FunSpec with Matchers with BeforeAndAfterEach {
  val testFolder = "src/test/resources/file-client-spec"
  val fileClient: FileClient = new RealFileClient()

  override def beforeEach(): Unit = {
    fileClient.createFolder(testFolder) shouldBe Success(true)
  }

  override def afterEach(): Unit = {
    fileClient.deleteFolder(testFolder) shouldBe Success(true)
  }

  describe("RealFileClient") {
    it("should write, read and delete a file") {
      val path = s"$testFolder/write.txt"
      val content = "Hello World"
      an[FileNotFoundException] should be thrownBy fileClient.read(path).get
      fileClient.write(path, content) shouldBe Success()
      fileClient.read(path) shouldBe Success(content)
      fileClient.delete(path) shouldBe Success(true)
      an[FileNotFoundException] should be thrownBy fileClient.read(path).get
    }

    it("should write a nested file") {
      val path = s"$testFolder/a/b/c/test.txt"
      fileClient.write(path, "") shouldBe Success()
    }

    it("should update a file when written twice") {
      val path = s"$testFolder/test.txt"
      fileClient.write(path, "hello") shouldBe Success()
      fileClient.read(path) shouldBe Success("hello")
      fileClient.write(path, "world") shouldBe Success()
      fileClient.read(path) shouldBe Success("world")
    }

    it("should list files in a folder") {
      fileClient.write(s"$testFolder/1.txt", "") shouldBe Success()
      fileClient.write(s"$testFolder/2.json", "") shouldBe Success()
      fileClient.listFiles(testFolder) shouldBe Success(Seq(s"$testFolder/1.txt", s"$testFolder/2.json"))
    }
  }
}
