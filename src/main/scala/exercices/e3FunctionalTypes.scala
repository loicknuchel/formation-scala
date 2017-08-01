package exercices

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

// implémenter une Monade (Validation ?)
// l'utiliser dans un for-comprehension
object e3FunctionalTypes {

  object Devoxx {

    import project.devoxx.dao._
    import project.devoxx.domain._

    def sequenceTry[A](in: Seq[Try[A]]): Try[Seq[A]] = Try(in.map(_.get))

    def frenchTalkPercentage(client: LocalClient): Try[Double] =
      client.getTalks().map(e2Collections.Devoxx.frenchTalkPercentage)

    def speakersOfTalk(client: LocalClient)(id: TalkId): Try[Seq[Speaker]] =
      for {
        talks <- client.getTalks()
        speakers <- client.getSpeakers()
      } yield e2Collections.Devoxx.speakersOfTalk(talks, speakers, id)

    def talksOfSpeaker(client: LocalClient)(id: SpeakerId): Try[Seq[Talk]] =
      for {
        speakers <- client.getSpeakers()
        talks <- client.getTalks()
      } yield e2Collections.Devoxx.talksOfSpeaker(speakers, talks, id)

    def roomSchedule(client: LocalClient)(id: RoomId): Try[Seq[(Long, Long, TalkId)]] =
      client.getScheduleDays()
        .flatMap(days => sequenceTry(days.map(client.getSchedule)))
        .map(_.flatten)
        .map(e2Collections.Devoxx.roomSchedule(_, id))

    def isSpeaking(client: LocalClient)(id: SpeakerId, time: Long): Try[Option[Room]] =
      for {
        slots <- client.getScheduleDays()
          .flatMap(days => sequenceTry(days.map(client.getSchedule)))
          .map(_.flatten)
        rooms <- client.getRooms()
      } yield e2Collections.Devoxx.isSpeaking(slots, rooms, id, time)

    def getTalks(client: ApiClient): Future[Seq[Talk]] =
      for {
        days <- client.getScheduleDays()
        slots <- Future.sequence(days.map(client.getSchedule)).map(_.flatten)
        talks <- Future.sequence(slots.flatMap(_.talk).map(_.id).distinct.map(client.getTalk))
      } yield talks

    def frenchTalkPercentage(client: ApiClient): Future[Double] =
      getTalks(client).map(e2Collections.Devoxx.frenchTalkPercentage)

    def speakersOfTalk(client: ApiClient)(id: TalkId): Future[Seq[Speaker]] =
      for {
        talks <- getTalks(client)
        speakers <- client.getSpeakers()
      } yield e2Collections.Devoxx.speakersOfTalk(talks, speakers, id)

    def talksOfSpeaker(client: ApiClient)(id: SpeakerId): Future[Seq[Talk]] =
      for {
        speakers <- client.getSpeakers()
        talks <- getTalks(client)
      } yield e2Collections.Devoxx.talksOfSpeaker(speakers, talks, id)

    def roomSchedule(client: ApiClient)(id: RoomId): Future[Seq[(Long, Long, TalkId)]] =
      client.getScheduleDays()
        .flatMap(days => Future.sequence(days.map(client.getSchedule)))
        .map(_.flatten)
        .map(e2Collections.Devoxx.roomSchedule(_, id))

    def isSpeaking(client: ApiClient)(id: SpeakerId, time: Long): Future[Option[Room]] =
      for {
        slots <- client.getScheduleDays()
          .flatMap(days => Future.sequence(days.map(client.getSchedule)))
          .map(_.flatten)
        rooms <- client.getRooms()
      } yield e2Collections.Devoxx.isSpeaking(slots, rooms, id, time)
  }

  object LazyMonad {

    class Lazy[+A](block: => A) {
      private var evaluated = false
      private lazy val underlying: A = {
        evaluated = true
        block
      }

      def get: A = underlying

      def getOrElse[B >: A](default: => B): B = asTry.getOrElse(default)

      def orElse[B >: A](default: => Lazy[B]): Lazy[B] = Lazy(asTry.orElse(default.asTry).get)

      def isEvaluated: Boolean = evaluated

      def map[B](f: A => B): Lazy[B] = Lazy(f(underlying))

      def flatMap[B](f: A => Lazy[B]): Lazy[B] = Lazy(f(underlying).get)

      def filter(p: A => Boolean): Lazy[A] = Lazy(if (p(underlying)) underlying else throw new NoSuchElementException("filtered Lazy"))

      def withFilter(p: A => Boolean): Lazy[A] = filter(p)

      def flatten[B](implicit ev: A <:< Lazy[B]): Lazy[B] = Lazy(ev(underlying).get)

      def collect[B](pf: PartialFunction[A, B]): Lazy[B] = Lazy(pf.applyOrElse(underlying, (v: A) => throw new NoSuchElementException("Lazy.collect not defined at " + v)))

      def compose[B](o: Lazy[B]): Lazy[A] = Lazy({
        o.get;
        underlying
      })

      def sequence[B](o: Lazy[B]): Lazy[B] = Lazy({
        underlying;
        o.get
      })

      def asOption: Option[A] = asTry.toOption

      def asTry: Try[A] = Try(underlying)

      def asEither: Either[Throwable, A] = asTry.toEither

      def asFuture(implicit ec: ExecutionContext): Future[A] = Future(underlying)(ec)

      override def toString: String =
        if (isEvaluated) s"Lazy($underlying)"
        else "Lazy(not evaluated)"
    }

    object Lazy {
      def apply[A](block: => A): Lazy[A] = new Lazy(block)

      def lazily[A](f: => A): Lazy[A] = new Lazy(f)

      def sequence[A](in: Seq[Lazy[A]]): Lazy[Seq[A]] = Lazy(in.map(_.get))

      //implicit def eval[A](lazy: Lazy[A]): A = lazy.get
    }

  }

}
