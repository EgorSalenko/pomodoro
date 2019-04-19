package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.repository.TaskRepository
import io.esalenko.pomadoro.vm.common.BaseViewModel


class ToDoListVIewModel(private val taskRepository: TaskRepository) : BaseViewModel() {

    private val _toDoListLiveData = MutableLiveData<List<Task>>()
    val toDoListLiveData: LiveData<List<Task>>
        get() = _toDoListLiveData

    init {
        fetchToDoList()
    }

    private fun fetchToDoList() {
        taskRepository
            .getAll()
            .subscribe { taskList: List<Task> ->
                _toDoListLiveData.value = taskList
            }
            .addToCompositeDisposable()
    }

}