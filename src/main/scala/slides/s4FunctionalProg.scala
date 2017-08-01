package slides

import java.io.PrintWriter
import java.util.UUID

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try}

object s4FunctionalProg {

  /**
    * La prog fun n'est pas nouvelle (débuts avec Lisp en 1958) mais, comme pour la recherche théorique, c'est plusieurs décénies plus tard qu'on en a vraiment besoin.
    * Aujourd'hui la prog fun permet de répondre au défi de la complexité croissante des applications.
    * Que ce soit sur le code (qui grossit et qu'il faut continuer à maintenir) ou sur l'exécution dans un environnement distribué.
    *
    * La prog fun permet d'avoir un style déclaratif, plus proche de l'intention initiale et reléguant au détail d'implémentation le mode d'exécution réel.
    *
    * Programmation impérative => instructions => modification de l'état
    * Programmation fonctionnelle => expressions => pas d'état
    * => Pourquoi un effet de bord pose problème ?
    *   - dépendances implicites masquées
    *   - impossible de raisonner sur le programme => plus complexe,
    * => Solution: limiter / faire les effets de bords à un seul endroit !
    *
    * Règles de la FP :
    *   - fonctions pures
    *     - pas de throw / log / call http / call db / lecture d'un fichier => ne peut pas fail
    *   - immutabilité
    *     - pas de modifiction des paramètres d'un fonction
    *     - pas de variables, que des constantes
    *     - état global peu pertinent (car non modifiable)
    *
    * Beaucoup de règles de la FP sont en réalités des bonnes pratiques en OOP
    *
    * Il n'y a pas de langage fonctionnel, tous les langages favorisent plus ou moins la programmation fonctionnelle
    *   - Haskell beaucoup
    *   - Java très peu (mais de +  en +)
    *   - Scala permet sans l'obliger (pragmtique)
    */
  /**
    * Curryfication
    * Type algébrique
    * Fonction d'ordre supérieur
    * Monade / Fonctor
    * Tail recursion
    *
    * Best practices :
    *   - semantic => DDD
    *   - bcp de petites fonctions pures
    * Common patterns :
    *   - ADT
    *   - error handling
    * Code smells :
    *   - paramètres optionnels => la fonction à trop de responsabilités ?
    */
  object InstructionsVsExpressions {
    def sumImp(nums: Seq[Int]): Int = {
      var i = 0
      var total = 0
      while (i < nums.length) {
        total += nums(i)
        i += 1
      }
      total
    }

    def sumFun(nums: Seq[Int]): Int =
      if (nums.isEmpty) 0 else nums.head + sumFun(nums.tail)

    @tailrec
    def sumFun(nums: Seq[Int], acc: Int = 0): Int =
      if (nums.isEmpty) 0 else sumFun(nums.tail, acc + nums.head)
  }

  object ContainSideEffects {
    val path = "src/main/resources/file.txt"

    def readFile(path: String): String =
      Source.fromFile(path).getLines().mkString("\n")

    def writeFile(path: String, content: String): Unit =
      new PrintWriter(path) {
        write(content)
        close()
      }

    def sideEffectsEverywhere(): Unit = {
      println("start")
      var content = readFile(path)
      println(content)
      writeFile(path, "new content !")
      content = readFile(path)
      println(content)
      // > start                                                                                                              
      // > file content
      // > new content !
    }

    class IO[A](effect: () => A) {
      def run: A = effect()

      def map[B](f: A => B) = new IO(() => f(run))

      def flatMap[B](f: A => IO[B]) = new IO(() => f(run).run)
    }

    def readFileIO(path: String): IO[String] = new IO(() => readFile(path))

    def writeFileIO(path: String, content: String): IO[Unit] = new IO(() => writeFile(path, content))

    def printlnIO(content: String): IO[Unit] = new IO(() => println(content))

    def isolatedSideEffect(): Unit = {
      val program: IO[Unit] = for {
        content <- readFileIO(path)
        _ <- printlnIO(content)
        _ <- writeFileIO(path, "new content !")
        newContent <- readFileIO(path)
        _ <- printlnIO(newContent)
      } yield ()
      println("start")
      program.run
      // > start
      // > file content
      // > new content !
    }

    def isolatedSideEffectRefactored(): Unit = {
      val printFile: IO[Unit] = readFileIO(path).flatMap(printlnIO)
      val program: IO[Unit] = for {
        _ <- printFile
        _ <- writeFileIO(path, "new content !")
        _ <- printFile
      } yield ()
      println("start")
      program.run
      // > start
      // > file content
      // > new content !
    }

    def isolatedSideEffectDRY(): Unit = {
      def wrap[A, B](a: IO[A], b: IO[B]): IO[A] = a.flatMap(_ => b).flatMap(_ => a)

      val printFile: IO[Unit] = readFileIO(path).flatMap(printlnIO)
      val program: IO[Unit] = wrap(printFile, writeFileIO(path, "new content !"))
      println("start")
      program.run
      // > start
      // > file content
      // > new content !
    }

    def breakingProgram(): Unit = {

      def wrap[A, B](a: IO[A], b: IO[B]): IO[A] = a.flatMap(_ => b).flatMap(_ => a)

      val printFile: IO[Unit] = {
        println("printFile")
        readFileIO(path).flatMap(printlnIO)
      }
      val program: IO[Unit] = wrap(printFile, writeFileIO(path, "new content !"))
      println("start")
      program.run
      // > printFile
      // > start
      // > file content
      // > new content !

      //  - "start" n’est plus en premier !
      //  - "printFile" n’est affiché qu’une fois !
    }
  }

  object HighOrderFunction {
    def greaterThan(threshold: Int): Int => Boolean = (x: Int) => x > threshold

    def foreach[A](seq: Seq[A], f: A => Unit): Unit = {
      for (s <- seq) {
        f(s)
      }
    }
  }

  object RefactorToFunctionnalEmplyeeAge {

    // Spec :
    //  - calculate average age for employees
    //  - exclude youngest
    //  - optionally filtrate by team

    case class Employee(name: String, age: Int)

    case class Team(employees: Seq[Employee]) {
      def has(employee: Employee): Boolean = employees.contains(employee)
    }

    val employees = Seq(
      Employee("Jim", 28),
      Employee("John", 50),
      Employee("Liz", 35),
      Employee("Penny", 40)
    )
    val sales = Team(employees.take(2))

    object Step0 {
      def averageAge(employees: Seq[Employee],
                     min: Int,
                     team: Team
                    ): Int = {
        var total = 0
        var count = 0

        for (e <- employees) {
          if (e.age >= min && (team == null || team.has(e))) {
            total += e.age
            count += 1
          }
        }

        if (count == 0) throw new Exception("no employee matching criterias")
        else total / count
      }

      averageAge(employees, 30, sales)
      averageAge(employees, 30, null)

      /**
        * Problèmes :
        *   - signature de fonction trompeuse (paramètres optionnels ? erreurs ?)
        *   - sensible aux nulls (si on les “autorises”, alors on peut éventuellement en avoir de partout)
        *     - si employees = null : NullPointerException sur le for
        *   - signature de fonction fragile
        *     - presque obligé de la changer si on veut modifier la fonction (ajouter/supprimer un paramètre)
        *   - code métier et technique mélangé
        *     - métier (spec de ce qu’on souhaite faire): condition + extraction de âge
        *     - technique (pourrait être mis dans une lib et partagé entre plusieurs applications): variables, for, compteur, moyenne
        *   - fonction boulimique
        *     - la fonction gère plusieurs scénarii, le code va grossir et se complexifier au fur et à mesure qu’on en ajoute (spaghetti code / pelote de if)
        *     - la liste des arguments peut s’allonger, on demande tous les arguments alors qu’on veut qu’un seul scénario
        *     - duplication éventuelle: pour ne pas faire trop grossir la fonction, on risque d’en coder une autre similaire pour d’autres scénarii
        *     - couplage fort: la modification de cette fonction risque d’avoir de forts impacts sur l’ensemble du code
        *     - code smell: paramètre optionnel
        *   - code verbeux et sujet aux bugs
        */
    }

    object Step1 {
      def averageAge(employees: Seq[Employee],
                     min: Int,
                     team: Option[Team] = None
                    ): Try[Int] = {
        var total = 0
        var count = 0

        for (e <- employees) {
          if (e.age >= min && team.forall(_.has(e))) {
            total += e.age
            count += 1
          }
        }

        if (count == 0) Failure(new Exception("no employee matching criterias"))
        else Success(total / count)
      }

      averageAge(employees, 30, Some(sales))
      averageAge(employees, 30)
    }

    object Step2 {
      def averageAge(employees: Seq[Employee],
                     min: Int,
                     team: Option[Team] = None
                    ): Try[Int] = {
        val ages = employees
          .filter(e => e.age >= min && team.forall(_.has(e)))
          .map(_.age)

        if (ages.isEmpty) Failure(new Exception("no employee matching criterias"))
        else Success(ages.sum / ages.length)
      }

      averageAge(employees, 30, Some(sales))
      averageAge(employees, 30)
    }

    object Step3 {
      def averageAge(employees: Seq[Employee],
                     min: Int,
                     team: Option[Team] = None
                    ): Try[Int] = {
        val ages = employees
          .filter(e => e.age >= min && team.forall(_.has(e)))
          .map(_.age)
        average(ages)
      }

      def average(nums: Seq[Int]): Try[Int] =
        if (nums.isEmpty) Failure(new Exception("Can't average an empty Seq"))
        else Success(nums.sum / nums.length)

      averageAge(employees, 30, Some(sales))
      averageAge(employees, 30)
    }

    object Step4 {
      type Predicate[T] = T => Boolean

      def averageAge(employees: Seq[Employee],
                     predicate: Predicate[Employee]
                    ): Try[Int] =
        average(employees.filter(predicate).map(_.age))

      def average(nums: Seq[Int]): Try[Int] =
        if (nums.isEmpty) Failure(new Exception("Can't average an empty Seq"))
        else Success(nums.sum / nums.length)

      averageAge(employees, e => e.age >= 30 && sales.has(e))
      averageAge(employees, _.age >= 30)
    }

    object Step5 {
      type Predicate[T] = T => Boolean

      def averageAge(employees: Seq[Employee],
                     predicate: Predicate[Employee]
                    ): Try[Int] =
        average(employees.filter(predicate).map(_.age))

      def gt(v: Int): Predicate[Employee] = (e: Employee) => e.age >= v

      def in(team: Team): Predicate[Employee] = (e: Employee) => team.has(e)

      def and[T](ps: Predicate[T]*): Predicate[T] = (e: T) => ps.forall(_ (e))

      def average(nums: Seq[Int]): Try[Int] =
        if (nums.isEmpty) Failure(new Exception("Can't average an empty Seq"))
        else Success(nums.sum / nums.length)

      averageAge(employees, and(gt(30), in(sales)))
      averageAge(employees, gt(30))
    }

    object More {

      import Step5._

      def or[T](predicates: Predicate[T]*): Predicate[T] = (e: T) => predicates.exists(_ (e))

      def not[T](predicate: Predicate[T]): Predicate[T] = (e: T) => !predicate(e)

      def in(min: Int, max: Int): Predicate[Employee] =
        and(gt(min), not(gt(max)))

      averageAge(employees, or(in(30, 50), not(Step5.in(sales))))

      def averageAgeImp(employees: Seq[Employee],
                        min: Int,
                        team: Team
                       ): Int =
        averageAge(employees, and(gt(min), Step5.in(team))).get
    }

  }

  object RefactorToFunctionnalAverageTemperature {

    case class Coords(lat: Double, lng: Double)

    case class City(name: String, coords: Coords, temperatures: Seq[Double])

    val data = Seq(
      City("Paris", Coords(48.856614, 2.352222), Seq(5, 6, 9, 11, 15, 16, 20, 20, 16, 12, 7, 5)),
      City("Marseille", Coords(43.296482, 5.36978), Seq(7, 8, 11, 14, 18, 21, 24, 24, 21, 17, 11, 8)),
      City("Lyon", Coords(45.764043, 4.835659), Seq(3, 4, 8, 11, 16, 18, 22, 21, 18, 13, 7, 5))
    )
    type ChartData = Seq[(Coords, Double)]
    val results: ChartData = Seq(
      (Coords(48.856614, 2.352222), 11.8),
      (Coords(43.296482, 5.36978), 15.3),
      (Coords(45.764043, 4.835659), 12.2)
    )

    object Step0 {
      def chartFormat(data: Seq[City]): ChartData = {
        var results = Seq.empty[(Coords, Double)]
        var totalTemp = 0d
        var averageTemp = 0d
        for (city <- data) {
          totalTemp = 0d
          for (temp <- city.temperatures) {
            totalTemp += temp
          }
          averageTemp = totalTemp / city.temperatures.length
          results = results :+ (city.coords, averageTemp)
        }
        results
      }
    }

    object Step1 {
      def chartFormat(data: Seq[City]): ChartData =
        data.map(_.coords).zip(data.map(_.temperatures).map(t => t.sum / t.length))
    }

  }

  object ADT {

    // Product type
    case class Person(name: String, age: Int)

    // Sum type
    sealed trait Pet

    case class Cat(name: String) extends Pet

    case class Fish(name: String, color: String) extends Pet

    case class Squid(name: String, age: Int) extends Pet

  }

  object FullTyping {

    object NoSoTyped {

      // linked fields ? atomic update ?
      case class User(id: String, // any String is valid ?
                      firstName: String,
                      title: String, // Optional ?
                      lastName: String,
                      phone: String,
                      email: String) // need two contacts ?

      val u = User(
        "1",
        "Loïc",
        "M",
        "Knuchel",
        "loicknuchel@gmail.com",
        "0000000000")

    }

    object WellTyped {

      case class UserId(value: String) {
        require(
          UserId.isValid(value),
          s"UserId should be a UUID (actual: $value)"
        )
      }

      object UserId {
        def generate: UserId = UserId(UUID.randomUUID.toString)

        def isValid(value: String): Boolean = Try(UUID.fromString(value)).isSuccess
      }

      case class Name(firstName: String,
                      lastName: String,
                      title: Option[String] = None)

      sealed trait Contact {
        def value: String
      }

      case class Phone(value: String) extends Contact

      sealed trait Email extends Contact

      object Email {
        private case class Impl(value: String) extends Email

        def from(value: String): Try[Email] =
          if (value.contains("@"))
            Success(Impl(value))
          else
            Failure(new Exception(s"invalid $value"))
      }

      case class User1(id: UserId,
                       name: Name,
                       contact: Contact)

      val u1 = User1(
        UserId.generate,
        Name("Loïc", "Knuchel"),
        Email.from("loicknuchel@gmail.com").get)

      // need at least one contact
      type NonEmptyList[T] = List[T]

      case class User2(id: UserId,
                       name: Name,
                       contacts: NonEmptyList[Contact])

      val u2 = User2(
        UserId.generate,
        Name("Loïc", "Knuchel"),
        List(Email.from("loicknuchel@gmail.com").get))


      case class String_50(value: String) {
        require(
          value.length <= 50,
          s"String_50 should be <= 50 (actual: $value)"
        )
        override def toString: String = value
      }

    }

  }

}
