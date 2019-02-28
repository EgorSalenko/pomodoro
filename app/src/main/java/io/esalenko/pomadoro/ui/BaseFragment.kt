package io.esalenko.pomadoro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.esalenko.pomadoro.di.Injectable
import javax.inject.Inject
import kotlin.reflect.KClass


abstract class BaseFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @get:LayoutRes
    protected abstract val layoutRes : Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layoutRes, container, false)
        return view
    }

    protected fun <T : ViewModel> Fragment.getViewModel(klass: KClass<T>): T {
        return ViewModelProviders.of(this@getViewModel, viewModelFactory)[klass.java]
    }

    protected fun <T : ViewModel> FragmentActivity.getSharedViewModel(klass: KClass<T>): T {
        return ViewModelProviders.of(this@getSharedViewModel)[klass.java]
    }
}