package exercices.web.controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.json.FinatraObjectMapper
import exercices.web.database.KeyValueStore
import exercices.web.domain.{User, UserNoId}
import exercices.web.utils.Extensions._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class UserController(store: KeyValueStore[User.Id, User], mapper: FinatraObjectMapper) extends Controller {
  /**
    * Endpoint to retrieve all users
    *
    * Tip: look at 'store' available methods
    * Tip2: finatra controllers want twitter Future instead of scala Future, you should transform them (see utils.Extensions)
    */
  get("/api/users") { _: Request =>
    val res: Future[Seq[User]] = store.keys().flatMap { ids: Seq[User.Id] =>
      val a: Seq[Future[Option[User]]] = ids.map(id => store.read(id))
      val b: Future[Seq[Option[User]]] = Future.sequence(a)
      b
    }.map { userOptions: Seq[Option[User]] =>
      userOptions.flatten
    }

    val res1: Future[Seq[User]] = for {
      ids <- store.keys()
      users <- Future.sequence(ids.map(store.read))
    } yield users.flatten

    res1.map(users => response.ok.json(users)).toTwitter.rescue(handleError)
  }

  /**
    * Endpoint to create a new user
    *
    * Tip: FinatraObjectMapper is able to parse String into case classes
    */
  post("/api/users") { req: Request =>
    /*val user = userNoId.generate
    val res = store.create(user.id, user).map(_ => user.id)
    res.map(response.ok.json).toTwitter.rescue(handleError)*/

    /*val userTry = Try(mapper.parse[UserNoId](req.contentString))
    val id = User.Id.generate()
    userTry match {
      case Success(user) =>
        store.create(id, User(id, user.firstName, user.lastName)).map { _ =>
          response.ok.json(id)
        }.toTwitter.rescue(handleError)
      case Failure(e) => response.internalServerError.toFuture
    }*/

    val res: Future[User.Id] = for {
      userNoId <- Try(mapper.parse[UserNoId](req.contentString)).toFuture
      user = userNoId.generate
      _ <- store.create(user.id, user)
    } yield user.id
    res.map(response.ok.json).toTwitter.rescue(handleError)
  }

  /**
    * Endpoint to retrieve a user given its id
    *
    * Tip: look at 'Request' methods
    */
  get("/api/users/:id") { req: Request =>
    val res: Future[Option[User]] = for {
      id <- Try(User.Id(req.getParam("id"))).toFuture
      userOpt <- store.read(id)
    } yield userOpt

    res.map {
      case Some(user) => response.ok.json(user)
      case None => response.notFound
    }.toTwitter.rescue(handleError)
  }

  /**
    * Endpoint to update an existing user
    */
  put("/api/users/:id") { req: Request =>
    val res: Future[User.Id] = for {
      id <- Try(User.Id(req.getParam("id"))).toFuture
      userNoId <- Try(mapper.parse[UserNoId](req.contentString)).toFuture
      _ <- store.update(id, userNoId.withId(id))
    } yield id
    res.map(response.ok.json).toTwitter.rescue(handleError)
  }

  /**
    * Endpoint to delete a user
    */
  delete("/api/users/:id") { req: Request =>
    val res: Future[User.Id] = for {
      id <- Try(User.Id(req.getParam("id"))).toFuture
      _ <- store.delete(id)
    } yield id
    res.map(response.ok.json).toTwitter.rescue(handleError)
  }
}
