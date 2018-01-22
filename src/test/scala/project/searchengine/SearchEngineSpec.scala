package project.searchengine

import org.scalatest.{FunSpec, Matchers}

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
  }
}
