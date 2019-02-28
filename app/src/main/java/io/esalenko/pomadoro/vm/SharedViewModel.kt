package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject


class SharedViewModel @Inject constructor(): ViewModel() {

    private val _isActivatedLiveData = MutableLiveData<Boolean>()
    val isActivatedLiveData: LiveData<Boolean>
        get() = _isActivatedLiveData

    init {
        _isActivatedLiveData.value = false
    }

    fun setActivatedState(isActivated: Boolean) {
        _isActivatedLiveData.value = isActivated
    }

}