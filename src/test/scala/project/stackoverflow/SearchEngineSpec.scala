package project.stackoverflow

import java.util.Date

import io.circe.generic.auto._
import org.scalatest.{FunSpec, Matchers}
import project.stackoverflow.SearchEngine.Document
import project.stackoverflow.domain.Question

import scala.util.Success

class SearchEngineSpec extends FunSpec with Matchers {
  val questionPath = "src/main/resources/stackoverflow/questions"
  val documents = List(
    "How can I specify java.library.path for an app built with sbt native packager?",
    "write generic function that calls generic functions in scala",
    "Scala RDF4J application won't terminate"
  )
  val (maxDocuments, query, resultNb) = List(
    (700, "sql", 26),
    (300, "sql", 13),
    (100, "sql", 2)
  )(2)

  def perfStats(name: String, start: Long = 0, read: Long = 0, created: Long = 0, indexed: Long = 0, found: Long = 0): String = {
    def pad(s: String, n: Int): String = s.padTo(n, " ").mkString

    def num(n: Long): String = pad(n.toString.reverse, 6).reverse

    if (indexed == 0)
      s"${pad(name, 21)}: read: ${num(read - start)},                                   found: ${num(found - read)}, total: ${num(found - start)}"
    else if (created == 0)
      s"${pad(name, 21)}: read: ${num(read - start)},                  indexed: ${num(indexed - read)}, found: ${num(found - indexed)}, total: ${num(found - start)}"
    else
      s"${pad(name, 21)}: read: ${num(read - start)}, created: ${num(created - read)}, indexed: ${num(indexed - created)}, found: ${num(found - indexed)}, total: ${num(found - start)}"
  }

  describe("SimpleSearch") {
    import SearchEngine.SimpleSearch._
    it("should make a simple search") {
      search(documents, "java").length shouldBe 1
      search(documents, "scala").length shouldBe 1
      search(documents, "Scala").length shouldBe 1
      search(documents, "generic function").length shouldBe 1
      search(documents, "generic scala").length shouldBe 0
    }
  }
  describe("MultiWordSearch") {
    import SearchEngine.MultiWordSearch._
    it("should make a simple search") {
      search(documents, "generic scala").length shouldBe 1
    }
  }
  describe("TokenizedSearch") {
    import SearchEngine.TokenizedSearch._
    describe("basicTokenizer") {
      it("should split by space") {
        basicTokenizer("Scala RDF4J application won't terminate") shouldBe Seq("Scala", "RDF4J", "application", "won't", "terminate")
      }
    }
    describe("punctuationFilter") {
      it("should remove punctuation") {
        punctuationFilter("Salut !") shouldBe "Salut "
      }
      it("should not remove composed words") {
        punctuationFilter("Jean-Philippe") shouldBe "Jean-Philippe"
      }
    }
    describe("diatricsFilter") {
      it("should remove diatrics") {
        diatricsFilter("àâçéèêëîïöû") shouldBe "aaceeeeiiou"
        diatricsFilter("orčpžsíáýd") shouldBe "orcpzsiayd"
      }
    }
  }
  describe("ReadFiles") {
    import SearchEngine.ReadFiles._
    it("should read questions") {
      loadDb[Question](questionPath).map(_.length) shouldBe Success(700)
    }
  }
  describe("AdapterSearch") {
    import SearchEngine.AdapterSearch._
    describe("questionAdapter") {
      it("should map Question to Document") {
        import SearchEngine.ReadFiles.loadDb
        import SearchEngine.TokenizedSearch.tokenizer
        implicit val t = tokenizer
        implicit val a = questionAdapter _
        val questions = loadDb[Question](questionPath).get.take(maxDocuments)
        search[Question](questions, query).length shouldBe resultNb
      }
    }
  }
  describe("TFIDF") {
    import SearchEngine.TFIDF._
    describe("termCount") {
      it("should count tokens in document") {
        termCount(Document(Unit, Seq("a", "b", "a", "c"))) shouldBe Map("a" -> 2, "b" -> 1, "c" -> 1)
      }
    }
    describe("documentCount") {
      it("should count documents containing the term") {
        documentCount(Seq(Document(Unit, Seq("a", "b", "a", "c")))) shouldBe Map("a" -> 1, "b" -> 1, "c" -> 1)
        documentCount(Seq(
          Document(Unit, Seq("a", "b", "a", "c")),
          Document(Unit, Seq("a", "b", "b"))
        )) shouldBe Map("a" -> 2, "b" -> 2, "c" -> 1)
      }
    }
    describe("search") {
      it("should score results and return them ordered") {
        import SearchEngine.AdapterSearch.questionAdapter
        import SearchEngine.ReadFiles.loadDb
        import SearchEngine.TokenizedSearch.tokenizer
        implicit val t = tokenizer
        implicit val a = questionAdapter _
        val start = new Date().getTime
        val questions = loadDb[Question](questionPath).get.take(maxDocuments)
        val read = new Date().getTime
        val results = search[Question](questions, query)
        val found = new Date().getTime
        println(perfStats("TFIDF", start = start, read = read, found = found))
        results.length shouldBe resultNb
      }
    }
  }
  describe("OnlyOneGlobalScoring") {
    import SearchEngine.OnlyOneGlobalScoring._
    it("should load documents and then search") {
      import SearchEngine.AdapterSearch.questionAdapter
      import SearchEngine.ReadFiles.loadDb
      import SearchEngine.TokenizedSearch.tokenizer
      val start = new Date().getTime
      val questions = loadDb[Question](questionPath).get.take(maxDocuments)
      val read = new Date().getTime
      val db = new SearchEngine[Question](tokenizer, questionAdapter(_)(tokenizer), questions)
      val indexed = new Date().getTime
      val results = db.search(query)
      val found = new Date().getTime
      println(perfStats("OnlyOneGlobalScoring", start = start, read = read, indexed = indexed, found = found))
      results.length shouldBe resultNb
    }
  }
  describe("LoadDocumentsLazily") {
    import SearchEngine.LoadDocumentsLazily._
    it("should load documents lazily") {
      import SearchEngine.AdapterSearch.questionAdapter
      import SearchEngine.ReadFiles.loadDb
      import SearchEngine.TokenizedSearch.tokenizer
      val start = new Date().getTime
      val questions = loadDb[Question](questionPath).get.take(maxDocuments)
      val read = new Date().getTime
      val db = new SearchEngine[Question](tokenizer, questionAdapter(_)(tokenizer))
      val created = new Date().getTime
      db.load(questions)
      val indexed = new Date().getTime
      val results = db.search(query)
      val found = new Date().getTime
      println(perfStats("LoadDocumentsLazily", start = start, read = read, created = created, indexed = indexed, found = found))
      results.length shouldBe resultNb
    }
  }
  describe("IndexDocuments") {
    import SearchEngine.IndexDocuments._
    it("should have quick query") {
      import SearchEngine.AdapterSearch.questionAdapter
      import SearchEngine.ReadFiles.loadDb
      import SearchEngine.TokenizedSearch.tokenizer
      val start = new Date().getTime
      val questions = loadDb[Question](questionPath).get.take(maxDocuments)
      val read = new Date().getTime
      val db = new SearchEngine[Question](tokenizer, questionAdapter(_)(tokenizer))
      val created = new Date().getTime
      db.load(questions)
      val indexed = new Date().getTime
      val results = db.search(query)
      val found = new Date().getTime
      println(perfStats("IndexDocuments", start = start, read = read, created = created, indexed = indexed, found = found))
      results.length shouldBe resultNb
    }
  }
  describe("ParallelRun") {
    import SearchEngine.ParallelRun._
    it("should have very quick query") {
      import SearchEngine.AdapterSearch.questionAdapter
      import SearchEngine.ReadFiles.loadDb
      import SearchEngine.TokenizedSearch.tokenizer
      val start = new Date().getTime
      val questions = loadDb[Question](questionPath).get.take(maxDocuments)
      val read = new Date().getTime
      val db = new SearchEngine[Question](tokenizer, questionAdapter(_)(tokenizer))
      val created = new Date().getTime
      db.load(questions)
      val indexed = new Date().getTime
      val results = db.search(query)
      val found = new Date().getTime
      println(perfStats("ParallelRun", start = start, read = read, created = created, indexed = indexed, found = found))
      results.length shouldBe resultNb
    }
  }

  /**
    * Perf results for 700 files (ms) :
    * .                     read  created  indexed  found
    * TFIDF	                 140			              15072
    * OnlyOneGlobalScoring   158		         16093	   24
    * LoadDocumentsLazily	   183	      4	   15355	   29
    * IndexDocuments	       128	      2	   92080      5
    * ParallelRun	           119	      3	   64792     15
    *
    * TFIDF                : each query will take a long time...
    * OnlyOneGlobalScoring : global scoring is done once on creation so queries are fast
    * LoadDocumentsLazily  : same perf but scoring is not done on creation and we can load more data after
    * IndexDocuments       : creating an other index is slow but speed up querying
    * ParallelRun          : parallel run spead up heavy operations but has a cost, querying on small amount of data is not as effective
    */
}
