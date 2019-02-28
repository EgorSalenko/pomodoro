package io.esalenko.pomadoro.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import kotlin.reflect.KClass


abstract class BaseActivity : AppCompatActivity(), HasSupportFragmentInjector {

    @Inject
    lateinit var viewModelFactory : ViewModelProvider.Factory

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
    }

    fun Fragment.replace(@IdRes containerId: Int, tag: String?) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out,
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .replace(containerId, this@replace, tag)
            .commitNowAllowingStateLoss()
    }

    @get:LayoutRes
    protected abstract val layoutRes : Int

    protected fun <T : ViewModel> FragmentActivity.getViewModel(klass : KClass<T>) : T {
        return ViewModelProviders.of(this@getViewModel, viewModelFactory)[klass.java]
    }

    protected fun <T : ViewModel> FragmentActivity.getSharedViewModel(klass : KClass<T>) : T {
        return ViewModelProviders.of(this@getSharedViewModel)[klass.java]
    }

}