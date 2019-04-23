package io.esalenko.pomadoro.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.esalenko.pomadoro.domain.dao.TaskDao
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.domain.model.converter.CategoryConverter
import io.esalenko.pomadoro.domain.model.converter.DateConverter
import io.esalenko.pomadoro.domain.model.converter.PriorityConverter

@Database(entities = [Task::class], version = 1)
@TypeConverters(CategoryConverter::class, PriorityConverter::class, DateConverter::class)
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

}