package exercices.web.domain

import java.util.UUID

import scala.util.Try

case class User(id: User.Id,
                firstName: String,
                lastName: String)

case class UserNoId(firstName: String,
                    lastName: String) {
  def generate: User =
    User(
      id = User.Id.generate(),
      firstName = firstName,
      lastName = lastName)

  def withId(id: User.Id): User =
    User(
      id = id,
      firstName = firstName,
      lastName = lastName)
}

object User {

  case class Id(value: String) { // extends AnyVal // does not work well with FinatraObjectMapper
    require(Id.isValid(value), s"Invalid User.Id: $value")
  }

  object Id {
    def generate(): Id = Id(UUID.randomUUID().toString)

    def isValid(in: String): Boolean = Try(UUID.fromString(in)).isSuccess
  }

}
