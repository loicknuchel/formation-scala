package exercices.web

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter
import com.twitter.finatra.json.FinatraObjectMapper
import exercices.web.controllers.{MainController, UserController}
import exercices.web.database.KeyValueStore
import exercices.web.domain.User

class AppServer(conf: AppConf, store: KeyValueStore[User.Id, User], mapper: FinatraObjectMapper) extends HttpServer {
  override protected def configureHttp(router: HttpRouter): Unit = {
    router
      .add(new MainController(conf.hello))
      .add(new UserController(store, mapper))
  }
}
