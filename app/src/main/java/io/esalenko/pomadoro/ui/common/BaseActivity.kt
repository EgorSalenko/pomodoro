package io.esalenko.pomadoro.ui.common

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import org.jetbrains.anko.AnkoLogger
import org.koin.core.KoinComponent


abstract class BaseActivity : AppCompatActivity(), KoinComponent, AnkoLogger {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutRes)
    }

    protected fun Fragment.replace(@IdRes containerId: Int, tag: String?, addToBackStack: Boolean = false) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out,
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .replace(containerId, this@replace, tag)
            .addToBackStack(if (addToBackStack) tag else null)
            .commitAllowingStateLoss()
    }

    protected fun Fragment.add(@IdRes containerId: Int, tag: String?, addToBackStack: Boolean = false) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out,
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .add(containerId, this@add, tag)
            .addToBackStack(if (addToBackStack) tag else null)
            .commitAllowingStateLoss()
    }

    protected fun Fragment.remove() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out,
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .remove(this@remove)
            .commitNowAllowingStateLoss()
    }

    @get:LayoutRes
    protected abstract val layoutRes : Int

}