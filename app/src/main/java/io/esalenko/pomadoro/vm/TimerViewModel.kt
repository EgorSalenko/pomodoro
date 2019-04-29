package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.repository.TaskRxRepository
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.vm.common.BaseViewModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.error


class TimerViewModel(
    private val taskRepository: TaskRxRepository,
    private val sharedPreferenceManager: SharedPreferenceManager
) : BaseViewModel() {

    private val _taskLiveData = MutableLiveData<RxResult<Task>>()
    val taskLiveData: LiveData<RxResult<Task>>
        get() = _taskLiveData

    private val _sessionsLiveData = MutableLiveData<Int>()
    val sessionsLiveData: LiveData<Int>
        get() = _sessionsLiveData

    fun getTask(itemId: Long) {
        _taskLiveData.postValue(RxResult.loading(null))
        taskRepository
            .get(itemId)
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

    fun completeTask(itemId: Long) {
        taskRepository
            .get(itemId)
            .subscribe(
                { task ->
                    task.isCompleted = true
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun removeTask(itemId: Long) {
        Single
            .just(itemId)
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
        itemId == sharedPreferenceManager.lastStartedTaskId || sharedPreferenceManager.lastStartedTaskId == -1L

    fun setTaskInProgress(taskId: Long) {
        taskRepository
            .get(taskId)
            .map {
                it.apply {
                    isRunning = true
                }
            }
            .subscribe(
                { id ->
                    taskRepository.add(id)
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()

    }


}