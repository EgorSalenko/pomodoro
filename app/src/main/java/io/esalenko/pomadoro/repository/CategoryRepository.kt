package io.esalenko.pomadoro.repository

import androidx.lifecycle.LiveData
import io.esalenko.pomadoro.db.dao.CategoryDao
import io.esalenko.pomadoro.db.model.task.Category
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class CategoryRepository(private val categoryDao: CategoryDao) : Repository<Category> {

    override fun get(id: Long): Single<Category> {
        throw Exception("Category does not have id property, use get by categoryName instead")
    }

    override fun getAll(): Maybe<List<Category>> {
        return categoryDao.getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAllLiveData(): LiveData<List<Category>> {
        return categoryDao.getAllLiveData()
    }

    override fun get(item: Category): Single<Category> {
        return categoryDao.get(item.categoryName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    override fun add(item: Category) {
        categoryDao.insert(item)
    }

    override fun delete(id: Long) {
        throw Exception("Category does not have id property, use get by categoryName instead")
    }

    override fun delete(item: Category) {
        categoryDao.delete(item.categoryName)
    }

    override fun deleteAll() {
        categoryDao.deleteAll()
    }
}