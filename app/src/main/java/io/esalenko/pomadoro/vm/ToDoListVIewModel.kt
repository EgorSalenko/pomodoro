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
            }, {
                _toDoListLiveData.postValue(RxResult.error("ToDo List::error occurred", null))
                error { it }
            })
            .addToCompositeDisposable()
    }

    fun addTask(type: String, taskDescription: String, priority: Int) {
        Single
            .just(
                Task(
                    type = type,
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

}