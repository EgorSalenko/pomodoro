package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.repository.TaskRepository
import java.util.*


class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val sharedPreferenceManager: SharedPreferenceManager
) : BaseViewModel() {

    private val _taskLiveData = MutableLiveData<List<Task>>()
    val taskLiveData: LiveData<List<Task>>
        get() = _taskLiveData

    init {
        fetchTasks()
    }

    fun saveTask(description: String) {
        taskRepository.add(
            Task(
                taskDescription = description,
                duration = sharedPreferenceManager.timerDuration,
                date = Date().time
            )
        )
    }

    private fun fetchTasks() {
        taskRepository
            .getAll()
            .subscribe { tasks: List<Task> ->
                _taskLiveData.value = tasks
            }
            .addToCompositeDisposable()
    }

}