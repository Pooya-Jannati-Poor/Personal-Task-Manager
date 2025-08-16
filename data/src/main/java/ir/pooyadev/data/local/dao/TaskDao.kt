package ir.pooyadev.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ir.pooyadev.data.local.entities.TaskEntity

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTask(task: TaskEntity): Long

    @Query("SELECT * FROM task_table ORDER BY taskUpdatedAt ASC")
    suspend fun fetchTasks(): List<TaskEntity>?

    @Query("SELECT * FROM task_table WHERE id = :taskId")
    suspend fun fetchTasksByTaskId(taskId: Long): TaskEntity?

    @Update
    suspend fun updateTask(task: TaskEntity): Int

    @Delete
    suspend fun deleteTask(task: TaskEntity): Int

}