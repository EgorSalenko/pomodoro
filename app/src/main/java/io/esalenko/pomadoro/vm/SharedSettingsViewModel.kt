package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.esalenko.pomadoro.vm.common.BaseViewModel
import io.esalenko.pomadoro.vm.common.Event


class SharedSettingsViewModel : BaseViewModel() {

    private val _openCategoriesEvent = MutableLiveData<Event<Any>>()
    val openCategoriesEvent: LiveData<Event<Any>>
        get() = _openCategoriesEvent

    fun openCategories() {
        _openCategoriesEvent.value = Event(Any())
    }

}