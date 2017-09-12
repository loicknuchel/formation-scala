package corrections

import java.text.{Normalizer, SimpleDateFormat}
import java.util.Date
import java.util.concurrent.{ScheduledExecutorService, ScheduledFuture}

import exercices.s2Collections.Projections._
import exercices.s4FunctionalProgramming.SchedulerRefactoring.CancelableFuture
import org.scalatest.{FunSpec, Matchers}

import scala.concurrent.Promise
import scala.concurrent.duration.FiniteDuration
import scala.util.Try

class Correction20170907 extends FunSpec with Matchers {

  describe("FizzBuzz") {
    def fizzBuzz(i: Int): String = {
      if (i % 15 == 0) "FizzBuzz"
      else if (i % 3 == 0) "Fizz"
      else if (i % 5 == 0) "Buzz"
      else i.toString
    }

    it("should return Fizz when 3") {
      fizzBuzz(3) shouldBe "Fizz"
    }
    it("should return Buzz when 5") {
      fizzBuzz(5) shouldBe "Buzz"
    }
    it("should return FizzBuzz when 15") {
      fizzBuzz(15) shouldBe "FizzBuzz"
    }
    it("should return 4 when 4") {
      fizzBuzz(4) shouldBe "4"
    }
  }

  describe("AverageAge") {
    case class Employee(name: String, age: Int)

    case class Team(employees: Seq[Employee]) {
      def has(employee: Employee): Boolean = employees.contains(employee)
    }
    
    val employees: Seq[Employee] = Seq(
      Employee("Jean", 22),
      Employee("Corinne", 54),
      Employee("Fanny", 32),
      Employee("Claude", 40),
      Employee("Cécile", 25))
    val RnD: Team = Team(employees.take(3))

    def averageAge(employees: Seq[Employee], minAge: Int, team: Team): Double = {
      var total = 0
      var count = 0
      for(employee <- employees) {
        if(employee.age > minAge && (team == null || team.has(employee))) {
          total = total + employee.age
          count = count + 1
        }
      }
      total.toDouble / count
    }

    it("should average age for all employees") {
      averageAge(employees, 0, null) shouldBe 34.6
    }
    it("should average age for oldest employees") {
      averageAge(employees, 30, null) shouldBe 42
    }
    it("should average age for employees in RnD") {
      averageAge(employees, 0, RnD) shouldBe 36
    }
  }

  describe("Projections") {
    def numberOfEvents(events: Seq[Event]): Int = events.length

    def registredPlayers(events: Seq[Event]): Int = {
      events.count {
        case _: PlayerHasRegistered => true
        case _ => false
      }
    }

    val monthFormat = new SimpleDateFormat("yyyy-MM")

    def dateAsMonth(date: Date): String =
      monthFormat.format(date)

    def eventAsMonth(event: Event): String =
      dateAsMonth(event.timestamp)

    def registredPlayersPerMonth(events: Seq[Event]): Map[String, Int] = {
      events
        .filter {
          case _: PlayerHasRegistered => true
          case _ => false
        }
        .groupBy(eventAsMonth)
        .mapValues(_.length)
    }

    def popularQuizs(events: Seq[Event]): Seq[(QuizId, String, Int)] = {
      val gameToQuiz: Map[GameId, QuizId] = events
        .collect { case e: GameWasOpened => (e.payload.game_id, e.payload.quiz_id) }
        .groupBy(_._1)
        .mapValues(_.head._2)
      val quizIdToTitle: Map[QuizId, String] = events
        .collect { case e: QuizWasCreated => e }
        .groupBy(_.payload.quiz_id)
        .mapValues(_.head.payload.quiz_title)
      events
        .collect { case e: PlayerJoinedGame => e }
        .groupBy(e => gameToQuiz(e.payload.game_id))
        .mapValues(_.length)
        .toSeq
        .sortBy(-_._2)
        .take(10)
        .map { case (id, count) => (id, quizIdToTitle(id), count) }
    }
  }

  describe("SchedulerRefactoring") {
    class Scheduler(underlying: ScheduledExecutorService) {
      def scheduleOnce[T](delay: FiniteDuration)(operation: => T): CancelableFuture[T] =
        schedule[T](operation, runnable => underlying.schedule(runnable, delay.length, delay.unit))

      def scheduleAtFixedRate(interval: FiniteDuration, delay: Long = 0)(operation: => Unit): CancelableFuture[Unit] =
        schedule[Unit](operation, underlying.scheduleAtFixedRate(_, delay, interval.length, interval.unit))

      private def schedule[T](operation: => T, f: Runnable  => ScheduledFuture[_]): CancelableFuture[T] = {
        val promise = Promise[T]()
        val scheduledFuture = f(new Runnable {
          override def run(): Unit = promise.complete(Try(operation))
        })
        new CancelableFuture(promise, scheduledFuture.cancel)
      }
    }
  }

  describe("SearchEngine") {
    it("should return empty when empty") {
      search(Seq(), "toto") shouldBe Seq()
    }
    it("should return matching exact elements on db") {
      search(Seq("a", "b"), "a") shouldBe Seq("a")
    }
    it("should return matching elements on db") {
      search(Seq("a b", "b", "a c"), "b") shouldBe Seq("a b", "b")
    }
    it("should return matching words on db") {
      search(Seq("b a", "ab", "a c"), "a b") shouldBe Seq("b a", "ab")
    }
    it("should return matching with diatrics") {
      search(Seq("Scala", "scalà", "scala,"), "scalA") shouldBe Seq("Scala", "scalà", "scala,")
    }
    it("should normalize") {
      normalize("Scalà,") shouldBe "scala"
    }

    type Token = String
    type Document = String

    def normalize(str: String): String = {
      Normalizer
        .normalize(str, Normalizer.Form.NFD)
        .replaceAll("[^\\p{ASCII}]", "")
        .replaceAll("""[\p{Punct}&&[^.]]""", "")
        .toLowerCase
    }
    def tokenize(str: String): Seq[Token] = {
      str.split(" ").map(normalize).filter(_.length > 2)
    }
    def docMatch(doc: Document, queryWords: Seq[Token]): Boolean = {
      queryWords.forall(queryWord => doc.contains(queryWord))
    }
    def tokenMatch(doc: Seq[Token], queryWords: Seq[Token]): Boolean = {
      queryWords.forall(queryWord => doc.contains(queryWord))
    }
    def search(db: Seq[Document], query: String): Seq[Document] = {
      // 1st step
      //db.filter(doc => doc.contains(query))

      // 2nd step
      //val queryWords = query.split(" ")
      //db.filter(doc => docMatch(doc, queryWords))

      // 3rd step
      val queryWords = tokenize(query)
      db
        .map(doc => (doc, tokenize(doc)))
        .filter { case (_, docTokens) => tokenMatch(docTokens, queryWords) }
        .map { case (doc, _) => doc }
    }
  }
}
