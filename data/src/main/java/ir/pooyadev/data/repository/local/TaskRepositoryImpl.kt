package ir.pooyadev.data.repository.local

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.pooyadev.data.local.AppDatabase
import ir.pooyadev.data.local.dao.TaskDao
import ir.pooyadev.data.local.entities.TaskEntity
import ir.pooyadev.data.remote.RemoteDataSource
import ir.pooyadev.domain.model.local.Task
import ir.pooyadev.domain.model.remote.RemoteResult
import ir.pooyadev.domain.repository.local.TaskRepository
import ir.pooyadev.domain.usecases.reminder.CancelTaskAlarmUseCase
import ir.pooyadev.domain.usecases.reminder.ScheduleTaskAlarmUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteDataSource: RemoteDataSource,
    private val taskDao: TaskDao,
    private val appDatabase: AppDatabase,
    private val scheduleTaskAlarmUseCase: ScheduleTaskAlarmUseCase,
    private val cancelTaskAlarmUseCase: CancelTaskAlarmUseCase
) : TaskRepository {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override suspend fun insertOrUpdateTask(task: Task): Long {
        val taskEntity = TaskEntity.fromDomain(task)
        return taskDao.insertOrUpdateTask(taskEntity)
    }

    override suspend fun fetchTasksSortByNewestFirst(): Flow<List<Task>> {
        return taskDao.fetchTasksSortByNewestFirst().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun fetchTasksSortByOldestFirst(): Flow<List<Task>> {
        return taskDao.fetchTasksSortByOldestFirst().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncTasks(): RemoteResult<Unit> {
        return when (val remoteResult = remoteDataSource.fetchTasks()) {
            is RemoteResult.Success -> {
                val remoteTasks = remoteResult.data
                if (remoteTasks.isNullOrEmpty()) {
                    return RemoteResult.Success(Unit)
                }

                val tasksToInsertOrUpdate = mutableListOf<TaskEntity>()

                for (remoteTask in remoteTasks) {
                    val localTask = taskDao.fetchTasksByTaskId(remoteTask.id.toLong())
                    val alarmTime = if (remoteTask.alarmDate!! > 0L) remoteTask.alarmDate else null

                    try {
                        val taskIdAsLong = remoteTask.id.toLong()

                        val canScheduleExactAlarms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            alarmManager.canScheduleExactAlarms()
                        } else {
                            true
                        }

                        if (localTask == null) {
                            tasksToInsertOrUpdate.add(remoteTask.toEntity())

                            if (canScheduleExactAlarms) {
                                alarmTime?.let { time ->
                                    scheduleTaskAlarmUseCase(time, taskIdAsLong)
                                }
                            }

                        } else if (remoteTask.createdAt > localTask.taskCreatedAt) {
                            tasksToInsertOrUpdate.add(remoteTask.toEntity())

                            if (canScheduleExactAlarms) {
                                cancelTaskAlarmUseCase(taskIdAsLong)
                                alarmTime?.let { time ->
                                    scheduleTaskAlarmUseCase(time, taskIdAsLong)
                                }
                            }
                        }

                    } catch (e: NumberFormatException) {
                        Log.e("TaskRepositoryImpl", "Invalid ID format from API: ${remoteTask.id}")
                    }
                }

                if (tasksToInsertOrUpdate.isNotEmpty()) {
                    tasksToInsertOrUpdate.forEach { task ->
                        taskDao.insertOrUpdateTask(task)
                    }
                }

                RemoteResult.Success(Unit)
            }
            is RemoteResult.Error -> {
                RemoteResult.Error(remoteResult.message ?: "Sync failed")
            }
            is RemoteResult.Loading -> {
                RemoteResult.Loading()
            }
        }
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