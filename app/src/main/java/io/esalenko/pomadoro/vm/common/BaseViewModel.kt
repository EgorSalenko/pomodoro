package io.esalenko.pomadoro.vm.common

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger


abstract class BaseViewModel : ViewModel(), AnkoLogger {

    private val compositeDisposable = CompositeDisposable()

    protected fun Disposable.addToCompositeDisposable() {
        compositeDisposable.add(this)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}