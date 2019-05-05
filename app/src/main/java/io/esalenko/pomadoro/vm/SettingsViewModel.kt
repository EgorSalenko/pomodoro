package io.esalenko.pomadoro.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.vm.common.BaseViewModel


class SettingsViewModel(private val spm: SharedPreferenceManager) : BaseViewModel() {

    private val workTimer = MutableLiveData<Long>()
    private val _workTimerLiveData = MutableLiveData<Long>()

    val workTimerLiveData = Transformations.switchMap(workTimer) { duration ->
        spm.timerDuration = duration
        updateWorkTimer()
        _workTimerLiveData
    }

    private val pauseTimer = MutableLiveData<Long>()
    private val _pauseTimerLiveData = MutableLiveData<Long>()

    init {
        fetchTimerDuration()
    }

    val pauseTimerLiveData = Transformations.switchMap(pauseTimer) { duration ->
        spm.cooldownDuration = duration
        updatePauseTimer()
        _pauseTimerLiveData
    }

    fun setWorkTimerDuration(duration: Long) {
        workTimer.value = duration
    }

    fun setPauseTimerDuration(duration: Long) {
        pauseTimer.value = duration
    }

    private fun updateWorkTimer() {
        _workTimerLiveData.value = spm.timerDuration
    }

    private fun updatePauseTimer() {
        _pauseTimerLiveData.value = spm.cooldownDuration
    }

    private fun fetchTimerDuration() {
        workTimer.value = spm.timerDuration
        pauseTimer.value = spm.cooldownDuration
    }
}