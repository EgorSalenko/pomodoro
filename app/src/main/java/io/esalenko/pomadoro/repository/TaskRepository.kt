package io.esalenko.pomadoro.repository

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import io.esalenko.pomadoro.db.dao.TaskDao
import io.esalenko.pomadoro.db.model.task.Priority
import io.esalenko.pomadoro.db.model.task.Task
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TaskRepository(private val taskDao: TaskDao) : Repository<Task> {

    fun observeList(): LiveData<List<Task>> {
        return taskDao.getAllLiveData()
    }

    override fun getAll(): Maybe<List<Task>> {
        return taskDao
            .getAll()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun get(id: Long): Single<Task> {
        return taskDao.get(id)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    fun getSessions(id: Long): LiveData<Int> {
        return taskDao.getSessions(id)
    }

    override fun get(item: Task): Single<Task> {
        return taskDao.get(item.id)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun add(item: Task) {
        taskDao.insert(item)
    }

    override fun delete(id: Long) {
        taskDao.delete(id)
    }

    override fun delete(item: Task) {
        taskDao.delete(item.id)
    }

    override fun deleteAll() {
        taskDao.deleteAll()
    }

    @SuppressLint("CheckResult")
    fun archive(id: Long) {
        taskDao.get(id)
            .subscribe(
                { task ->
                    task.isArchived = true
                    taskDao.insert(task)
                },
                { error ->
                    error { error }
                }
            )
    }

    fun getAllByPriority(priority: Priority): Maybe<List<Task>> {
        return taskDao
            .getAllByPriority(priority)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    fun getAllArchived(): Maybe<List<Task>> {
        return taskDao.getAllArchived()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    fun getAllArchivedByPriority(priority: Priority): Maybe<List<Task>> {
        return taskDao.getAllArchivedByPriority(priority)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    fun getAllCompleted(): Maybe<List<Task>> {
        return taskDao.getAllCompleted()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    fun getAllCompletedByPriority(priority: Priority): Maybe<List<Task>> {
        return taskDao.getAllCompletedByPriority(priority)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    fun getTaskCooldown(taskId: Long): LiveData<Boolean> {
        return taskDao.getTaskCooldown(taskId)
    }

    fun getCompletedCount(): LiveData<Long> = taskDao.getCompletedCount()

    fun getArchivedCount(): LiveData<Long> = taskDao.getArchivedCount()

    fun getTotalCount(): LiveData<Long> = taskDao.getTotalCount()
}
