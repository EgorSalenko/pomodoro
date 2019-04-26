package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.domain.model.FilterType
import io.esalenko.pomadoro.vm.common.BaseViewModel
import io.esalenko.pomadoro.vm.common.Event


class SharedViewModel : BaseViewModel() {

    private val _mainScreenLiveData = MutableLiveData<Event<String>>()
    val mainScreenLiveData: LiveData<Event<String>>
        get() = _mainScreenLiveData

    private val _filterLiveData = MutableLiveData<Event<FilterType>>()
    val filterLiveData: LiveData<Event<FilterType>>
        get() = _filterLiveData

    private val _errorLiveData = MutableLiveData<Event<String>>()
    val errorLiveData: LiveData<Event<String>>
        get() = _errorLiveData

    private val _errorRetryLiveData = MutableLiveData<Event<Any>>()
    val errorRetryLiveData: LiveData<Event<Any>>
        get() = _errorRetryLiveData

    private val _timerLiveData = MutableLiveData<String>()
    val timerLiveData: LiveData<String>
        get() = _timerLiveData

    private val _timerStateLiveData = MutableLiveData<TimerState>()
    val timerStateLiveData: LiveData<TimerState>
        get() = _timerStateLiveData

    private val _timerEventLiveData = MutableLiveData<Event<CountdownEvent>>()
    val timerEventLiveData: LiveData<Event<CountdownEvent>>
        get() = _timerEventLiveData

    fun openMainScreen() {
        _mainScreenLiveData.value = Event("Saved")
    }

    fun setFilter(filterType: FilterType) {
        _filterLiveData.value = Event(filterType)
    }

    fun showError(msg: String? = null) {
        _errorLiveData.value = Event(msg ?: "Error occurred")
    }

    fun onErrorRetry() {
        _errorRetryLiveData.value = Event(Any())
    }

    fun setTime(time: String) {
        _timerLiveData.value = time
    }

    fun updateState(state: TimerState) {
        _timerStateLiveData.value = state
    }

    fun startTimer() {
        _timerEventLiveData.value = Event(CountdownEvent.START)
    }

    fun stopTimer() {
        _timerEventLiveData.value = Event(CountdownEvent.STOP)
    }

    enum class TimerState {
        WORKING,
        FINISHED,
        STOPPED
    }

    enum class CountdownEvent {
        START,
        STOP
    }

}