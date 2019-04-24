package io.esalenko.pomadoro.ui.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.AnkoLogger
import org.koin.core.KoinComponent


abstract class BaseFragment : Fragment(), KoinComponent, AnkoLogger {

    @get:LayoutRes
    protected abstract val layoutRes: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutRes, container, false)
    }

    protected fun Fragment.add(@IdRes containerId: Int, tag: String? = null) {
        childFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out,
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .add(containerId, this@add, tag)
            .commitNowAllowingStateLoss()
    }

    protected fun Fragment.replace(@IdRes containerId: Int, tag: String? = null) {
        childFragmentManager
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

    protected fun Fragment.remove() {
        childFragmentManager
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

    protected fun Fragment.show() {
        childFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out,
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .show(this@show)
            .commitNowAllowingStateLoss()
    }

    protected fun Fragment.hide() {
        childFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                android.R.animator.fade_in,
                android.R.animator.fade_out,
                android.R.animator.fade_in,
                android.R.animator.fade_out
            )
            .hide(this@hide)
            .commitNowAllowingStateLoss()
    }

    protected fun showSnackBar(v: View, msg: String) {
        Snackbar.make(v, msg, Snackbar.LENGTH_SHORT)
    }

}