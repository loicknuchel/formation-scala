package utils.stackoverflow.domain

case class User(
                 account_id: AccountId,
                 user_id: UserId,
                 display_name: String,
                 user_type: UserType,
                 is_employee: Boolean,
                 profile_image: Link,
                 website_url: Option[Link],
                 link: Link,
                 badge_counts: Map[String, Int],
                 accept_rate: Option[Int],
                 reputation: Int,
                 reputation_change_day: Int,
                 reputation_change_week: Int,
                 reputation_change_month: Int,
                 reputation_change_quarter: Int,
                 reputation_change_year: Int,
                 creation_date: Timestamp,
                 last_access_date: Timestamp,
                 last_modified_date: Option[Timestamp]
               ) extends Entity {
  val id: String = user_id.toString
}

case class UserEmbed(
                      user_id: UserId,
                      user_type: UserType,
                      display_name: String,
                      profile_image: Link,
                      link: Link,
                      reputation: Int,
                      accept_rate: Option[Int]
                    ) extends Entity {
  val id: String = user_id.toString
}
