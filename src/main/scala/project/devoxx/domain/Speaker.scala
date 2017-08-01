package project.devoxx.domain

case class Speaker(
                    uuid: SpeakerId,
                    firstName: String,
                    lastName: String,
                    bio: Option[String] = None,
                    bioAsHtml: Option[String] = None,
                    avatarURL: Option[String] = None,
                    company: Option[String] = None,
                    blog: Option[String] = None,
                    twitter: Option[String] = None,
                    lang: Option[String] = None,
                    acceptedTalks: Option[Seq[AcceptedTalk]] = None
                  )
