package exercices.s6Cats

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{FunSpec, Matchers}

class MonadTransformerSpec extends FunSpec with Matchers with ScalaFutures {
  describe("MonadTransformer") {
    import MonadTransformer._
    it("should return the file content") {
      whenReady(downloadFile("path")) { result =>
        result shouldBe Right(
          """id;name
            |1;Alice
            |2;Bob
          """.stripMargin.trim)
      }
    }
  }
}
