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

    fun increaseSession(itemId: Long) {
        taskRepository
            .get(itemId)
            .map { task: Task ->
                task.pomidors += 1
                task
            }
            .subscribe(
                { task: Task ->
                    taskRepository.add(task)
                    // TODO :: Update pomidors counter
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun setTaskInProgress(taskId: Long, inProgress: Boolean) {
        taskRepository
            .get(taskId)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { task ->
                task.isInProgress = inProgress
                task
            }
            .subscribe(
                { task ->
                    taskRepository.add(task)
                    // TODO :: Update 'Task in progress' view
                },
                { error ->
                    error { error }
                }
            )
            .addToCompositeDisposable()
    }

    fun setTaskOnPause(taskId: Long, isPause: Boolean) {
        taskRepository
            .get(taskId)
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .map { task ->
                task.isPaused = isPause
                task
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

    fun isLastStartedTask(itemId: Long) =
        itemId == sharedPreferenceManager.lastStartedTaskId && sharedPreferenceManager.lastStartedTaskId != -1L

}