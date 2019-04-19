package io.esalenko.pomadoro.ui

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import com.afollestad.materialdialogs.MaterialDialog
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.service.CountdownService
import io.esalenko.pomadoro.service.CountdownService.Companion.createCountdownServiceIntent
import io.esalenko.pomadoro.ui.SettingsActivity.Companion.createSettingsActivityIntent
import io.esalenko.pomadoro.ui.fragment.TaskFragment
import io.esalenko.pomadoro.vm.SharedCountdownViewModel
import io.esalenko.pomadoro.vm.TaskViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : BaseActivity(), CountdownService.CountdownCommunicationCallback {

    private var isRunning: Boolean = false
    private var isPause: Boolean = false
    private var isBound: Boolean = false

    private var countdownService: CountdownService? = null
    private lateinit var sharedCountdownViewModel: SharedCountdownViewModel
    private lateinit var taskViewModel: TaskViewModel

    override val layoutRes: Int
        get() = R.layout.activity_main

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

    override fun onStart() {
        super.onStart()
        startService(createCountdownServiceIntent())
        bindService(createCountdownServiceIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedCountdownViewModel = getViewModel()
        taskViewModel = getViewModel()

        if (savedInstanceState == null) {
            showTaskFragment()
        }

        bottomAppBar.apply {
            replaceMenu(R.menu.bottom_app_bar_menu)
            setOnMenuItemClickListener { item ->
                onMenuItemClicked(item)
            }
        }

        sharedCountdownViewModel.updateTimerStatus(isRunning)

        addTaskButton.setOnClickListener {
            sharedCountdownViewModel.showAddTaskFragment(!isRunning)
        }
    }

    private fun showTimerDialog() {
        MaterialDialog(this).show {
            title(R.string.dialog_title)
            message(R.string.dialog_message)
            negativeButton(android.R.string.no) { dialog ->
                stopCountdown()
                showTaskFragment()
            }
            positiveButton(android.R.string.yes) { dialog ->
                stopCountdown()
                showTaskFragment()
            }
        }
    }

    private fun openProfile() {

    }

    private fun openSettings() {
        startActivity(createSettingsActivityIntent())
    }

    private fun startCountdown() {
        countdownService?.startTimer()
    }

    private fun stopCountdown() {
        countdownService?.stopTimer()
    }

    private fun updateView(isRunning: Boolean) {
        if (isPause) {
            addTaskButton.setImageResource(if (isRunning) R.drawable.ic_round_stop_24px else R.drawable.ic_round_local_cafe_24px)
            sharedCountdownViewModel.updateSessionMessage("Take some rest")
        } else {
            addTaskButton.setImageResource(if (isRunning) R.drawable.ic_round_stop_24px else R.drawable.ic_round_timer_24px)
            sharedCountdownViewModel.updateSessionMessage("You must concentrate on your work now")
        }
    }


    private fun showTaskFragment() {
        TaskFragment().replace(R.id.fragmentContainer, TaskFragment.TAG)
    }

    override fun provideTimer(timer: String) {
        sharedCountdownViewModel.updateTimer(timer)
    }

    override fun onTimerStatusChanged(isTimerProcessing: Boolean) {
        isRunning = isTimerProcessing
        updateView(isRunning)
    }

    override fun onSessionCounterUpdate(sessions: Int) {
//        sharedCountdownViewModel.updateSessions(sessions)
        updateView(isRunning)
    }

    override fun countdownFinished() {
        showTaskFragment()
        showTimerDialog()
    }

    override fun isOnPause(isPause: Boolean) {
        this.isPause = isPause
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
