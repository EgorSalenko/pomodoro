package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.db.model.TimerState
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.repository.TaskRepository
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.vm.common.BaseViewModel
import io.esalenko.pomadoro.vm.common.Event
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

    private val _timerLiveData = MutableLiveData<String>()
    val timerLiveData: LiveData<String>
        get() = _timerLiveData

    private val _timerStateLiveData = MutableLiveData<TimerState>()
    val timerStateLiveData: LiveData<TimerState>
        get() = _timerStateLiveData

    private val _timerActionLiveData = MutableLiveData<Event<TimerAction>>()
    val timerActionLiveData: LiveData<Event<TimerAction>>
        get() = _timerActionLiveData

    private val _shareTaskLiveData = MutableLiveData<Task>()
    val shareTaskLiveData: LiveData<Task>
        get() = _shareTaskLiveData

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
                    isArchived = false
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

    fun getSession(itemId: Long): LiveData<Int> = taskRepository.getSessions(itemId)

    fun getTaskCooldown(taskId: Long): LiveData<Boolean> = taskRepository.getTaskCooldown(taskId)

    fun isLastStartedTask(itemId: Long) =
        itemId == sharedPreferenceManager.lastStartedTaskId || sharedPreferenceManager.lastStartedTaskId == -1L

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

    fun provideTimer(timer: String) {
        _timerLiveData.value = timer
    }

    fun provideState(state: TimerState) {
        _timerStateLiveData.value = state
    }

    fun setTimerAction(action: TimerAction) {
        _timerActionLiveData.value = Event(action)
    }

    fun shareTaskInfo(task: Task) {
        _shareTaskLiveData.value = task
    }

    enum class TimerAction {
        START,
        STOP
    }
}