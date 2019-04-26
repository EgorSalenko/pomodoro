package io.esalenko.pomadoro.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.service.CountdownService
import io.esalenko.pomadoro.service.CountdownService.Companion.createCountdownServiceIntent
import io.esalenko.pomadoro.ui.common.BaseActivity
import io.esalenko.pomadoro.ui.fragment.TaskFragment
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.vm.SharedViewModel
import io.esalenko.pomadoro.vm.TimerViewModel
import io.esalenko.pomadoro.vm.common.Event
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.activity_timer.bottomAppBar
import org.koin.androidx.viewmodel.ext.android.viewModel


class TimerActivity : BaseActivity(), CountdownService.CountdownCommunicationCallback {

    private var isRunning: Boolean = false
    override val layoutRes: Int
        get() = R.layout.activity_timer

    private var itemId: Long = -1L
    private var isBound: Boolean = false

    private val sharedViewModel: SharedViewModel by viewModel()
    private val timerViewModel: TimerViewModel by viewModel()

    private var countdownService: CountdownService? = null
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerViewModel.getTask(itemId)
            val countdownBinder: CountdownService.CountdownBinder = service as CountdownService.CountdownBinder
            countdownService = countdownBinder.countdownService
            countdownService?.setCountdownCommunicationCallback(this@TimerActivity)
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    companion object {
        private const val KEY_ITEM_ID = "key_item_id"
        @JvmStatic
        fun createTimerActivityIntent(ctx: Context, itemId: Long): Intent {
            val intent = Intent(ctx, TimerActivity::class.java)
            intent.putExtra(KEY_ITEM_ID, itemId)
            return intent
        }
    }

    override fun onStart() {
        super.onStart()
        startService(createCountdownServiceIntent())
        bindService(createCountdownServiceIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireNotNull(intent)
        itemId = intent.extras[KEY_ITEM_ID] as Long
        if (itemId == -1L) throw IllegalStateException("ItemId can't be -1")

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        if (savedInstanceState == null) {
            TaskFragment
                .newInstance(itemId)
                .replace(R.id.fragmentContainer, TaskFragment.TAG)
        }
        bottomAppBar.apply {
            replaceMenu(R.menu.bottom_app_bar_menu_task)
            setOnMenuItemClickListener { menuItem: MenuItem ->
                onMenuItemClick(menuItem)
            }
        }
        completeTask.setOnClickListener {
            timerViewModel.completeTask(itemId)
            finish()
        }
        subscribeUi()
    }

    private fun subscribeUi() {
        sharedViewModel.apply {
            timerEventLiveData.observe(
                this@TimerActivity,
                Observer { event: Event<SharedViewModel.CountdownEvent> ->
                    when (event.getContentIfNotHandled()) {
                        SharedViewModel.CountdownEvent.START -> {
                            startCountdown()
                        }
                        SharedViewModel.CountdownEvent.STOP -> {
                            stopCountdown()
                        }
                        null -> {
                            Snackbar
                                .make(coordinatorLayout, "Error occurred while start timer", Snackbar.LENGTH_INDEFINITE)
                                .setAnchorView(completeTask)
                                .show()
                        }
                    }
                })
        }
        timerViewModel.apply {
            taskLiveData.observe(this@TimerActivity, Observer { result ->
                when (result.status) {
                    RxStatus.SUCCESS -> {
                        if (isBound && timerViewModel.isLastStartedTask(itemId)) {
                            countdownService?.task = result.data
                        }
                    }
                    RxStatus.ERROR -> {

                    }
                    RxStatus.LOADING -> {

                    }
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (isRunning) {
                    MaterialDialog(this).show {
                        title(R.string.dialog_title_task_cancel)
                        message(R.string.dialog_message_task_cancel)
                        positiveButton {
                            sharedViewModel.stopTimer()
                            finish()
                            it.dismiss()
                        }
                        negativeButton {
                            it.dismiss()
                        }
                    }
                } else {
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onMenuItemClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.delete_item -> {
                MaterialDialog(this).show {
                    title(R.string.delete_item)
                    message(R.string.delete_item_msg)
                    icon(R.drawable.ic_round_delete_forever_24px)
                    negativeButton {
                        it.dismiss()
                    }
                    positiveButton {
                        timerViewModel.removeTask(itemId)
                        it.dismiss()
                        finish()
                    }
                }
            }
        }
        return true
    }

    private fun startCountdown() {
        countdownService?.startTimer()
    }

    private fun stopCountdown() {
        countdownService?.stopTimer()
    }

    override fun provideTimer(timer: String) {
        sharedViewModel.setTime(timer)
    }

    override fun onTimerProcessingListener(isTimerProcessing: Boolean) {
        isRunning = isTimerProcessing
        sharedViewModel.updateState(if (isTimerProcessing) SharedViewModel.TimerState.WORKING else SharedViewModel.TimerState.STOPPED)
    }

    override fun onCountdownFinished() {
        if (timerViewModel.isLastStartedTask(itemId)) {
            timerViewModel.increaseSession(itemId)
        }
        sharedViewModel.updateState(SharedViewModel.TimerState.FINISHED)
    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

}