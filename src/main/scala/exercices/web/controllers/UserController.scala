package exercices.web.controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.response.ResponseBuilder
import com.twitter.finatra.json.FinatraObjectMapper
import exercices.web.database.KeyValueStore
import exercices.web.domain.api.ApiError
import exercices.web.domain.{User, UserNoId}
import exercices.web.utils.Extensions._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try
import scala.util.control.NonFatal

class UserController(store: KeyValueStore[User.Id, User], mapper: FinatraObjectMapper) extends Controller {
  /**
    * Endpoint to create a new user and return a User.Id
    *
    * Tip: FinatraObjectMapper is able to parse String into case classes
    * Tip: finatra response: response.ok.json(myObject), it will serialize your object to json (introspection)
    */
  post("/api/users") { req: Request =>
    (for {
      userNoId <- Try(mapper.parse[UserNoId](req)).toFuture
        .recoverWith { case NonFatal(e) => println("err", e); Future.failed(e) }
      id <- createUser(userNoId)
    } yield response.ok(id)).recover(manageError)
  }

  val manageError: PartialFunction[Throwable, ResponseBuilder#EnrichedResponse] = {
    case e: Throwable => response.internalServerError(ApiError(e.getClass.getSimpleName, e.getMessage))
  }

  private def createUser(userNoId: UserNoId): Future[User.Id] = {
    val user = userNoId.generate
    store.create(user.id, user).map(_ => user.id)
  }

  /**
    * Endpoint to retrieve a user given its id
    *
    * Tip: look at 'Request' methods
    */
  get("/api/users/:id") { req: Request =>
    (for {
      id <- Try(User.Id(req.getParam("id"))).toFuture
      userOpt <- store.read(id)
    } yield userOpt match {
      case Some(user) => response.ok(user)
      case None => response.notFound
    }).recover(manageError)
  }

  /**
    * Endpoint to retrieve all users
    *
    * Tip: look at 'store' available methods
    * Tip2: you may need Future.sequence somewhere...
    * Tip3: finatra controllers want twitter Future instead of scala Future, you should transform them (see utils.Extensions)
    */
  get("/api/users") { _: Request =>
    store.keys().flatMap(ids =>
      Future.sequence(ids.map(id => store.read(id))).map({ users: Seq[Option[User]] =>
        response.ok(users.flatten)
      })
    )
    (for {
      ids: Seq[User.Id] <- store.keys()
      users: Seq[Option[User]] <- Future.sequence(ids.map(id => store.read(id)))
    } yield response.ok(users.flatten))
      .recover(manageError)
  }

  /**
    * Endpoint to update an existing user and return the User.Id
    */
  put("/api/users/:id") { req: Request =>
    (for {
      id <- Try(User.Id(req.getParam("id"))).toFuture
      userNoId <- Try(mapper.parse[UserNoId](req)).toFuture
      _ <- store.update(id, userNoId.withId(id))
    } yield response.ok(id))
      .recover(manageError)
  }

  /**
    * Endpoint to delete a user and return a boolean
    */
  delete("/api/users/:id") { req: Request =>
    (for {
      id <- Try(User.Id(req.getParam("id"))).toFuture
      res <- store.delete(id)
    } yield response.ok(res))
      .recover(manageError)
  }
}
