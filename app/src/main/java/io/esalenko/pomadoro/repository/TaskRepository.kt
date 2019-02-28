package io.esalenko.pomadoro.repository

import io.esalenko.pomadoro.domain.dao.TaskDao
import io.esalenko.pomadoro.domain.model.Task
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) : Repository<Task> {

    override fun getAll(): List<Task> {
        return taskDao.getAll()
    }

    override fun get(id: Long): Task {
        return taskDao.get(id)
    }

    override fun get(item: Task): Task {
        return taskDao.get(item.id)
    }

    override fun add(item: Task) {
        return taskDao.insert(item)
    }

    override fun delete(id: Long) {
        return taskDao.delete(id)
    }

    override fun delete(item: Task) {
        return taskDao.delete(item.id)
    }

    override fun deleteAll() {
        return taskDao.deleteAll()
    }

}