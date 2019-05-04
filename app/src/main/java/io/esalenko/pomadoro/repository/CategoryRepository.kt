package io.esalenko.pomadoro.repository

import io.esalenko.pomadoro.db.dao.CategoryDao
import io.esalenko.pomadoro.db.model.task.TaskCategory
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class CategoryRepository(private val categoryDao: CategoryDao) : Repository<TaskCategory> {

    override fun get(id: Long): Single<TaskCategory> {
        throw Exception("TaskCategory does not have id property, use get by categoryName instead")
    }

    override fun getAll(): Maybe<List<TaskCategory>> {
        return categoryDao.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun get(item: TaskCategory): Single<TaskCategory> {
        return categoryDao.get(item.categoryName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun add(item: TaskCategory) {
        categoryDao.insert(item)
    }

    override fun delete(id: Long) {
        throw Exception("TaskCategory does not have id property, use get by categoryName instead")
    }

    override fun delete(item: TaskCategory) {
        categoryDao.delete(item.categoryName)
    }

    override fun deleteAll() {
        categoryDao.deleteAll()
    }
}