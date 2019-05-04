package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.esalenko.pomadoro.db.model.Filter
import io.esalenko.pomadoro.db.model.task.Category
import io.esalenko.pomadoro.db.model.task.Priority
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.repository.CategoryRepository
import io.esalenko.pomadoro.repository.TaskRepository
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.vm.common.BaseViewModel
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.error
import java.util.*


class ToDoListViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    sharedPreferenceManager: SharedPreferenceManager
) :
    BaseViewModel() {

    private val _categoryLiveData = MutableLiveData<RxResult<List<Category>>>()
    val categoryLiveData: LiveData<RxResult<List<Category>>>
        get() = _categoryLiveData

    private var _toDoListLiveData = MutableLiveData<RxResult<List<Task>>>()
    val toDoListLiveData: LiveData<RxResult<List<Task>>>
        get() = _toDoListLiveData

    private var cachedPriority = MutableLiveData<Priority?>()
    private var cachedFilter = MutableLiveData<Filter>()

    val cachedPriorityTransformations = Transformations.switchMap(cachedPriority) { priority ->
        getToDoListBy()
        _toDoListLiveData
    }

    val cachedFilterTransformations = Transformations.switchMap(cachedFilter) { filter ->
        return@switchMap if (filter == null) {
            null
        } else {
            getToDoListBy()
            _toDoListLiveData
        }
    }

    init {
        if (sharedPreferenceManager.isFirstInit) {
            Observable.fromIterable(
                arrayListOf(
                    "Work",
                    "Education",
                    "Sport",
                    "Household"
                )
            )
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe { categoryName: String ->
                    categoryRepository.add(Category(categoryName))
                }
                .addToCompositeDisposable()
            sharedPreferenceManager.firstInit = 1
        }
    }

    val toDoList: LiveData<List<Task>> = taskRepository.observeList()

    val totalCount = taskRepository.getTotalCount()
    val completedCount = taskRepository.getCompletedCount()
    val archivedCount = taskRepository.getArchivedCount()

    fun setPriority(priority: Priority?) {
        cachedPriority.value = priority
    }

    fun setFilter(filter: Filter) {
        cachedFilter.value = filter
    }

    fun getToDoListBy() {
        val priority: Priority? = cachedPriority.value
        val filter: Filter? = cachedFilter.value

        _toDoListLiveData.postValue(RxResult.loading(null))

        val observable: Maybe<List<Task>> = if (priority == null) {
            when (filter) {
                Filter.ALL -> {
                    taskRepository.getAll()
                }
                Filter.ARCHIVED -> {
                    taskRepository.getAllArchived()
                }
                Filter.COMPLETED -> {
                    taskRepository.getAllCompleted()
                }
                else -> taskRepository.getAll()
            }
        } else {
            when (filter) {
                Filter.ALL -> {
                    taskRepository.getAllByPriority(priority)
                }
                Filter.ARCHIVED -> {
                    taskRepository.getAllArchivedByPriority(priority)
                }
                Filter.COMPLETED -> {
                    taskRepository.getAllCompletedByPriority(priority)
                }
                else -> taskRepository.getAllByPriority(priority)
            }
        }

        observable.subscribe(
            { taskList: List<Task> ->
                _toDoListLiveData.postValue(RxResult.success(taskList))
            }, { error ->
                _toDoListLiveData.postValue(RxResult.error("", null))
                error { error }
            }
        ).addToCompositeDisposable()

    }

    fun getCategories() {
        _categoryLiveData.postValue(RxResult.loading(null))
        categoryRepository
            .getAll()
            .subscribe(
                { categories ->
                    _categoryLiveData.postValue(RxResult.success(categories))
                },
                { error ->
                    _categoryLiveData.postValue(RxResult.error(error.message!!, null))
                    error { error }
                }
            ).addToCompositeDisposable()
    }

    fun addTask(category: Category, taskDescription: String, priority: Priority) {
        Single
            .just(
                Task(
                    category = category,
                    description = taskDescription,
                    priority = priority,
                    date = Date()
                )
            )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { task: Task ->
                    taskRepository.add(task)
                },
                { error ->
                    error { error }
                })
            .addToCompositeDisposable()
    }

    fun deleteTask(id: Long) {
        Single
            .just(id)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    taskRepository.delete(it)
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun archiveTask(id: Long) {
        Single
            .just(id)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    taskRepository.archive(id)
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun addCategory(text: CharSequence) {
        Single.just(text)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { categoryName, err ->
                categoryRepository.add(Category(categoryName.toString()))
                error { err }
            }
            .addToCompositeDisposable()
    }
}