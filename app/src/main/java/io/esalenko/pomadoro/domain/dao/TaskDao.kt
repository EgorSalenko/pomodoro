package io.esalenko.pomadoro.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.esalenko.pomadoro.domain.model.Task
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task)

    @Query("select * from task_table where isArchived == 0 order by date DESC")
    fun getAll(): Maybe<List<Task>>

    @Query("select * from task_table where id =:id")
    fun get(id: Long): Single<Task>

    @Query("delete from task_table where id=:id")
    fun delete(id: Long)

    @Query("delete from task_table")
    fun deleteAll()

    @Query("select * from task_table where isArchived == 0 order by priority ASC")
    fun getAllByPriority(): Maybe<List<Task>>

    @Query("select * from task_table where isArchived == 1 order by date DESC")
    fun getAllArchived(): Maybe<List<Task>>
}