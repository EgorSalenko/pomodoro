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

    override fun get(id: Long): Maybe<Task> {
        return taskDao.get(id)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun get(item: Task): Maybe<Task> {
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

}