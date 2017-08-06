package utils.stackoverflow.domain

case class ApiResponse[T](
                           items: Seq[T],
                           has_more: Boolean,
                           quota_max: Int,
                           quota_remaining: Int
                         )
