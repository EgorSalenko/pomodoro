package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.vm.common.BaseViewModel


class SettingsViewModel(private val sharedPreferenceManager: SharedPreferenceManager) : BaseViewModel() {

    private val _workTimerLiveData = MutableLiveData<Long>()
    val workTimerLiveData: LiveData<Long>
        get() = _workTimerLiveData

    private val _pauseTimerLiveData = MutableLiveData<Long>()
    val pauseTimerLiveData: LiveData<Long>
        get() = _pauseTimerLiveData

    fun setWorkTimerDuration(duration: Long) {
        sharedPreferenceManager.timerDuration = duration
    }

    fun setPauseTimerDuration(duration: Long) {
        sharedPreferenceManager.cooldownDuration = duration
    }

}