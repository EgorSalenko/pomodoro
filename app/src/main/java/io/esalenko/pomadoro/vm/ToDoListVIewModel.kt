package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.domain.model.TaskCategory
import io.esalenko.pomadoro.domain.model.TaskPriority
import io.esalenko.pomadoro.repository.TaskRepository
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.vm.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.error
import org.jetbrains.anko.info
import java.util.*


class ToDoListVIewModel(private val taskRepository: TaskRepository) : BaseViewModel() {

    private val _toDoListLiveData = MutableLiveData<RxResult<List<Task>>>()
    val toDoListLiveData: LiveData<RxResult<List<Task>>>
        get() = _toDoListLiveData

    fun fetchToDoList() {
        _toDoListLiveData.postValue(RxResult.loading(null))
        taskRepository
            .getAll()
            .subscribe(
                { taskList: List<Task> ->
                    _toDoListLiveData.postValue(RxResult.success(taskList))
                    info { taskList }
                },
                { error ->
                    _toDoListLiveData.postValue(RxResult.error("Something went wrong", null))
                    error { error }
                })
            .addToCompositeDisposable()
    }

    fun getToDoListByPriority() {
        _toDoListLiveData.postValue(RxResult.loading(null))
        taskRepository
            .getAllByPriority()
            .subscribe(
                { listByPriority: List<Task> ->
                    _toDoListLiveData.postValue(RxResult.success(listByPriority))
                },
                { error ->
                    _toDoListLiveData.postValue(RxResult.error("Something went wrong", null))
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun getToDoListArchived() {
        _toDoListLiveData.postValue(RxResult.loading(null))
        taskRepository
            .getAllArchived()
            .subscribe(
                { archivedList: List<Task> ->
                    _toDoListLiveData.postValue(RxResult.success(archivedList))
                },
                { error ->
                    _toDoListLiveData.postValue(RxResult.error("Something went wrong", null))
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun getToDoListLatest() {
        _toDoListLiveData.postValue(RxResult.loading(null))
        taskRepository
            .getAllLatest()
            .subscribe(
                { latest: List<Task> ->
                    _toDoListLiveData.postValue(RxResult.success(latest))
                },
                { error ->
                    _toDoListLiveData.postValue(RxResult.error("Something went wrong", null))
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun addTask(category: TaskCategory, taskDescription: String, priority: TaskPriority) {
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

    fun remove(id: Long) {
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

    fun archive(id: Long) {
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

    fun exchangeItems(oldItemId: Long, newItemId: Long) {
        Single.just(Unit)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    val oldTask: Task = taskRepository.get(oldItemId) //0
                    val newTask: Task = taskRepository.get(newItemId) //2

                    val tempId = oldTask.id // 0
                    oldTask.id = newTask.id // 2
                    taskRepository.add(oldTask)
                    newTask.id = tempId // 0
                    taskRepository.add(newTask)
                },
                { error ->
                    _toDoListLiveData.postValue(RxResult.error("Something went wrong", null))
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

}