package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.db.model.FilterType
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
}