package ir.pooyadev.domain.model.local

data class Task(
    val id: Long = 0,
    val taskTitle: String,
    val taskDescription: String,
    val taskAlarmDate: Long,
    val taskUpdatedAt: Long
)
