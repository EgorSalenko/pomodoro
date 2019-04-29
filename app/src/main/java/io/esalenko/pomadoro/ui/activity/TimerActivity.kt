package io.esalenko.pomadoro.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.TimerState
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.service.CountdownService
import io.esalenko.pomadoro.service.CountdownService.Companion.createCountdownServiceIntent
import io.esalenko.pomadoro.ui.common.BaseActivity
import io.esalenko.pomadoro.util.RxResult
import io.esalenko.pomadoro.util.RxStatus
import io.esalenko.pomadoro.util.getPriorityColor
import io.esalenko.pomadoro.util.getPriorityIcon
import io.esalenko.pomadoro.vm.TimerViewModel
import kotlinx.android.synthetic.main.activity_timer.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class TimerActivity : BaseActivity(), CountdownService.CountdownCommunicationCallback {

    override val layoutRes: Int
        get() = R.layout.activity_timer

    private val timerViewModel: TimerViewModel by viewModel()

    private var taskId: Long = -1L
    private var isPause = false
    private var isBound: Boolean = false
    private var currentState: TimerState = TimerState.FINISHED

    private var countdownService: CountdownService? = null

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
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
        if (timerViewModel.isLastStartedTask(taskId)) {
            startService(createCountdownServiceIntent())
            bindService(createCountdownServiceIntent(), serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireNotNull(intent)

        taskId = intent?.extras!![KEY_ITEM_ID] as Long

        if (taskId == -1L) throw IllegalStateException("ItemId can't be -1")

        timerViewModel.getTask(taskId)

        setupToolbar()

        updateTimerView()

        bottomAppBar.apply {
            replaceMenu(R.menu.bottom_app_bar_menu_task)
            setOnMenuItemClickListener { menuItem: MenuItem ->
                onMenuItemClick(menuItem)
            }
        }
        completeTask.setOnClickListener {
            timerViewModel.completeTask(taskId)
            finish()
        }
        timerHandler.setOnClickListener {
            when (currentState) {
                TimerState.WORKING -> {
                    stopCountdown()
                }
                TimerState.FINISHED, TimerState.STOPPED -> {
                    timerViewModel.saveLastStartedTaskId(taskId)
                    timerViewModel.setTaskInProgress(taskId)
                    startCountdown()
                }
            }
        }
        subscribeUi()
    }

    private fun updateTimerView() {
        if (timerViewModel.isLastStartedTask(taskId)) {
            timerContent.visibility = View.VISIBLE
            taskMsg.visibility = View.GONE
        } else {
            timerContent.visibility = View.GONE
            taskMsg.visibility = View.VISIBLE
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun subscribeUi() {
        timerViewModel.apply {
            taskLiveData
                .observe(
                    this@TimerActivity,
                    Observer { result: RxResult<Task> ->
                        when (result.status) {
                            RxStatus.LOADING -> {
                                loading.visibility = View.VISIBLE
                            }
                            RxStatus.SUCCESS -> {
                                loading.visibility = View.GONE

                                val task = result.data ?: return@Observer
                                isPause = task.isCooldown

                                pomodidorCount.text = "x ${task.pomidors}"
                                taskCategory.text = task.category.categoryName

                                taskCategory.setCompoundDrawablesWithIntrinsicBounds(
                                    task.priority.getPriorityIcon(),
                                    0,
                                    0,
                                    0
                                )

                                taskCategory.setTextColor(
                                    ContextCompat.getColor(
                                        this@TimerActivity,
                                        task.priority.getPriorityColor()
                                    )
                                )

                                taskText.text = task.description
                                updateView()
                            }
                            RxStatus.ERROR -> {
                                loading.visibility = View.GONE

                                Snackbar
                                    .make(parentTimer, " 0", Snackbar.LENGTH_LONG)
                                    .setAction(R.string.snackbar_action_retry) {
                                        timerViewModel.getTask(taskId)
                                    }
                                    .show()
                            }
                        }
            })
            getSession(taskId).observe(this@TimerActivity, Observer { session: Int? ->
                pomodidorCount.text = "x $session" ?: return@Observer
            })
            getTaskCooldown(taskId).observe(this@TimerActivity, Observer { isCooldown: Boolean? ->
                isPause = isCooldown ?: return@Observer
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
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
                        countdownService?.stopTimer(taskId)
                        timerViewModel.removeTask(taskId)
                        it.dismiss()
                        finish()
                    }
                }
            }
        }
        return true
    }

    private fun startCountdown() {
        countdownService?.startTimer(taskId, isPause)
    }

    private fun stopCountdown() {
        countdownService?.stopTimer(taskId)
    }

    override fun onTimerResult(timer: String) {
        countdown.text = timer
    }

    override fun onTimerStateChangeListener(timerState: TimerState) {
        currentState = timerState
        updateView()
    }

    override fun onStop() {
        super.onStop()
        if (timerViewModel.isLastStartedTask(taskId)) {
            unbindService(serviceConnection)
        }
    }

    private fun updateView() {
        timerHandler.setImageResource(
            when (currentState) {
                TimerState.WORKING -> {
                    R.drawable.ic_round_stop_24px
                }
                TimerState.FINISHED -> {
                    R.drawable.ic_round_play_circle_outline_24px
                }
                TimerState.STOPPED -> {
                    R.drawable.ic_round_play_circle_outline_24px
                }
            }
        )
    }
}