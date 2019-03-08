package io.esalenko.pomadoro.ui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.esalenko.pomadoro.service.CountdownService
import io.esalenko.pomadoro.service.CountdownService.Companion.createCountdownServiceIntent
import io.esalenko.pomadoro.ui.SettingsActivity.Companion.createSettingsActivityIntent
import io.esalenko.pomadoro.vm.CountdownViewModel
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity(), CountdownService.CountdownCommunicationCallback {


    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    private var isBound: Boolean = false

    private var isRunning: Boolean = false

    private var countdownService: CountdownService? = null
    private lateinit var viewModel: CountdownViewModel

    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val countdownBinder: CountdownService.CountdownBinder = service as CountdownService.CountdownBinder
            countdownService = countdownBinder.countdownService
            isBound = true
            countdownService?.setCountdownCommunicationCallback(this@MainActivity)
            updateView(isRunning)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }

    }

    override val layoutRes: Int
        get() = R.layout.activity_main

    override fun onStart() {
        super.onStart()
        startService(createCountdownServiceIntent())
        bindService(createCountdownServiceIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(CountdownViewModel::class)

        bab_activity_main.replaceMenu(R.menu.bottom_app_bar_menu)

        bab_activity_main.setOnMenuItemClickListener { item ->
            onMenuItemClicked(item)
        }

        fab_activity_main.setOnClickListener {
            if (isRunning) {
                stopCountdown()
            } else {
                startCountdown()
            }
        }
    }

    private fun openProfile() {

    }

    private fun openSettings() {
        startActivity(createSettingsActivityIntent())
    }

    private fun startCountdown() {
        viewModel.setTaskDescription(et_main_activity.text.toString())
        countdownService?.startTimer(sharedPreferenceManager.timerDuration)
    }

    private fun stopCountdown() {
        countdownService?.stopTimer()
    }

    private fun updateView(isRunning: Boolean = false) {
        fab_activity_main.setImageResource(if (isRunning) R.drawable.ic_round_stop_24px else R.drawable.ic_round_timer_24px)

        tv_main_activity_timer.visibility = if (isRunning) {
            View.VISIBLE
        } else {
            View.GONE
        }

        et_main_activity.visibility = if (isRunning) {
            View.GONE
        } else {
            View.VISIBLE
        }

    }

    override fun provideTimer(timer: String) {
        tv_main_activity_timer.text = timer
    }

    override fun onTimerStatusChanged(isTimerProcessing: Boolean) {
        isRunning = isTimerProcessing
        updateView(isTimerProcessing)
    }

    private fun onMenuItemClicked(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_settings -> {
                openSettings()
                true
            }
            R.id.menu_profile -> {
                openProfile()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

}
