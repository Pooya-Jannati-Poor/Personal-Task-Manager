package ir.pooyadev.domain.repository.local

import ir.pooyadev.domain.model.local.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun insertOrUpdateTask(task: Task): Long

    suspend fun fetchTasksSortByNewestFirst(): Flow<List<Task>>

    suspend fun fetchTasksSortByOldestFirst(): Flow<List<Task>>

    suspend fun fetchTasksByTaskId(taskId: Long): Task?

    suspend fun updateTask(task: Task): Int

    suspend fun deleteTask(task: Task): Int

    suspend fun clearAllDataTables()

}