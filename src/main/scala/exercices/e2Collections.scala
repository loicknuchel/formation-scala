package exercices

object e2Collections {

  object Factoriel {
    def factoriel(n: Int): Int =
      (1 to n).foldLeft(1)(_ * _)
  }

  object Average {
    def average(seq: Seq[Int]): Int =
      seq.reduce(_ + _) / seq.length
  }

  object Size {
    def sizeOfRec[A](list: List[A]): Int = list match {
      case Nil => 0
      case _ :: tail => 1 + sizeOfRec(tail)
    }

    def sizeOfFun[A](list: List[A]): Int =
      list.map(_ => 1).sum
  }

  object TemperatureMap {

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

    def chartFormatImp(data: Seq[City]): Seq[(Coords, Double)] = {
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

    def chartFormatFun(data: Seq[City]): Seq[(Coords, Double)] =
      data.map(_.coords).zip(data.map(_.temperatures).map(t => t.sum / t.length))
  }

  object Devoxx {

    import project.devoxx.domain._

    def frenchTalkPercentage(talks: Seq[Talk]): Double =
      talks.count(_.lang == "fr").toDouble / talks.length

    def speakersOfTalk(talks: Seq[Talk], speakers: Seq[Speaker], id: TalkId): Seq[Speaker] =
      talks.find(_.id == id).map { talk =>
        speakers.filter(speaker => talk.speakers.exists(_.link.href.contains(speaker.uuid)))
      }.getOrElse(Seq())

    def talksOfSpeaker(speakers: Seq[Speaker], talks: Seq[Talk], id: SpeakerId): Seq[Talk] =
      speakers.find(_.uuid == id).map { speaker =>
        talks.filter(talk => speaker.acceptedTalks.exists(_.exists(_.id == talk.id)))
      }.getOrElse(List())

    def roomSchedule(slots: Seq[Slot], id: RoomId): Seq[(Long, Long, TalkId)] =
      slots.filter(_.roomId == id).flatMap { slot =>
        slot.talk.map { talk =>
          (slot.fromTimeMillis, slot.toTimeMillis, talk.id)
        }
      }

    def isSpeaking(slots: Seq[Slot], rooms: Seq[Room], id: SpeakerId, time: Long): Option[Room] =
      slots
        .filter(s => s.fromTimeMillis <= time && time <= s.toTimeMillis)
        .flatMap(slot => slot.talk.map(t => (slot.roomId, t.speakers)))
        .filter { case (_, speakers) => speakers.exists(_.link.href.contains(id)) }
        .flatMap { case (roomId, _) => rooms.find(_.id == roomId)}
        .headOption
  }

  object CustomOption {

    trait Option[+A] {
      def isEmpty: Boolean

      def get: A

      def getOrElse[B >: A](default: => B): B // = if(isEmpty) default else get
      def map[B](f: A => B): Option[B] // = if(isEmpty) None else Some(f(get))
      def find(p: A => Boolean): Option[A] // = if(isEmpty || p(get)) this else None
      def filter(p: A => Boolean): Option[A] // = if(isEmpty || p(get)) this else None
      def flatMap[B](f: A => Option[B]): Option[B] // = if(isEmpty) None else f(get)
      def foldLeft[B](z: B)(op: (B, A) => B): B // = if(isEmpty) z else op(z, get)
      def foreach[U](f: A => U): Unit // = if(isEmpty) () else f(get)
      def exists(p: A => Boolean): Boolean // = if(isEmpty) false else p(get)
      def forall(p: A => Boolean): Boolean // = if(isEmpty) true else p(get)
      def toList: List[A] // = if(isEmpty) Nil else get :: Nil
    }

    object Option {
      def apply[A](value: A): Option[A] = if (value == null) None else Some(value)

      def empty[A]: Option[A] = None
    }

    case class Some[A](value: A) extends Option[A] {
      def isEmpty: Boolean = false

      def get: A = value

      def getOrElse[B >: A](default: => B): B = value

      def map[B](f: A => B): Option[B] = Some(f(value))

      def find(p: A => Boolean): Option[A] = if (p(value)) this else None

      def filter(p: A => Boolean): Option[A] = if (p(value)) this else None

      def flatMap[B](f: A => Option[B]): Option[B] = f(value)

      def foldLeft[B](z: B)(op: (B, A) => B): B = op(z, value)

      def foreach[U](f: A => U): Unit = f(value)

      def exists(p: A => Boolean): Boolean = p(value)

      def forall(p: A => Boolean): Boolean = p(value)

      def toList: List[A] = value :: Nil
    }

    case object None extends Option[Nothing] {
      def isEmpty: Boolean = true

      def get: Nothing = throw new NoSuchElementException

      def getOrElse[B >: Nothing](default: => B): B = default

      def map[B](f: Nothing => B): Option[B] = this

      def find(p: Nothing => Boolean): Option[Nothing] = this

      def filter(p: Nothing => Boolean): Option[Nothing] = this

      def flatMap[B](f: Nothing => Option[B]): Option[B] = this

      def foldLeft[B](z: B)(op: (B, Nothing) => B): B = z

      def foreach[U](f: Nothing => U): Unit = ()

      def exists(p: Nothing => Boolean): Boolean = false

      def forall(f: Nothing => Boolean): Boolean = true

      def toList: List[Nothing] = Nil
    }

  }

  object CustomList {

    trait List[+A] {
      def isEmpty: Boolean

      def head: A

      def tail: List[A]

      def headOption: Option[A] // = if(isEmpty) None else Some(head)
      def size: Int // = if(isEmpty) 0 else tail.size + 1
      def take(n: Int): List[A] // = if(isEmpty || n <= 0) Nil else ::(head, tail.take(n - 1))
      def map[B](f: A => B): List[B] // = if(isEmpty) Nil else ::(f(head), tail.map(f))
      def find(p: A => Boolean): Option[A] // = if(isEmpty) None else if(p(head)) Some(head) else tail.find(p)
      def filter(p: A => Boolean): List[A] // = if(isEmpty) Nil else if(p(head)) ::(head, tail.filter(p)) else tail.filter(p)
      def ++[B >: A](that: List[B]): List[B] // = if(isEmpty) that else ::(head, tail ++ that)
      def flatMap[B](f: A => List[B]): List[B] // = if(isEmpty) Nil else f(head) ++ tail.flatMap(f)
      def foldLeft[B](z: B)(op: (B, A) => B): B // = if(isEmpty) z else tail.foldLeft(op(z, head))(op)
      def reduceLeft[B >: A](op: (B, A) => B): B // = if(isEmpty) throw new UnsupportedOperationException else tail.foldLeft[B](head)(op)
      def sum[B >: A](implicit num: Numeric[B]): B = foldLeft(num.zero)(num.plus)
    }

    object List {
      def apply[A](values: A*): List[A] = {
        def inner(values: Seq[A], acc: List[A]): List[A] =
          if (values.isEmpty) acc
          else inner(values.tail, ::(values.head, acc))

        inner(values.reverse, Nil)
      }

      def empty[A]: List[A] = Nil
    }

    case class ::[A](head: A, tail: List[A]) extends List[A] {
      def isEmpty: Boolean = false

      def headOption: Option[A] = Some(head)

      def size: Int = 1 + tail.size

      def take(n: Int): List[A] = if (n > 0) ::(head, tail.take(n - 1)) else Nil

      def map[B](f: A => B): List[B] = ::(f(head), tail.map(f))

      def find(p: A => Boolean): Option[A] = if (p(head)) Some(head) else tail.find(p)

      def filter(p: A => Boolean): List[A] = if (p(head)) ::(head, tail.filter(p)) else tail.filter(p)

      def ++[B >: A](that: List[B]): List[B] = ::(head, tail ++ that)

      def flatMap[B](f: A => List[B]): List[B] = f(head) ++ tail.flatMap(f)

      def foldLeft[B](z: B)(op: (B, A) => B): B = tail.foldLeft(op(z, head))(op)

      def reduceLeft[B >: A](op: (B, A) => B): B = tail.foldLeft[B](head)(op)
    }

    case object Nil extends List[Nothing] {
      def isEmpty: Boolean = true

      def head: Nothing = throw new NoSuchElementException

      def tail: List[Nothing] = throw new UnsupportedOperationException

      def headOption: Option[Nothing] = None

      def size: Int = 0

      def take(n: Int): List[Nothing] = this

      def map[B](f: Nothing => B): List[B] = this

      def find(p: Nothing => Boolean): Option[Nothing] = None

      def filter(p: Nothing => Boolean): List[Nothing] = this

      def ++[B >: Nothing](that: List[B]): List[B] = that

      def flatMap[B](f: Nothing => List[B]): List[B] = this

      def foldLeft[B](z: B)(op: (B, Nothing) => B): B = z

      def reduceLeft[B >: Nothing](op: (B, Nothing) => B): B = throw new UnsupportedOperationException
    }

  }

  object CustomTree {

    // operations are in depth first
    trait Tree[+A] {
      def isEmpty: Boolean

      def root: A

      def children: Seq[Tree[A]]

      def size: Int

      def height: Int

      def map[B](f: A => B): Tree[B]

      def find(p: A => Boolean): Option[A]

      def filter(p: A => Boolean): Tree[A]

      def flatMap[B](f: A => Tree[B]): Tree[B]

      def fold[B](z: B)(f: (B, A) => B): B

      def toList: List[A]
    }

    object Tree {
      def apply(): Tree[Nothing] = Leaf

      def apply[A](root: A): Tree[A] = Node(root)

      def apply[A](root: A, child: A): Tree[A] = Node(root, Seq(Node(child)))

      def apply[A](root: A, child: A, children: A*): Tree[A] = Node(root, Node(child) +: children.map(c => Node(c)))

      def apply[A](root: A, child: Tree[A]): Tree[A] = Node(root, Seq(child))

      def apply[A](root: A, child: Tree[A], children: Tree[A]*): Tree[A] = Node(root, child +: children)

      def empty[A]: Tree[A] = Leaf
    }

    case class Node[A](root: A, children: Seq[Tree[A]] = Seq()) extends Tree[A] {
      def isEmpty: Boolean = false

      def size: Int = 1 + children.map(_.size).sum

      def height: Int = 1 + (if (children.isEmpty) 0 else children.map(_.height).max)

      def map[B](f: A => B): Tree[B] = Node(f(root), children.map(_.map(f)))

      def find(p: A => Boolean): Option[A] = if (p(root)) Some(root) else children.collect { case tree => tree.find(p) }.headOption.flatten

      def filter(p: A => Boolean): Tree[A] = if (p(root)) Node(root, children) else Leaf

      def flatMap[B](f: A => Tree[B]): Tree[B] = {
        val r = f(root)
        Node(r.root, r.children ++ children.map(_.flatMap(f)))
      }

      def fold[B](z: B)(f: (B, A) => B): B = children.foldLeft(f(z, root))((acc, item) => item.fold(acc)(f))

      def toList: List[A] = root +: children.flatMap(_.toList).toList
    }

    case object Leaf extends Tree[Nothing] {
      def isEmpty: Boolean = true

      def root: Nothing = throw new NoSuchElementException

      def children: Seq[Tree[Nothing]] = throw new UnsupportedOperationException

      def size: Int = 0

      def height: Int = 0

      def map[B](f: Nothing => B): Tree[B] = this

      def find(p: Nothing => Boolean): Option[Nothing] = None

      def filter(p: Nothing => Boolean): Tree[Nothing] = this

      def flatMap[B](f: Nothing => Tree[B]): Tree[B] = this

      def fold[B](z: B)(f: (B, Nothing) => B): B = z

      def toList: List[Nothing] = Nil
    }

  }

}
