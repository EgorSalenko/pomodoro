package io.esalenko.pomadoro.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.esalenko.pomadoro.db.model.task.Category
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface CategoryDao {

    @Query("select * from task_category")
    fun getCategories(): Maybe<List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: Category)

    @Query("select * from task_category where category_name=:name")
    fun get(name: String): Single<Category>

    @Query("delete from task_category where category_name=:name")
    fun delete(name: String)

    @Query("delete from task_category")
    fun deleteAll()

    @Query("select * from task_category")
    fun getAllLiveData(): LiveData<List<Category>>

}