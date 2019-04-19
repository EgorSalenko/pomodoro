package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent


class SharedCountdownViewModel : ViewModel(), KoinComponent {

    companion object {
        const val TAG = "SharedCountdownViewModel"
    }

    private val _timerLiveData = MutableLiveData<String>()
    val timerLiveData: LiveData<String>
        get() = _timerLiveData

    private val _sessionMessageLiveData = MutableLiveData<String>()
    val sessionMessageLiveData: LiveData<String>
        get() = _sessionMessageLiveData

    private val _timerStatus = MutableLiveData<Boolean>()
    val timerStatus: LiveData<Boolean>
        get() = _timerStatus

    private val _newTaskLiveData = MutableLiveData<Boolean>()
    val newTaskLiveData: LiveData<Boolean>
        get() = _newTaskLiveData

    fun updateTimerStatus(isRunning: Boolean) {
        _timerStatus.value = isRunning
    }

    fun updateTimer(timer: String) {
        _timerLiveData.value = timer
    }

    fun updateSessionMessage(status: String) {
        _sessionMessageLiveData.value = status
    }

    fun showAddTaskFragment(isVisible: Boolean) {
        _newTaskLiveData.value = isVisible
    }
}