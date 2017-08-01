package project.devoxx.domain

case class Talk(
                 id: TalkId,
                 talkType: String,
                 trackId: String,
                 track: String,
                 lang: String,
                 title: String,
                 summary: String,
                 summaryAsHtml: String,
                 speakers: Seq[LinkWithName]
               )

case class AcceptedTalk(
                         id: TalkId,
                         talkType: String,
                         track: String,
                         title: String,
                         links: Seq[Link]
                       )
