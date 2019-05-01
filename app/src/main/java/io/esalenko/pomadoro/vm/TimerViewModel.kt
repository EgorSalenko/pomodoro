package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.repository.TaskRepository
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.vm.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.error


class TimerViewModel(
    private val taskRepository: TaskRepository,
    private val sharedPreferenceManager: SharedPreferenceManager
) : BaseViewModel() {

    private val _taskLiveData = MutableLiveData<RxResult<Task>>()
    val taskLiveData: LiveData<RxResult<Task>>
        get() = _taskLiveData

    fun getTask(taskId: Long) {
        _taskLiveData.postValue(RxResult.loading(null))
        taskRepository
            .get(taskId)
            .subscribe(
                { task: Task ->
                    _taskLiveData.postValue(RxResult.success(task))
                },
                { error ->
                    _taskLiveData.postValue(RxResult.error(error.localizedMessage, null))
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun completeTask(taskId: Long) {
        taskRepository
            .get(taskId)
            .map { task ->
                task.apply {
                    isCompleted = true
                }
            }
            .subscribe(
                { task ->
                    taskRepository.add(task)
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun restoreCompletedTask(taskId: Long) {
        taskRepository
            .get(taskId)
            .map { task ->
                task.apply {
                    isCompleted = false
                }
            }
            .subscribe(
                { task ->
                    taskRepository.add(task)
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun removeTask(taskId: Long) {
        Single
            .just(taskId)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe(
                { id ->
                    taskRepository.delete(id)
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun saveLastStartedTaskId(itemId: Long) {
        sharedPreferenceManager.lastStartedTaskId = itemId
    }

    fun getSession(itemId: Long): LiveData<Int> {
        return taskRepository
            .getSessions(itemId)
    }

    fun getTaskCooldown(taskId: Long): LiveData<Boolean> {
        return taskRepository
            .getTaskCooldown(taskId)
    }

    fun isLastStartedTask(itemId: Long) =
        itemId == sharedPreferenceManager.lastStartedTaskId
                || sharedPreferenceManager.lastStartedTaskId == -1L

    fun setTaskInProgress(taskId: Long) {
        taskRepository
            .get(taskId)
            .map { task ->
                task.apply {
                    isRunning = true
                }
            }
            .subscribe(
                { task ->
                    taskRepository.add(task)
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()

    }


}