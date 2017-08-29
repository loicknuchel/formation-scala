package project.searchengine

import java.text.Normalizer

import scala.collection.parallel.{ParMap, ParSeq}
import scala.util.Try

object SearchEngine {
  /**
    * Code ici le moteur un moteur de recherche.
    *   - étape 1: code un simple fonction qui cherche une requête dans un ensemble de documents
    *       def search(db: Seq[String], query: String): Seq[String]
    *   - étape 2: permet une recherche multi-mots, c'est à dire chercher les mots de la requête indépendemment les uns des autres
    *   - étape 3: crée une fonction de tokenization pour comparer les tokens présents dans la requête et les documents
    *   - étape 4: implémente une recherche pour un type générique A en utilisant un adapteur
    *       def search[A](db: Seq[A], query: String, adapter: A => Seq[String]): Seq[A]
    *   - étape 5: améliore la pertinence de la recherche en utilisant l'algorithme TFIDF
    *   - étape 6: attention aux performances !!!
    */

  object BasicSearch {
    def search(db: Seq[String])(query: String): Seq[String] = {
      db.filter(_.contains(query))
    }
  }

  object MultiWordSearch {
    def search(db: Seq[String])(query: String): Seq[String] = {
      val tokens = query.split(" ").toSeq
      db.filter(d => tokens.forall(q => d.contains(q)))
    }
  }

  type Doc = String
  type Query = String
  type Token = String

  def normalize(in: Token): Token = {
    Normalizer.normalize(in, Normalizer.Form.NFD)
      .replaceAll("[^\\p{ASCII}]", "")
      .replaceAll("""[\p{Punct}&&[^-]]""", "")
      .toLowerCase
      .trim
  }

  def tokenize(doc: Doc): Seq[Token] = {
    doc.split(" ").map(normalize)
  }

  def hasTokens(doc: Seq[Token], queryTokens: Seq[Token]): Boolean = {
    queryTokens.forall(token => doc.contains(token))
  }

  object TokenizedSearch {
    def search(db: Seq[Doc])(query: Query): Seq[Doc] = {
      val queryTokens = tokenize(query)
      db.filter { doc => hasTokens(tokenize(doc), queryTokens) }
    }
  }

  object AdapterSearch {
    def search[A](adapter: A => Seq[Token], db: Seq[A])(query: String): Seq[A] = {
      val queryTokens = tokenize(query)
      db.filter { doc => hasTokens(adapter(doc), queryTokens) }
    }

    def questionAdapter(q: Question): Seq[Token] = {
      tokenize(q.body)
    }
  }

  case class Document[A](doc: A, tokens: Seq[String])

  def questionAdapter(q: Question): Document[Question] = {
    Document(q, tokenize(q.body))
  }

  // count of token occurences in document
  def termCount(doc: Seq[Token]): Map[Token, Int] =
    doc.groupBy(identity).mapValues(_.length)

  // count of documents containing the token
  def documentCount[A](docs: Seq[Seq[Token]]): Map[Token, Int] =
    docs.foldLeft(Map.empty[Token, Int]) { (dc, doc) =>
      sum(dc, termCount(doc).mapValues(_ => 1))
    }

  // frequency of the token in the document
  def termFrequency[A](doc: Seq[Token]): Map[Token, Double] =
    termCount(doc).mapValues(count => count.toDouble / doc.length)

  // frequency of documents containing the token
  def documentFrequency[A](docs: Seq[Seq[Token]]): Map[Token, Double] =
    documentCount(docs).mapValues(count => count.toDouble / docs.length)

  def tfidf(df: Map[Token, Double], tf: Map[Token, Double], token: Token): Option[Double] =
    for {
      t <- tf.get(token)
      d <- df.get(token)
    } yield t / d

  def score[A](df: Map[Token, Double], queryTokens: Seq[Token], doc: Seq[Token]): Double = {
    val tf = termFrequency(doc)
    queryTokens.map(queryToken => tfidf(df, tf, queryToken).getOrElse(0d)).product
  }

  def merge[A, B](f: (Option[B], Option[B]) => Option[B])(m1: Map[A, B], m2: Map[A, B]): Map[A, B] =
    (m1.keySet ++ m2.keySet).flatMap(key => f(m1.get(key), m2.get(key)).map(v => (key, v))).toMap

  def sum[A, B](m1: Map[A, B], m2: Map[A, B])(implicit num: Numeric[B]): Map[A, B] =
    merge[A, B]((v1, v2) => Some(num.plus(v1.getOrElse(num.zero), v2.getOrElse(num.zero))))(m1, m2)

  object TFIDF {
    def search[A](adapter: A => Document[A], db: Seq[A])(query: String): Seq[(A, Double)] = {
      val docs = db.map(adapter)
      val df = documentFrequency(docs.map(_.tokens))
      val queryTokens = tokenize(query)
      val a = Seq(1).sum
      docs
        .map { doc => (doc.doc, score(df, queryTokens, doc.tokens)) }
        .filter(_._2 > 0)
        .sortBy(-_._2)
    }
  }

  object GlobalScoring {

    class SearchEngine[A](adapter: A => Document[A], db: Seq[A]) {
      private val docs: Seq[Document[A]] = db.map(adapter)
      private val df: Map[Token, Double] = documentFrequency(docs.map(_.tokens))

      def search(query: String): Seq[(A, Double)] = {
        val queryTokens = tokenize(query)
        docs
          .map { doc => (doc.doc, score(df, queryTokens, doc.tokens)) }
          .filter(_._2 > 0)
          .sortBy(-_._2)
      }
    }

  }

  def addDocs(df: Map[Token, Double], oldDocs: Seq[Seq[Token]], newDocs: Seq[Seq[Token]]): Map[Token, Double] = {
    sum(
      df.mapValues(_ * oldDocs.length),
      documentCount(newDocs).mapValues(_.toDouble)
    ).mapValues(_ / (oldDocs.length + newDocs.length))
  }

  object LazyLoad {

    class SearchEngine[A](adapter: A => Document[A]) {
      private var docs: Seq[Document[A]] = Seq()
      private var df: Map[Token, Double] = Map()

      def load(db: Seq[A]): Unit = {
        val newDocs = db.map(adapter)
        df = addDocs(df, docs.map(_.tokens), newDocs.map(_.tokens))
        docs = docs ++ newDocs
      }

      def search(query: String): Seq[(A, Double)] = {
        val queryTokens = tokenize(query)
        docs
          .map { doc => (doc.doc, score(df, queryTokens, doc.tokens)) }
          .filter(_._2 > 0)
          .sortBy(-_._2)
      }
    }

  }

  object IndexDocuments {

    class SearchEngine[A](adapter: A => Document[A]) {
      private var docs: Seq[Document[A]] = Seq()
      private var df: Map[Token, Double] = Map()
      private var index: Map[Token, Seq[Document[A]]] = Map()

      def load(db: Seq[A]): Unit = {
        val newDocs = db.map(adapter)
        df = addDocs(df, docs.map(_.tokens), newDocs.map(_.tokens))
        docs = docs ++ newDocs
        index = aggregate(index, indexDocs(newDocs))
      }

      def search(query: String): Seq[(A, Double)] = {
        val queryTokens = tokenize(query)
        val matchingDocs = queryTokens.flatMap(t => index.get(t)).flatten.distinct
        matchingDocs
          .map { doc => (doc.doc, score(df, queryTokens, doc.tokens)) }
          .sortBy(-_._2)
      }
    }

    def indexDocs[A](docs: Seq[Document[A]]): Map[Token, Seq[Document[A]]] = {
      docs.foldLeft(Map.empty[Token, Seq[Document[A]]]) { (acc, doc) =>
        aggregate(acc, doc.tokens.distinct.groupBy(identity).mapValues(_ => Seq(doc)))
      }
    }

    def aggregate[A, B](m1: Map[A, Seq[B]], m2: Map[A, Seq[B]]): Map[A, Seq[B]] =
      merge[A, Seq[B]]((v1, v2) => Some(v1.getOrElse(Seq()) ++ v2.getOrElse(Seq())))(m1, m2)
  }

  object ParallelRun {

    case class ParDocument[A](doc: A, tokens: ParSeq[String])

    class SearchEngine[A](adapter: A => Document[A]) {
      private var docs: ParSeq[ParDocument[A]] = ParSeq()
      private var df: ParMap[Token, Double] = ParMap()
      private var index: ParMap[Token, ParSeq[ParDocument[A]]] = ParMap()

      def load(db: Seq[A]): Unit = {
        val newDocs = db.par.map(adapter).map(doc => ParDocument(doc.doc, doc.tokens.par))
        df = addDocs(df, docs.map(_.tokens), newDocs.map(_.tokens))
        docs = docs ++ newDocs
        index = aggregate(index, indexDocs(newDocs))
      }

      def search(query: String): Seq[(A, Double)] = {
        val queryTokens = tokenize(query).par
        val matchingDocs = queryTokens.flatMap(t => index.get(t)).flatten.distinct
        matchingDocs
          .map { doc => (doc.doc, score(df, queryTokens, doc.tokens)) }
          .seq
          .sortBy(-_._2)
      }
    }

    def addDocs(df: ParMap[Token, Double], oldDocs: ParSeq[ParSeq[Token]], newDocs: ParSeq[ParSeq[Token]]): ParMap[Token, Double] = {
      sum(
        df.mapValues(_ * oldDocs.length),
        documentCount(newDocs).mapValues(_.toDouble)
      ).mapValues(_ / (oldDocs.length + newDocs.length))
    }

    def indexDocs[A](docs: ParSeq[ParDocument[A]]): ParMap[Token, ParSeq[ParDocument[A]]] = {
      docs.foldLeft(ParMap.empty[Token, ParSeq[ParDocument[A]]]) { (acc, doc) =>
        aggregate(acc, doc.tokens.distinct.groupBy(identity).mapValues(_ => ParSeq(doc)))
      }
    }

    def score[A](df: ParMap[Token, Double], queryTokens: ParSeq[Token], doc: ParSeq[Token]): Double = {
      val tf = termFrequency(doc)
      queryTokens.map(queryToken => tfidf(df, tf, queryToken).getOrElse(0d)).product
    }

    def tfidf(df: ParMap[Token, Double], tf: ParMap[Token, Double], token: Token): Option[Double] =
      for {
        t <- tf.get(token)
        d <- df.get(token)
      } yield t / d

    // count of token occurences in document
    def termCount[A](doc: ParSeq[Token]): ParMap[Token, Int] =
      doc.par.groupBy(identity).mapValues(_.length)

    // count of documents containing the token
    def documentCount[A](docs: ParSeq[ParSeq[Token]]): ParMap[Token, Int] =
      docs.foldLeft(ParMap.empty[Token, Int]) { (dc, doc) =>
        sum(dc, termCount(doc).mapValues(_ => 1))
      }

    // frequency of the token in the document
    def termFrequency[A](doc: ParSeq[Token]): ParMap[Token, Double] =
      termCount(doc).mapValues(count => count.toDouble / doc.length)

    // frequency of documents containing the token
    def documentFrequency[A](docs: ParSeq[ParSeq[Token]]): ParMap[Token, Double] =
      documentCount(docs).mapValues(count => count.toDouble / docs.length)

    def aggregate[A, B](m1: ParMap[A, ParSeq[B]], m2: ParMap[A, ParSeq[B]]): ParMap[A, ParSeq[B]] =
      merge[A, ParSeq[B]]((v1, v2) => Some(v1.getOrElse(ParSeq()) ++ v2.getOrElse(ParSeq())))(m1, m2)

    def sum[A, B](m1: ParMap[A, B], m2: ParMap[A, B])(implicit num: Numeric[B]): ParMap[A, B] =
      merge[A, B]((v1, v2) => Some(num.plus(v1.getOrElse(num.zero), v2.getOrElse(num.zero))))(m1, m2)

    def merge[A, B](f: (Option[B], Option[B]) => Option[B])(m1: ParMap[A, B], m2: ParMap[A, B]): ParMap[A, B] =
      (m1.keySet ++ m2.keySet).flatMap(key => f(m1.get(key), m2.get(key)).map(v => (key, v))).toMap
  }

  /**
    * More features :
    *   - typo tolerant
    *     - fuzzy matching
    *     - distance de Levenshtein
    */

  /**
    *     ---------- Ne pas modifier ----------
    * Fonction utilitaire pour charger les données.
    */
  type UserId = Long
  type UserType = String
  type QuestionId = Long
  type Timestamp = Long
  type Tag = String
  type Link = String

  case class UserEmbed(user_id: UserId, user_type: UserType, display_name: String, profile_image: Link, link: Link, reputation: Int, accept_rate: Option[Int])

  case class Question(question_id: QuestionId, title: String, body: String, link: Link, owner: UserEmbed, is_answered: Boolean, creation_date: Timestamp, last_activity_date: Timestamp, tags: Seq[Tag], score: Int, view_count: Int, answer_count: Int)

  def loadDb(): Try[Seq[Question]] = {
    import java.io.File

    import io.circe.generic.auto._
    import io.circe.{Decoder, parser}

    import scala.io.Source

    def listFiles(folder: String): Try[Seq[String]] =
      Try(new File(folder).listFiles.toSeq.map(_.getPath))

    def readFile(path: String): Try[String] =
      Try(Source.fromFile(path).mkString)

    def readEntity[A](path: String)(implicit decoder: Decoder[A]): Try[A] =
      readFile(path).flatMap { file => parser.decode[A](file).toTry }

    def readEntities[A](path: String)(implicit decoder: Decoder[A]): Try[Seq[A]] =
      listFiles(path).map(_.filter(_.endsWith(".json")))
        .flatMap { paths => Try(paths.map(path => readEntity(path).get)) }

    readEntities[Question]("src/main/resources/stackoverflow/questions")
  }
}
