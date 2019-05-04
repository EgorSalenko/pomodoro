package io.esalenko.pomadoro.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.esalenko.pomadoro.db.model.task.Priority
import io.esalenko.pomadoro.db.model.task.Task
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

    @Query("select * from task_table where priority =:priority order by date DESC")
    fun getAllByPriority(priority: Priority): Maybe<List<Task>>

    @Query("select * from task_table where isArchived == 1 order by date DESC")
    fun getAllArchived(): Maybe<List<Task>>

    @Query("select * from task_table where isCompleted == 1 order by date DESC")
    fun getAllCompleted(): Maybe<List<Task>>

    @Query("select pomidors from task_table where id =:id")
    fun getSessions(id: Long): LiveData<Int>

    @Query("select isCooldown from task_table where id =:id")
    fun getTaskCooldown(id: Long): LiveData<Boolean>

    @Query("select * from task_table where isArchived == 0 order by date DESC")
    fun getAllLiveData(): LiveData<List<Task>>

    @Query("select * from task_table where id =:id")
    fun getTaskLiveData(id: Long): LiveData<Task>
}