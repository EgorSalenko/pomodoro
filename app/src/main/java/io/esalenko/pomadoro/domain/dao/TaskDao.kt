package io.esalenko.pomadoro.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.esalenko.pomadoro.domain.model.Task
import io.reactivex.Maybe

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Query("select * from task_table")
    fun getAll(): Maybe<List<Task>>

    @Query("select * from task_table where id =:id")
    fun get(id: Long): Task

    @Query("delete from task_table where id=:id")
    fun delete(id: Long)

    @Query("delete from task_table")
    fun deleteAll()

}