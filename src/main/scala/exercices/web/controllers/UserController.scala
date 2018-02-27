package exercices.web.controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.json.FinatraObjectMapper
import exercices.web.database.KeyValueStore
import exercices.web.domain.{User, UserNoId}
import exercices.web.utils.Extensions._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

class UserController(store: KeyValueStore[User.Id, User], mapper: FinatraObjectMapper) extends Controller {
  /**
    * Endpoint to retrieve all users
    *
    * Tip: look at 'store' available methods
    * Tip2: finatra controllers want twitter Future instead of scala Future, you should transform them (see utils.Extensions)
    */
  get("/api/users") { _: Request =>
    ???
  }

  /**
    * Endpoint to create a new user
    *
    * Tip: FinatraObjectMapper is able to parse String into case classes
    */
  post("/api/users") { req: Request =>
    ???
  }

  /**
    * Endpoint to retrieve a user given its id
    *
    * Tip: look at 'Request' methods
    */
  get("/api/users/:id") { req: Request =>
    ???
  }

  /**
    * Endpoint to update an existing user
    */
  put("/api/users/:id") { req: Request =>
    ???
  }

  /**
    * Endpoint to delete a user
    */
  delete("/api/users/:id") { req: Request =>
    ???
  }
}
