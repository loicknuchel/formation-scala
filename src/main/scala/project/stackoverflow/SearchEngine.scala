package project.stackoverflow

import java.io.File
import java.text.Normalizer

import helpers.{RealFileClient, TryUtils}
import io.circe.{Decoder, parser}
import project.stackoverflow.dao.LocalClient
import project.stackoverflow.domain.Question

import scala.collection.parallel.{ParMap, ParSeq}
import scala.io.Source
import scala.util.Try

object SearchEngine {
  type Token = String
  type Tokenizer = String => Seq[Token]
  type Adapter[A] = A => Document[A]

  case class Document[A](doc: A, tokens: Seq[Token])

  val fileClient = new RealFileClient()
  val repo = new LocalClient(fileClient, "src/main/resources/stackoverflow")

  object SimpleSearch {
    def search(db: Seq[String], query: String): Seq[String] =
      db.filter(_.contains(query))
  }

  object MultiWordSearch {
    def search(db: Seq[String], query: String): Seq[String] =
      db.filter(d => query.split(" ").forall(q => d.contains(q)))
  }

  object TokenizedSearch {
    type Doc = String
    type TokenSegmenter = String => Seq[Token]
    type TokenTransformer = Token => Token
    type TokenFilter = Token => Boolean

    val basicTokenizer: Tokenizer = (doc: Doc) => doc.split(" ")

    val zeroSegmenter: TokenSegmenter = (t: Token) => Seq(t)
    val zeraTransformer: TokenTransformer = (t: Token) => t
    val zeroFilter: TokenFilter = (t: Token) => true

    val spaceSegmenter: TokenSegmenter = (t: Token) => t.split(" ")

    val punctuationFilter: TokenTransformer = (t: Token) => t.replaceAll("""[\p{Punct}&&[^-]]""", "")
    val diatricsFilter: TokenTransformer = (t: Token) => Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
    val lowercaseFilter: TokenTransformer = (t: Token) => t.toLowerCase
    val trimFilter: TokenTransformer = (t: Token) => t.trim

    val smallFilter: TokenFilter = (t: Token) => t.size > 2

    def tokenizerBuilder(segmenters: Seq[TokenSegmenter] = Seq(zeroSegmenter),
                         transformers: Seq[TokenTransformer] = Seq(zeraTransformer),
                         filters: Seq[TokenFilter] = Seq(zeroFilter)): Tokenizer = (doc: Doc) => {
      val s: TokenSegmenter = segmenters.reduceLeft((s1, s2) => (t: Token) => s1(t).flatMap(s2))
      val t: TokenTransformer = transformers.reduceLeft((t1, t2) => t2.compose(t1))
      val f: TokenFilter = filters.reduce((f1, f2) => (t: Token) => f1(t) && f2(t))
      s(doc).map(t).filter(f)
    }

    val tokenizer: Tokenizer = tokenizerBuilder(
      segmenters = Seq(basicTokenizer),
      transformers = Seq(punctuationFilter, diatricsFilter, lowercaseFilter, trimFilter),
      filters = Seq(smallFilter)
    )

    def search(db: Seq[String], query: String)(implicit tokenizer: Tokenizer): Seq[String] = {
      val queryTokens = tokenizer(query)
      db.filter { doc =>
        val docTokens = tokenizer(doc)
        queryTokens.forall(q => docTokens.contains(q))
      }
    }
  }

  object ReadFiles {
    def loadDb[A](path: String)(implicit decoder: Decoder[A]): Try[Seq[A]] = {
      def listFiles(path: String): Try[Seq[String]] = Try(new File(path).listFiles.toSeq.map(_.getPath))

      def read(path: String): Try[String] = Try(Source.fromFile(path).mkString)

      listFiles(path)
        .flatMap { paths => TryUtils.sequence(paths.map(read)) }
        .flatMap { files => TryUtils.sequence(files.map(file => parser.decode[A](file).toTry)) }
    }
  }

  object AdapterSearch {
    def questionAdapter(q: Question)(implicit tokenizer: Tokenizer): Document[Question] = Document(
      doc = q,
      tokens = tokenizer(q.title) ++ tokenizer(q.body)
    )

    def search[A](db: Seq[A], query: String)(implicit tokenizer: Tokenizer, adapter: Adapter[A]): Seq[A] = {
      val queryTokens = tokenizer(query)
      db.filter { doc =>
        val docTokens = adapter(doc).tokens
        queryTokens.forall(q => docTokens.contains(q))
      }
    }
  }

  object TFIDF {
    def search[A](db: Seq[A], query: String)(implicit tokenizer: Tokenizer, adapter: Adapter[A]): Seq[(A, Double)] = {
      val docs = db.map(adapter)
      val df = documentFrequency(docs)
      docs
        .map(score(tokenizer(query), df))
        .filter(_._2 > 0)
        .sortBy(-_._2)
    }

    def score[A](queryTokens: Seq[Token], df: Map[Token, Double])(doc: Document[A]): (A, Double) = {
      val tf = termFrequency(doc)
      val score = queryTokens.map(tfidf(tf, df, _).getOrElse(0d)).product
      (doc.doc, score)
    }

    def tfidf(tf: Map[Token, Double], df: Map[Token, Double], token: Token): Option[Double] =
      for {
        t <- tf.get(token)
        d <- df.get(token)
      } yield t / d

    // count of documents containing the token
    def documentCount[A](docs: Seq[Document[A]]): Map[Token, Int] =
      docs.foldLeft(Map.empty[Token, Int]) { (dc, doc) =>
        sum(dc, termCount(doc).mapValues(_ => 1))
      }

    // frequency of documents containing the token
    def documentFrequency[A](docs: Seq[Document[A]]): Map[Token, Double] =
      documentCount(docs).mapValues(count => count.toDouble / docs.length)

    // count of token occurences in document
    def termCount[A](doc: Document[A]): Map[Token, Int] =
      doc.tokens.groupBy(identity).mapValues(_.length)

    // frequency of the token in the document
    def termFrequency[A](doc: Document[A]): Map[Token, Double] =
      termCount(doc).mapValues(count => count.toDouble / doc.tokens.length)


    def merge[A, B](f: (Option[B], Option[B]) => Option[B])(m1: Map[A, B], m2: Map[A, B]): Map[A, B] =
      (m1.keySet ++ m2.keySet).flatMap(key => f(m1.get(key), m2.get(key)).map(v => (key, v))).toMap

    def sum: (Map[String, Int], Map[String, Int]) => Map[String, Int] =
      merge[String, Int]((v1, v2) => Some(v1.getOrElse(0) + v2.getOrElse(0)))
  }

  object OnlyOneGlobalScoring {

    import TFIDF.{documentFrequency, score}

    class SearchEngine[A](tokenizer: Tokenizer, adapter: Adapter[A], db: Seq[A]) {
      private val docs: Seq[Document[A]] = db.map(adapter)
      private val df: Map[Token, Double] = documentFrequency(docs)

      def search(query: String): Seq[(A, Double)] = {
        docs
          .map(score(tokenizer(query), df))
          .filter(_._2 > 0)
          .sortBy(-_._2)
      }
    }

  }

  object LoadDocumentsLazily {

    import TFIDF.{documentFrequency, merge, score}

    class SearchEngine[A](tokenizer: Tokenizer, adapter: Adapter[A]) {
      private var docs: Seq[Document[A]] = Seq()
      private var df: Map[Token, Double] = Map()

      def load(db: Seq[A]): Unit = {
        val newDocs = db.map(adapter)
        df = sum(
          df.mapValues(_ * docs.length),
          documentFrequency(newDocs).mapValues(_ * newDocs.length)
        ).mapValues(_ / (docs.length + newDocs.length))
        docs = docs ++ newDocs
      }

      def search(query: String): Seq[(A, Double)] = {
        docs
          .map(score(tokenizer(query), df))
          .filter(_._2 > 0)
          .sortBy(-_._2)
      }
    }

    def sum: (Map[String, Double], Map[String, Double]) => Map[String, Double] =
      merge[String, Double]((v1, v2) => Some(v1.getOrElse(0d) + v2.getOrElse(0d)))
  }

  object IndexDocuments {

    import LoadDocumentsLazily.sum
    import TFIDF.{documentFrequency, merge, score}

    class SearchEngine[A](tokenizer: Tokenizer, adapter: Adapter[A]) {
      private var docs: Seq[Document[A]] = Seq()
      private var index: Map[Token, Seq[Document[A]]] = Map()
      private var df: Map[Token, Double] = Map()

      def load(db: Seq[A]): Unit = {
        val newDocs = db.map(adapter)
        df = sum(
          df.mapValues(_ * docs.length),
          documentFrequency(newDocs).mapValues(_ * newDocs.length)
        ).mapValues(_ / (docs.length + newDocs.length))
        index = aggregate(index, indexDocs(newDocs))
        docs = docs ++ newDocs
      }

      def search(query: String): Seq[(A, Double)] = {
        val queryTokens = tokenizer(query)
        val matchingDocs = queryTokens.flatMap(t => index.get(t)).flatten.distinct
        matchingDocs
          .map(score(queryTokens, df))
          .sortBy(-_._2)
      }
    }

    def indexDocs[A](docs: Seq[Document[A]]): Map[Token, Seq[Document[A]]] = {
      docs.foldLeft(Map.empty[Token, Seq[Document[A]]]) { (acc, doc) =>
        aggregate(acc, doc.tokens.distinct.groupBy(identity).mapValues(_ => Seq(doc)))
      }
    }

    def aggregate[A]: (Map[Token, Seq[Document[A]]], Map[Token, Seq[Document[A]]]) => Map[Token, Seq[Document[A]]] =
      merge[Token, Seq[Document[A]]]((v1, v2) => Some(v1.getOrElse(Seq()) ++ v2.getOrElse(Seq())))
  }

  object ParallelRun {

    class SearchEngine[A](tokenizer: Tokenizer, adapter: Adapter[A]) {
      private var docs: Seq[Document[A]] = Seq()
      private var index: Map[Token, Seq[Document[A]]] = Map()
      private var df: Map[Token, Double] = Map()

      def load(db: Seq[A]): Unit = {
        val newDocs = db.par.map(adapter)
        df = sumDouble(
          df.par.mapValues(_ * docs.length),
          documentFrequency(newDocs).mapValues(_ * newDocs.length)
        ).mapValues(_ / (docs.length + newDocs.length)).seq.toMap
        index = aggregate(index.par, indexDocs(newDocs)).seq.toMap
        docs = docs ++ newDocs
      }

      def search(query: String): Seq[(A, Double)] = {
        val queryTokens = tokenizer(query).par
        val matchingDocs = queryTokens.flatMap(t => index.get(t)).flatten.distinct
        matchingDocs
          .map(score(queryTokens, df))
          .seq
          .sortBy(-_._2)
      }
    }

    def score[A](queryTokens: ParSeq[Token], df: Map[Token, Double])(doc: Document[A]): (A, Double) = {
      val tf = termFrequency(doc)
      val score = queryTokens.map(tfidf(tf, df, _).getOrElse(0d)).product
      (doc.doc, score)
    }

    def tfidf(tf: ParMap[Token, Double], df: Map[Token, Double], token: Token): Option[Double] = {
      for {
        t <- tf.get(token)
        d <- df.get(token)
      } yield t / d
    }

    // count of documents containing the token
    def documentCount[A](docs: ParSeq[Document[A]]): ParMap[Token, Int] =
      docs.foldLeft(ParMap.empty[Token, Int]) { (dc, doc) =>
        sumInt(dc, termCount(doc).mapValues(_ => 1))
      }

    // frequency of documents containing the token
    def documentFrequency[A](docs: ParSeq[Document[A]]): ParMap[Token, Double] =
      documentCount(docs).map { case (token, count) => (token, count.toDouble / docs.length) }

    // count of token occurences in document
    def termCount[A](doc: Document[A]): ParMap[Token, Int] =
      doc.tokens.par.groupBy(identity).mapValues(_.length)

    // frequency of the token in the document
    def termFrequency[A](doc: Document[A]): ParMap[Token, Double] =
      termCount(doc).mapValues(count => count.toDouble / doc.tokens.length)

    def indexDocs[A](docs: ParSeq[Document[A]]): ParMap[Token, Seq[Document[A]]] =
      docs.foldLeft(ParMap.empty[Token, Seq[Document[A]]]) { (acc, doc) =>
        aggregate(acc, doc.tokens.par.distinct.groupBy(identity).mapValues(_ => Seq(doc)))
      }

    def aggregate[A]: (ParMap[Token, Seq[Document[A]]], ParMap[Token, Seq[Document[A]]]) => ParMap[Token, Seq[Document[A]]] =
      merge[Token, Seq[Document[A]]]((v1, v2) => Some(v1.getOrElse(Seq()) ++ v2.getOrElse(Seq())))

    def sumInt: (ParMap[Token, Int], ParMap[Token, Int]) => ParMap[Token, Int] =
      merge[Token, Int]((v1, v2) => Some(v1.getOrElse(0) + v2.getOrElse(0)))

    def sumDouble: (ParMap[Token, Double], ParMap[Token, Double]) => ParMap[Token, Double] =
      merge[String, Double]((v1, v2) => Some(v1.getOrElse(0d) + v2.getOrElse(0d)))

    def merge[A, B](f: (Option[B], Option[B]) => Option[B])(m1: ParMap[A, B], m2: ParMap[A, B]): ParMap[A, B] =
      (m1.keySet ++ m2.keySet).flatMap(key => f(m1.get(key), m2.get(key)).map(v => (key, v))).toMap
  }

  /**
    * Add more features :
    *   - typos tolerant
    *     - distance de Levenshtein
    *     - fuzzy matching
    *   - customize search with custom weights (title != body)
    */
}
