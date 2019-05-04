package io.esalenko.pomadoro.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.esalenko.pomadoro.db.dao.CategoryDao
import io.esalenko.pomadoro.db.dao.TaskDao
import io.esalenko.pomadoro.db.model.converter.DateConverter
import io.esalenko.pomadoro.db.model.converter.PriorityConverter
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.db.model.task.TaskCategory

@Database(entities = [Task::class, TaskCategory::class], version = 1)
@TypeConverters(PriorityConverter::class, DateConverter::class)
abstract class LocalRoomDatabase : RoomDatabase() {

    companion object {

        private const val DB_NAME = "pomadoro_local_db"

        @Volatile
        @JvmStatic
        private var DB_INSTANCE: LocalRoomDatabase? = null

        @JvmStatic
        fun getInstance(ctx: Context): LocalRoomDatabase {
            if (DB_INSTANCE == null) {
                synchronized(LocalRoomDatabase::class) {
                    DB_INSTANCE = Room
                        .databaseBuilder(ctx.applicationContext, LocalRoomDatabase::class.java, DB_NAME)
                        .build()
                }
            }
            return DB_INSTANCE!!
        }
    }

    abstract val taskDao: TaskDao

    abstract val categoryDao: CategoryDao
}