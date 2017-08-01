package project.devoxx.domain

case class Link(
                 title: String,
                 href: String,
                 rel: String
               )

case class LinkWithName(
                         name: String,
                         link: Link
                       )
