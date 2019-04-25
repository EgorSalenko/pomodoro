package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.repository.TaskRxRepository
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.vm.common.BaseViewModel
import org.jetbrains.anko.error


class TimerViewModel(private val taskRepository: TaskRxRepository) : BaseViewModel() {

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

}