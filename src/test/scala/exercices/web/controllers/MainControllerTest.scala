package exercices.web.controllers

import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.server.FeatureTest
import exercices.web.database.InMemoryKeyValueStore
import exercices.web.domain.User
import exercices.web.{AppConf, AppServer, HelloConf}

class MainControllerTest extends FeatureTest {
  private val conf = AppConf(HelloConf("world"))
  private val store = new InMemoryKeyValueStore[User.Id, User](0)
  private val mapper = FinatraObjectMapper.create()
  override val server = new EmbeddedHttpServer(new AppServer(conf, store, mapper))

  test("/hello should answer 'world'") {
    server.httpGet(path = "/hello", andExpect = Ok, withBody = "world")
  }
}
