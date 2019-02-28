package io.esalenko.pomadoro.domain.dao

import androidx.room.*
import io.esalenko.pomadoro.domain.model.Task

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Query("select * from task_table")
    fun getAll(): List<Task>

    @Query("select * from task_table where id =:id")
    fun get(id: Long): Task

    @Query("delete from task_table where id=:id")
    fun delete(id: Long)

    @Query("delete from task_table")
    fun deleteAll()

}