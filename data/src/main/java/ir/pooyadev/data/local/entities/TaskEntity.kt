package ir.pooyadev.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import ir.pooyadev.domain.model.local.Task

@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskTitle: String,
    val taskDescription: String,
    val taskAlarmDate: Long?,
    val taskCreatedAt: Long,
) {
    fun toDomain(): Task {
        return Task(
            id = this.id,
            taskTitle = this.taskTitle,
            taskDescription = this.taskDescription,
            taskAlarmDate = this.taskAlarmDate,
            taskCreatedAt = this.taskCreatedAt
        )
    }

    companion object {
        fun fromDomain(task: Task): TaskEntity {
            return TaskEntity(
                id = task.id,
                taskTitle = task.taskTitle,
                taskDescription = task.taskDescription,
                taskAlarmDate = task.taskAlarmDate,
                taskCreatedAt = task.taskCreatedAt
            )
        }
    }
}
