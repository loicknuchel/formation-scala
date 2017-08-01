package project.devoxx.domain

case class Schedule(
                     slots: Seq[Slot]
                   )

case class ScheduleList(
                         links: Seq[Link]
                       )
