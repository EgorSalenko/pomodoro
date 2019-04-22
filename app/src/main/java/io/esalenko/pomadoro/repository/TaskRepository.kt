package io.esalenko.pomadoro.repository

import io.esalenko.pomadoro.domain.dao.TaskDao
import io.esalenko.pomadoro.domain.model.Task
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class TaskRepository(private val taskDao: TaskDao) : Repository<Task> {

    override fun getAll(): Maybe<List<Task>> {
        return taskDao
            .getAll()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun get(id: Long): Task {
        return taskDao.get(id)
    }

    override fun get(item: Task): Task {
        return taskDao.get(item.id)
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

    fun archive(id: Long) {
        val task: Task = taskDao.get(id)
        task.isArchived = true
        taskDao.insert(task)
    }

    fun getAllByPriority(): Maybe<List<Task>> {
        return taskDao
            .getAllByPriority()
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

}