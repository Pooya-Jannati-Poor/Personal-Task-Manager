package ir.pooyadev.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import ir.pooyadev.data.local.dao.TaskDao
import ir.pooyadev.data.local.entities.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}