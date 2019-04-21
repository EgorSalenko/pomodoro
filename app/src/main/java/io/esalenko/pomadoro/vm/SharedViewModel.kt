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

    fun openMainScreen() {
        _mainScreenLiveData.value = Event("Saved")
    }

    fun setFilter(filterType: FilterType) {
        _filterLiveData.value = Event(filterType)
    }

}