package io.esalenko.pomadoro.ui

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


abstract class BaseActivity : AppCompatActivity() {

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

}