package ir.pooyadev.data.remote.model

import com.squareup.moshi.JsonClass
import ir.pooyadev.data.local.entities.TaskEntity

@JsonClass(generateAdapter = true)
data class TaskDto(
    val id: String,
    val title: String,
    val description: String,
    val alarmDate: Long?,
    val createdAt: Long,
) {

    fun toEntity(): TaskEntity {
        return TaskEntity(
            id = this.id.toLong(),
            taskTitle = this.title,
            taskDescription = this.description,
            taskAlarmDate = this.alarmDate,
            taskCreatedAt = this.createdAt
        )

    }
}