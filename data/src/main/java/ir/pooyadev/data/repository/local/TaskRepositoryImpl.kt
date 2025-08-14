package ir.pooyadev.data.repository.local

import ir.pooyadev.data.local.AppDatabase
import ir.pooyadev.data.local.dao.TaskDao
import ir.pooyadev.data.local.entities.TaskEntity
import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.domain.repository.local.TaskRepository
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao,
    private val appDatabase: AppDatabase
) : TaskRepository {

    override suspend fun insertTask(task: Task): Long {
        val taskEntity = TaskEntity.fromDomain(task)
        return taskDao.insertTask(taskEntity)
    }

    override suspend fun fetchTasks(): List<Task>? {
        return taskDao.fetchTasks()?.map { it.toDomain() }
    }

    override suspend fun fetchTasksByTaskId(taskId: Long): Task? {
        return taskDao.fetchTasksByTaskId(taskId)?.toDomain()
    }

    override suspend fun updateTask(task: Task): Int {
        val taskEntity = TaskEntity.fromDomain(task)
        return taskDao.updateTask(taskEntity)
    }

    override suspend fun deleteTask(task: Task): Int {
        val taskEntity = TaskEntity.fromDomain(task)
        return taskDao.deleteTask(taskEntity)
    }

    override suspend fun clearAllDataTables() {
        appDatabase.clearAllTables()
    }
}