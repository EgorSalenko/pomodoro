package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.repository.TaskRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*


class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val sharedPreferenceManager: SharedPreferenceManager
) : BaseViewModel() {

    private val _taskLiveData = MutableLiveData<List<Task>>()
    val taskLiveData: LiveData<List<Task>>
        get() = _taskLiveData

    private var cahcedTaskDescription: String = ""

    init {
        fetchTasks()
    }

    fun saveTask() {
        Single.create<Task> { emitter ->
            emitter.onSuccess(
                Task(
                    description = sharedPreferenceManager.cachedTaskDescription,
                duration = sharedPreferenceManager.timerDuration,
                date = Date().time
                )
            )
        }
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { task: Task ->
                taskRepository.add(task)
            }
            .addToCompositeDisposable()
    }

    private fun fetchTasks() {
        taskRepository
            .getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { tasks: List<Task> ->
                _taskLiveData.value = tasks
            }
            .addToCompositeDisposable()
    }

    fun cacheTask(text: String) {
        cahcedTaskDescription = text
    }

}