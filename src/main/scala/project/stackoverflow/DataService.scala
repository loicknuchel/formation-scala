package project.stackoverflow

import project.stackoverflow.domain.{Answer, Comment, Question}
import project.stackoverflow.helpers.FutureUtils
import utils.stackoverflow.domain._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class DataService(localClient: LocalClient, apiClient: ApiClient) {

  private def fetchAndSave[A, B](items: Seq[A], fetch: A => Future[Seq[B]], save: B => Try[Unit]): Future[Seq[B]] = {
    FutureUtils.execSeq(items, (item: A) => fetch(item).map { results =>
      results.map(save)
      results
    }).map(_.flatten)
  }

  def fetchAndSaveLastQuestions(number: Int): Future[Seq[Question]] =
    fetchAndSave[Int, Question](Seq(number), apiClient.lastQuestions(_), localClient.write)

  def fetchAndSaveAnswers(questions: Seq[Question]): Future[Seq[Answer]] =
    fetchAndSave[Question, Answer](questions.filter(_.is_answered), q => apiClient.answers(q.question_id), localClient.write)

  def fetchAndSaveComments(answers: Seq[Answer]): Future[Seq[Comment]] =
    fetchAndSave[Answer, Comment](answers, a => apiClient.comments(a.answer_id), localClient.write)

  def fetchAndSaveUsers(ids: Seq[UserId]): Future[Seq[User]] =
    fetchAndSave[Seq[UserId], User](ids.distinct.sliding(100, 100).toSeq, ids => apiClient.users(ids), localClient.write)
}
