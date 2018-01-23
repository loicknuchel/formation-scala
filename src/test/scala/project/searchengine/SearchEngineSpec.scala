package project.searchengine

import java.text.Normalizer

import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Future

class SearchEngineSpec extends FunSpec with Matchers {

  describe("SearchEngine") {
    import SearchEngine._
    describe("step1") {
      type DB = Seq[Document]
      type Document = String
      type Query = String

      def search(db: DB, query: Query): Seq[Document] =
        db.filter(doc => doc.contains(query))

      it("should return results") {
        search(Seq("aa", "bb", "cc"), "a") shouldBe Seq("aa")
      }
    }
    describe("step2") {
      type DB = Seq[Document]
      type Document = String
      type Query = String

      def search(db: DB, query: Query): Seq[Document] =
        db.filter(matcher(_, query.split(" ")))

      def matcher(doc: Document, queries: Seq[Query]): Boolean =
        queries.forall(q => queryMatcher(doc, q))

      def queryMatcher(doc: Document, q: String): Boolean =
        doc.contains(q)

      it("should return results") {
        search(Seq("aa", "bb", "cc", "aab"), "a b") shouldBe Seq("aab")
      }
    }
    describe("step3") {
      type DB = Seq[Document]
      type Document = String
      type Query = String
      type Token = String

      def normalize(in: String): String =
        Normalizer
          .normalize(in, Normalizer.Form.NFD)
          .replaceAll("[^\\p{ASCII}]", "")
          .replaceAll("\\p{P}", "")
          .toLowerCase
          .trim

      def tokenize(in: String): Seq[Token] =
        in.split(" ").map(normalize) //.filter(_.length > 2)

      def search(db: DB, query: Query): Seq[Document] = {
        val queryTokens = tokenize(query)
        db.filter(doc => matcher(doc, queryTokens))
      }

      def matcher(doc: Document, queryTokens: Seq[Token]): Boolean = {
        val docTokens = tokenize(doc)
        queryTokens.forall(queryToken => docTokens.contains(queryToken))
      }

      it("should normalize") {
        normalize("áéőűú,? Sàlù") shouldBe "aeouu salu"
      }
      it("should tokenize") {
        tokenize("áéőűú,? Sàlù l") shouldBe Seq("aeouu", "salu", "l")
        tokenize("a b") shouldBe Seq("a", "b")
      }
      it("should match") {
        matcher("aa", tokenize("a b")) shouldBe false
      }
      it("should return results") {
        search(Seq("aa", "bb", "cc", "aab"), "a b") shouldBe Seq()
        search(Seq("a toto b", "bb", "c ab a", "aab"), "a b") shouldBe Seq("a toto b")
      }
      it("test") {
        import scala.concurrent.ExecutionContext.Implicits.global
        val f = Future.successful(1)
        val g = f.map(i => i + 1)
      }
    }
  }
}
