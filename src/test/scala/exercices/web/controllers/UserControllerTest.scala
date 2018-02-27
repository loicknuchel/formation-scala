package exercices.web.controllers

import com.twitter.finagle.http.Response
import com.twitter.finagle.http.Status._
import com.twitter.finatra.http.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.server.FeatureTest
import exercices.web.database.InMemoryKeyValueStore
import exercices.web.domain.api.ApiError
import exercices.web.domain.{User, UserNoId}
import exercices.web.{AppConf, AppServer, HelloConf}

import scala.util.{Success, Try}

class UserControllerTest extends FeatureTest {
  private val conf = AppConf(HelloConf("world"))
  private val store = new InMemoryKeyValueStore[User.Id, User](0)
  private val mapper = FinatraObjectMapper.create()
  override val server = new EmbeddedHttpServer(new AppServer(conf, store, mapper))

  test("CRUD behaviour") {
    // should return an empty list
    server.httpGet(path = "/api/users", andExpect = Ok, withBody = "[]")

    // should save a new user
    val user = UserNoId("a", "a")
    val res = server.httpPost(path = "/api/users", postBody = asJson(user), andExpect = Ok)
    val id = parse[User.Id](res)
    id.isSuccess shouldBe true

    // should retrieve the saved user
    val res2 = server.httpGet(path = s"/api/users/${id.get.value}", andExpect = Ok)
    val user2 = parse[User](res2)
    user2 shouldBe Success(user.withId(id.get))

    // should retrieve user list with one user
    val res3 = server.httpGet(path = "/api/users", andExpect = Ok)
    val users = parse[Seq[User]](res3)
    users.map(_.length) shouldBe Success(1)

    // should update the user
    val updatedUser = user.copy(lastName = "b")
    server.httpPut(path = s"/api/users/${id.get.value}", putBody = asJson(updatedUser), andExpect = Ok)

    // should retrieve the updated user
    val res4 = server.httpGet(path = s"/api/users/${id.get.value}", andExpect = Ok)
    val user4 = parse[User](res4)
    user4 shouldBe Success(updatedUser.withId(id.get))

    // should delete the user
    server.httpDelete(path = s"/api/users/${id.get.value}", andExpect = Ok)

    // should retrieve the deleted user
    server.httpGet(path = s"/api/users/${id.get.value}", andExpect = NotFound)

    // should retrieve user list with no user
    val res5 = server.httpGet(path = "/api/users", andExpect = Ok)
    parse[Seq[User]](res5).map(_.length) shouldBe Success(0)
  }

  test("wrong payload when create user") {
    val res = server.httpPost(path = "/api/users", postBody = """{}""", andExpect = InternalServerError)
    val err = parse[ApiError](res)
    err.isSuccess shouldBe true
    err.map(_.error) shouldBe Success("CaseClassMappingException")
  }

  test("wrong id when get user") {
    val res = server.httpGet(path = s"/api/users/abc", andExpect = InternalServerError)
    val err = parse[ApiError](res)
    err.isSuccess shouldBe true
    err.map(_.error) shouldBe Success("IllegalArgumentException")
  }

  test("invalid id when updating user") {
    val user = UserNoId("a", "a")
    val res = server.httpPut(path = s"/api/users/1ff5380f-58db-41f9-a8a3-df48ef8625e3", putBody = asJson(user), andExpect = InternalServerError)
    val err = parse[ApiError](res)
    err.isSuccess shouldBe true
    err.map(_.error) shouldBe Success("Exception")
  }

  private def asJson[A](value: A): String =
    server.mapper.writeValueAsString(value)

  private def fromJson[A: Manifest](json: String): Try[A] =
    Try(server.mapper.parse[A](json))

  private def parse[A: Manifest](res: Response): Try[A] =
    fromJson(res.contentString)
}
