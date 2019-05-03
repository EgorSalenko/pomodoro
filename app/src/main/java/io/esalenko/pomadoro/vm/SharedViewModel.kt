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

    private val _detailScreenEventLiveData = MutableLiveData<Event<Pair<Long, Boolean>>>()
    val detailScreenEventLiveData: LiveData<Event<Pair<Long, Boolean>>>
        get() = _detailScreenEventLiveData

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

    fun openDetailTaskScreen(id: Long, isCompleted: Boolean) {
        _detailScreenEventLiveData.value = Event(Pair(id, isCompleted))
    }
}