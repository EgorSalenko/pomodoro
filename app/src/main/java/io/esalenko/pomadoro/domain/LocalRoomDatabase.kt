package io.esalenko.pomadoro.domain

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.esalenko.pomadoro.domain.dao.TaskDao
import io.esalenko.pomadoro.domain.model.Task

@Database(entities = [Task::class], version = 1)
abstract class LocalRoomDatabase : RoomDatabase() {

    companion object {

        private val DB_NAME = "pomodoro_local_db"

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