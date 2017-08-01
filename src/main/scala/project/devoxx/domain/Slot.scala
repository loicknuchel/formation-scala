package project.devoxx.domain

case class Slot(
                 slotId: SlotId,
                 day: String,
                 roomId: String,
                 roomName: String,
                 roomSetup: String,
                 roomCapacity: Int,
                 fromTime: String,
                 fromTimeMillis: Long,
                 toTime: String,
                 toTimeMillis: Long,
                 notAllocated: Boolean,
                 break: Option[Break],
                 talk: Option[Talk]
               )
