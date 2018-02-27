package exercices.web

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.Logging
import exercices.web.database.InMemoryKeyValueStore
import exercices.web.domain.User

import scala.util.{Failure, Success}

// see https://twitter.github.io/finatra/user-guide/index.html
object App extends Logging {
  def main(args: Array[String]): Unit = {
    AppConf.load() match {
      case Success(conf) =>
        val userStore = new InMemoryKeyValueStore[User.Id, User](failureRate = 0)
        val mapper = FinatraObjectMapper.create()
        val server: HttpServer = new AppServer(conf, userStore, mapper)
        server.main(args)
      case Failure(e) =>
        logger.error("Unable to read conf", e)
    }
  }
}
