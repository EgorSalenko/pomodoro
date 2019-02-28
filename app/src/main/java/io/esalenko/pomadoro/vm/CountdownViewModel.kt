package io.esalenko.pomadoro.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import io.esalenko.pomadoro.domain.model.Task
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.repository.TaskRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CountdownViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val spm: SharedPreferenceManager

) : ViewModel() {

    private val _updateCountdownLiveData = MutableLiveData<Long>()

    val updateCountdownLiveData: LiveData<Long>
        get() = _updateCountdownLiveData

    private val taskDescription = MutableLiveData<String?>()

    private val _taskDescriptionLiveData = MutableLiveData<String>()

    val taskDescriptionLiveData = Transformations.switchMap(taskDescription){task: String? ->
        return@switchMap if (task.isNullOrEmpty()){
            AbsentLiveData.create<String>()
        } else {
            _taskDescriptionLiveData.value = task
            _taskDescriptionLiveData
        }
    }

    private var countdownDisposable: Disposable? = null

    fun setTaskDescription(description: String?) {
        if (description.isNullOrEmpty()) return
        taskDescription.value = description
    }

    init {

    }

    fun startTimer() {
        countdownDisposable = Observable
            .interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { timer: Long ->
                val result = spm.timerDuration - (timer * 1000)
                _updateCountdownLiveData.value = result
            }
            .takeUntil { timer: Long ->
                timer * 1000 == spm.timerDuration
            }
            .doOnComplete {
                stopTimer()
            }
            .doOnError {
                Log.e(TAG, it.message)
                spm.timerTimestamp = _updateCountdownLiveData.value!!
            }
            .doOnDispose {
                spm.timerTimestamp = _updateCountdownLiveData.value!!
            }
            .subscribe()
    }

    fun stopTimer() {
        if (countdownDisposable != null && !countdownDisposable?.isDisposed!!) {
            countdownDisposable?.dispose()
//            saveLastSession()
        }
    }

    private fun saveLastSession() {
        val task = Task(
            taskDescription = taskDescription.value ?: EMPTY_DESCRIPTION,
            duration = _updateCountdownLiveData.value!!,
            date = Date().time
        )
        taskRepository.add(task)
    }


    companion object {
        val TAG = CountdownViewModel.javaClass.simpleName
        private const val EMPTY_DESCRIPTION = "Empty description"
    }


}