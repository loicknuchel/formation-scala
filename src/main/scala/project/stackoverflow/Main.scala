package project.stackoverflow

import project.stackoverflow.dao.{ApiClient, LocalClient}
import helpers.{RealFileClient, RealHttpClient}

object Main {
  val fileClient = new RealFileClient()
  val httpClient = new RealHttpClient()
  val localClient = new LocalClient(fileClient, "src/main/resources/stackoverflow")
  val apiClient = new ApiClient(httpClient)
  val dataService = new DataService(localClient, apiClient)

  def main(args: Array[String]): Unit = {
    val questions = localClient.getQuestions().get
    val answers = localClient.getAnswers().get
    val comments = localClient.getComments().get
    val users = localClient.getUsers().get

    println(
      s"""Data :
         | - ${questions.length} questions (${questions.count(_.is_answered)} answered)
         | - ${answers.length} answers
         | - ${comments.length} comments
         | - ${users.length} users""".stripMargin)

    /*val userIds = (questions.map(_.owner.user_id) ++ answers.map(_.owner.user_id) ++ comments.map(_.owner.user_id)).distinct
    dataService.fetchAndSaveUsers(userIds).map { users =>
      println("users: " + users)
    }.recover {
      case e => println("err: " + e)
    }*/

    //dataService.fetchAndSaveAnswers(localClient.readQuestions().get).map(_ => println("Answers saved !"))
    //dataService.fetchAndSaveComments(localClient.readAnswers().get).map(_ => println("Comments saved !"))

    /*dataService.fetchAndSaveLastQuestions(500)
      .flatMap { questions => dataService.fetchAndSaveAnswers(questions) }
      .flatMap { answers => dataService.fetchAndSaveComments(answers) }
      .map { comments => println("Done !") }*/
  }
}
