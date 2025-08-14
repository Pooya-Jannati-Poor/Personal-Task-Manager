package ir.pooyadev.domain.repository.local

import ir.pooyadev.domain.model.local.Task

interface TaskRepository {

    suspend fun insertTask(task: Task): Long

    suspend fun fetchTasks(): List<Task>?

    suspend fun fetchTasksByTaskId(taskId: Long): Task?

    suspend fun updateTask(task: Task): Int

    suspend fun deleteTask(task: Task): Int

    suspend fun clearAllDataTables()

}