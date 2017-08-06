package corrections

import java.text.Normalizer

import org.scalatest.{FunSpec, Matchers}
import project.searchengine.SearchEngine
import project.searchengine.SearchEngine.Question

class Correction20170803 extends FunSpec with Matchers {
  describe("FizzBuzz") {
    def fizzbuzz1(n: Int): String = {
      if (n % 3 == 0 && n % 5 == 0) "FizzBuzz"
      else if (n % 3 == 0) "Fizz"
      else if (n % 5 == 0) "Buzz"
      else n.toString
    }

    def fizzbuzz2(n: Int): String = {
      n match {
        case _ if n % 3 == 0 && n % 5 == 0 => "FizzBuzz"
        case _ if n % 3 == 0 => "Fizz"
        case _ if n % 5 == 0 => "Buzz"
        case _ => n.toString
      }
    }

    def fizzbuzz(n: Int): String = {
      (n % 3, n % 5) match {
        case (0, 0) => "FizzBuzz"
        case (0, _) => "Fizz"
        case (_, 0) => "Buzz"
        case _ => n.toString
      }
    }

    it("should return Fizz when 3") {
      fizzbuzz(3) shouldBe "Fizz"
    }
    it("should return Buzz when 5") {
      fizzbuzz(5) shouldBe "Buzz"
    }
    it("should return FizzBuzz when 15") {
      fizzbuzz(15) shouldBe "FizzBuzz"
    }
    it("should return 4 when 4") {
      fizzbuzz(4) shouldBe "4"
    }
  }
  describe("AverageAge") {
    case class Employee(name: String, age: Int)

    case class Team(employees: Seq[Employee]) {
      def has(employee: Employee): Boolean = employees.contains(employee)
    }

    val employees = Seq(
      Employee("Corinne", 49),
      Employee("Fanny", 36),
      Employee("Claude", 45),
      Employee("Jean", 41),
      Employee("Cécile", 34))
    val RnD = Team(employees.take(3))

    def averageAge(employees: Seq[Employee], ageMin: Int, team: Team): Int = {
      var total = 0
      var count = 0
      for (e <- employees) {
        if (e.age > ageMin && (team == null || team.has(e))) {
          total += e.age
          count += 1
        }
      }
      total / count
    }

    it("should return average") {
      averageAge(employees, 40, null) shouldBe 45
    }
    it("should return average for a team") {
      averageAge(employees, 40, RnD) shouldBe 47
    }
  }
  describe("AverageTemperature") {
    case class Coords(lat: Double, lng: Double)
    case class City(name: String, coords: Coords, temperatures: Seq[Double])

    val data = Seq(
      City("Paris", Coords(48.856614, 2.352222), Seq(5, 6, 9, 11, 15, 16, 20, 20, 16, 12, 7, 5)),
      City("Marseille", Coords(43.296482, 5.36978), Seq(7, 8, 11, 14, 18, 21, 24, 24, 21, 17, 11, 8)),
      City("Lyon", Coords(45.764043, 4.835659), Seq(3, 4, 8, 11, 16, 18, 22, 21, 18, 13, 7, 5))
    )

    val results: Seq[(Coords, Double)] = Seq(
      (Coords(48.856614, 2.352222), 11.833333333333334),
      (Coords(43.296482, 5.36978), 15.333333333333334),
      (Coords(45.764043, 4.835659), 12.166666666666666)
    )

    def averageTemp1(data: Seq[City]): Seq[(Coords, Double)] = {
      data.map { city => (city.coords, average(city.temperatures)) }
    }

    def averageTemp(data: Seq[City]): Seq[(Coords, Double)] = {
      data.map(_.coords).zip(data.map(_.temperatures).map(average))
    }

    def average(values: Seq[Double]): Double =
      if (values.isEmpty) 0
      else values.sum / values.length

    it("should work") {
      averageTemp(data) shouldBe results
    }
  }
  describe("Devoxx") {
    import exercices.s2Collections.Devoxx._

    val (talks, speakers, slots, rooms) = loadData().get

    val talkId = "XPI-0919"
    val talkTitle = "Scala class, bien démarrer avec Scala"
    val speakerId = "09a79f4e4592cf77e5ebf0965489e6c7ec0438cd"
    val roomId = "par224M-225M"

    def frenchTalkPercentage(talks: Seq[Talk]): Double = {
      val frenchTalks = talks.filter(_.lang == "fr")
      frenchTalks.length.toDouble / talks.length
    }

    describe("frenchTalkPercentage") {
      it("should calculate the percentage of french talks") {
        math.round(frenchTalkPercentage(talks) * 100) shouldBe 90
      }
    }

    def speakersOfTalk(talks: Seq[Talk], speakers: Seq[Speaker], talkId: String): Seq[Speaker] = {
      talks
        .find(talk => talk.id == talkId)
        .map(_.speakers)
        .getOrElse(Seq())
        .flatMap(id => speakers.find(_.id == id))
    }

    describe("speakersOfTalk") {
      it("should list speaker for a talk") {
        speakersOfTalk(talks, speakers, talkId).map(_.id) shouldBe Seq(speakerId, "1693d28c079e6c28269b9aa86ae04a4549ad3074", "d167a51898267ed3b5913c1789f1ae6110a6ecf5")
      }
    }
  }
  describe("SearchEngine") {
    it("should search query") {
      def search(db: Seq[String], query: String): Seq[String] = {
        db.filter(_.contains(query))
      }

      search(Seq("a", "b", "ab"), "a") shouldBe Seq("a", "ab")
    }
    it("should search multi words") {
      def docMatch(doc: String, tokens: Seq[String]): Boolean = {
        tokens.forall(token => doc.contains(token))
      }

      def search(db: Seq[String], query: String): Seq[String] = {
        val tokens = query.split(" ")
        db.filter(docMatch(_, tokens))
      }

      search(Seq("a", "b", "ba"), "a b") shouldBe Seq("ba")
    }

    type Doc = String
    type Query = String
    type Token = String

    def normalize(in: String): Token = {
      Normalizer.normalize(in, Normalizer.Form.NFD)
        .replaceAll("[^\\p{ASCII}]", "")
        .replaceAll("""[\p{Punct}&&[^-]]""", "")
        .toLowerCase
        .trim
    }

    def tokenize(doc: Doc): Seq[Token] = {
      doc.split(" ").map(normalize)
    }

    def hasTokens(doc: Seq[Token], tokens: Seq[Token]): Boolean = {
      tokens.forall(token => doc.contains(token))
    }

    it("should tokenize docs") {
      def search(db: Seq[Doc], query: Query): Seq[Doc] = {
        val queryTokens = tokenize(query)
        db
          .map(doc => (doc, tokenize(doc)))
          .filter { case (_, docTokens) => hasTokens(docTokens, queryTokens) }
          .map(_._1)
      }

      normalize("àâçéèêëîïöû") shouldBe "aaceeeeiiou"
      normalize("orčpžsíáýd") shouldBe "orcpzsiayd"
      search(Seq("a", "b", "b a"), "a b") shouldBe Seq("b a")
    }

    val db = SearchEngine.loadDb().get

    it("should load questions") {
      db.length shouldBe 700
    }

    case class Document[A](doc: A, tokens: Seq[String])

    def questionAdapter(q: Question): Document[Question] = {
      Document(q, tokenize(q.body))
    }

    it("should enable adapter") {
      def search[A](db: Seq[A], adapter: A => Document[A])(query: String): Seq[A] = {
        val queryTokens = tokenize(query)
        db.map(adapter).filter(doc => hasTokens(doc.tokens, queryTokens)).map(_.doc)
      }

      def searchQuestion(query: String): Seq[Question] = search(db, questionAdapter)(query)

      searchQuestion("scala").length shouldBe 1
    }
  }
}
