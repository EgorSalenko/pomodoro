package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject


class SharedCountdownViewModel @Inject constructor() : ViewModel() {

    private val _timerLiveData = MutableLiveData<String>()
    val timerLiveData: LiveData<String>
        get() = _timerLiveData

    private val _statusLiveData = MutableLiveData<String>()
    val statusLiveData: LiveData<String>
        get() = _statusLiveData

    private val _sessionsLiveData = MutableLiveData<Int>()
    val sessionsLiveData: LiveData<Int>
        get() = _sessionsLiveData

    companion object {
        const val TAG = "SharedCountdownViewModel"
    }

    fun updateTimer(timer: String) {
        _timerLiveData.value = timer
    }

    fun updateStatus(status: String) {
        _statusLiveData.value = status
    }

    fun updateSessions(session: Int) {
        _sessionsLiveData.value = session
    }
}