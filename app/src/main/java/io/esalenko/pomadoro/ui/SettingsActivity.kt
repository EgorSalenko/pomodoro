package io.esalenko.pomadoro.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.esalenko.pomadoro.R


class SettingsActivity : BaseActivity() {

    override val layoutRes: Int
        get() = R.layout.acticity_settings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    companion object {
        val TAG = SettingsActivity.javaClass.simpleName

        @JvmStatic
        fun Context.createSettingsActivityIntent(): Intent {
            return Intent(this, SettingsActivity::class.java)
        }
    }
}