package project.searchengine

import java.util.Date

import org.scalatest.{FunSpec, Matchers}

class SearchEngineSpec extends FunSpec with Matchers {
  describe("SearchEngine") {
    import SearchEngine._
    describe("BasicSearch") {
      import SearchEngine.BasicSearch._
      it("should search query in documents") {
        search(Seq("a", "b", "ab"))("a") shouldBe Seq("a", "ab")
        search(Seq("a", "b", "ba"))("a b") shouldBe Seq()
      }
    }
    describe("MultiWordSearch") {
      import SearchEngine.MultiWordSearch._
      it("should search all query words in documents") {
        search(Seq("a", "b", "ba"))("a b") shouldBe Seq("ba")
      }
    }
    describe("TokenizedSearch") {
      import SearchEngine.TokenizedSearch._
      it("should normalize a token") {
        normalize("àâçéèêëîïöû") shouldBe "aaceeeeiiou"
        normalize("orčpžsíáýd") shouldBe "orcpzsiayd"
      }
      it("should search all query tokens in document tokens") {
        search(Seq("a", "b", "ba"))("a b") shouldBe Seq()
        search(Seq("a", "b", "b a"))("a b") shouldBe Seq("b a")
      }
    }

    val db = SearchEngine.loadDb().get

    describe("AdapterSearch") {
      import SearchEngine.AdapterSearch._
      it("should search for custom documents") {
        search(questionAdapter, db)("scala").length shouldBe 1
      }
    }
    describe("TFIDF") {
      import SearchEngine.TFIDF._
      it("should rank search results") {
        search(questionAdapter, db.take(100))("sql").length shouldBe 6
      }
    }
    describe("GlobalScoring") {
      import SearchEngine.GlobalScoring._
      it("should calculate df once and the search fast") {
        val engine = new SearchEngine(questionAdapter, db.take(100))
        engine.search("sql").length shouldBe 6
      }
    }
    describe("LazyLoad") {
      import SearchEngine.LazyLoad._
      it("should be able to lazily add more documents to db") {
        val engine = new SearchEngine(questionAdapter)
        engine.load(db.take(100))
        engine.search("sql").length shouldBe 6
      }
    }
    describe("IndexDocuments") {
      import SearchEngine.IndexDocuments._
      it("should index documents to search very fast") {
        val engine = new SearchEngine(questionAdapter)
        engine.load(db.take(100))
        engine.search("sql").length shouldBe 6
      }
    }
    describe("ParallelRun") {
      import SearchEngine.ParallelRun._
      it("should run operations in parallel to speed up heavy calculations") {
        val engine = new SearchEngine(questionAdapter)
        engine.load(db.take(100))
        engine.search("sql").length shouldBe 6
      }
    }
    describe("Perf") {
      ignore("should run all methods and show perf report") {
        def perfStats(name: String, start: Long = 0, created: Long = 0, indexed: Long = 0, found: Long = 0): String = {
          def pad(s: String, n: Int): String = s.padTo(n, " ").mkString

          def num(n: Long): String = pad(n.toString.reverse, 7).reverse

          s"${pad(name, 14)} ${num(created - start)}  ${num(indexed - created)}  ${num(found - indexed)}  ${num(found - start)}"
        }

        println("method         created  indexed    found    total")

        val startTFIDF = new Date().getTime
        // no create
        val createdTFIDF = new Date().getTime
        // no load
        val indexedTFIDF = new Date().getTime
        val resultsTFIDF = TFIDF.search(questionAdapter, db)("sql")
        val foundTFIDF = new Date().getTime
        println(perfStats("TFIDF", startTFIDF, createdTFIDF, indexedTFIDF, foundTFIDF))
        resultsTFIDF.length shouldBe 20

        val startGlobal = new Date().getTime
        val engineGlobal = new GlobalScoring.SearchEngine(questionAdapter, db)
        val createdGlobal = new Date().getTime
        // load done
        val indexedGlobal = new Date().getTime
        val resultsGlobal = engineGlobal.search("sql")
        val foundGlobal = new Date().getTime
        println(perfStats("GlobalScoring", startGlobal, createdGlobal, indexedGlobal, foundGlobal))
        resultsGlobal.length shouldBe 20

        val startLazy = new Date().getTime
        val engineLazy = new LazyLoad.SearchEngine[Question](questionAdapter)
        val createdLazy = new Date().getTime
        engineLazy.load(db)
        val indexedLazy = new Date().getTime
        val resultsLazy = engineLazy.search("sql")
        val foundLazy = new Date().getTime
        println(perfStats("LazyLoad", startLazy, createdLazy, indexedLazy, foundLazy))
        resultsLazy.length shouldBe 20

        val startIndex = new Date().getTime
        val engineIndex = new IndexDocuments.SearchEngine[Question](questionAdapter)
        val createdIndex = new Date().getTime
        engineIndex.load(db)
        val indexedIndex = new Date().getTime
        val resultsIndex = engineIndex.search("sql")
        val foundIndex = new Date().getTime
        println(perfStats("IndexDocuments", startIndex, createdIndex, indexedIndex, foundIndex))
        resultsIndex.length shouldBe 20

        val startPar = new Date().getTime
        val enginePar = new ParallelRun.SearchEngine[Question](questionAdapter)
        val createdPar = new Date().getTime
        enginePar.load(db)
        val indexedPar = new Date().getTime
        val resultsPar = enginePar.search("sql")
        val foundPar = new Date().getTime
        println(perfStats("ParallelRun", startPar, createdPar, indexedPar, foundPar))
        resultsPar.length shouldBe 20

        /**
          * Perf results for 700 files (ms) :
          * method         created  indexed    found
          * TFIDF                0        0    18258
          * GlobalScoring    14320        0       44
          * LazyLoad             3    14160       43
          * IndexDocuments       2   138633        5
          * ParallelRun         93    92522        6
          *
          * TFIDF          : each query will take a long time...
          * GlobalScoring  : global scoring is done once on creation so queries are fast
          * LazyLoad       : same perf but scoring is not done on creation and we can load more data after
          * IndexDocuments : creating an other index is slow but speed up querying
          * ParallelRun    : parallel run spead up heavy operations but has a cost, querying on small amount of data is not as effective
          */
      }
    }
  }
}
