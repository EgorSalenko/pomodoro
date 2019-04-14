package io.esalenko.pomadoro.ui

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.service.CountdownService
import io.esalenko.pomadoro.service.CountdownService.Companion.createCountdownServiceIntent
import io.esalenko.pomadoro.ui.SettingsActivity.Companion.createSettingsActivityIntent
import io.esalenko.pomadoro.ui.fragment.TaskFragment
import io.esalenko.pomadoro.ui.fragment.WorkTimerFragment
import io.esalenko.pomadoro.vm.SharedCountdownViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), CountdownService.CountdownCommunicationCallback {

    override fun isOnPause(isPause: Boolean) {
        this.isPause = isPause
    }

    private var isBound: Boolean = false
    private var isRunning: Boolean = false

    private var isPause: Boolean = false

    private var countdownService: CountdownService? = null
    private lateinit var viewModel: SharedCountdownViewModel

    private val serviceConnection: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val countdownBinder: CountdownService.CountdownBinder = service as CountdownService.CountdownBinder
            countdownService = countdownBinder.countdownService
            countdownService?.setCountdownCommunicationCallback(this@MainActivity)
            updateView(isRunning)
            isBound = true
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

        viewModel = getSharedViewModel(SharedCountdownViewModel::class)

        if (savedInstanceState == null) {
            if (isRunning) {
                showWorkTimerFragment()
            } else {
                showTaskFragment()
            }
        }

        bottomAppBar.apply {
            replaceMenu(R.menu.bottom_app_bar_menu)
            setOnMenuItemClickListener { item ->
                onMenuItemClicked(item)
            }
        }

        timerButton.setOnClickListener {
            if (isRunning) {
                stopCountdown()
                showTaskFragment()
            } else {
                startCountdown()
                showWorkTimerFragment()
            }
        }
    }

    private fun openProfile() {

    }

    private fun openSettings() {
        startActivity(createSettingsActivityIntent())
    }

    private fun startCountdown() {
        // TODO :: Invoke Task View Model and persist task description into DB
//        viewModel.setTaskDescription(et_main_activity.text.toString())
        countdownService?.startTimer()
    }

    private fun stopCountdown() {
        countdownService?.stopTimer()
    }

    private fun updateView(isRunning : Boolean) {

        if (isPause) {
            timerButton.setImageResource(if (isRunning) R.drawable.ic_round_stop_24px else R.drawable.ic_round_local_cafe_24px)
            viewModel.updateStatus("Take some rest")
        } else {
            timerButton.setImageResource(if (isRunning) R.drawable.ic_round_stop_24px else R.drawable.ic_round_timer_24px)
            viewModel.updateStatus("You must concentrate on your work now")
        }
/*

        if (isRunning) {
            showWorkTimerFragment()
        } else {
            showTaskFragment()
        }
*/

    }

    private fun showWorkTimerFragment() {
        WorkTimerFragment().replace(R.id.fragmentContainer, WorkTimerFragment.TAG)
    }

    private fun showTaskFragment() {
        TaskFragment().replace(R.id.fragmentContainer, TaskFragment.TAG)
    }

    override fun provideTimer(timer: String) {
        viewModel.updateTimer(timer)
    }

    override fun onTimerStatusChanged(isTimerProcessing: Boolean) {
        isRunning = isTimerProcessing
        updateView(isRunning)
    }

    override fun onSessionCounterUpdate(sessions: Int) {
        viewModel.updateSessions(sessions)
        updateView(isRunning)
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
