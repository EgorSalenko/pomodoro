package io.esalenko.pomadoro.vm

import androidx.lifecycle.LiveData


class AbsentLiveData<T> private constructor() : LiveData<T>() {

    init {
        value = null
    }

    companion object {
        fun <T> create() : LiveData<T> {
            return AbsentLiveData()
        }
    }
}