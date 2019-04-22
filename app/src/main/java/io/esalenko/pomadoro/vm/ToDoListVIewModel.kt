package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.domain.model.Task
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
            .subscribe({ taskList: List<Task> ->
                _toDoListLiveData.postValue(RxResult.success(taskList))
                info { taskList }
            }, {
                _toDoListLiveData.postValue(RxResult.error("ToDo List::error occurred", null))
                error { it }
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
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun addTask(category: Int, taskDescription: String, priority: Int) {
        Single
            .just(
                Task(
                    category = category,
                    description = taskDescription,
                    priority = priority,
                    date = Date().time
                )
            )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({ task: Task ->
                taskRepository.add(task)
            }, { error ->
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
        Single.just(id)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                {
                    taskRepository.archive(id)
                },
                { error ->
                    error { error }
                }
            ).addToCompositeDisposable()
    }

}