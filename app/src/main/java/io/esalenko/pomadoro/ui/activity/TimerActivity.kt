package io.esalenko.pomadoro.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.Task
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

    private var itemId: Long = -1L
    private var isBound: Boolean = false


    companion object {
        private const val KEY_ITEM_ID = "key_item_id"
        @JvmStatic
        fun createTimerActivityIntent(ctx: Context, itemId: Long): Intent {
            val intent = Intent(ctx, TimerActivity::class.java)
            intent.putExtra(KEY_ITEM_ID, itemId)
            return intent
        }
    }

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
        timerViewModel.getTask(itemId)
        subscribeUi()
    }

    private fun subscribeUi() {
        timerViewModel.apply {
            taskLiveData.observe(this@TimerActivity, Observer { result: RxResult<Task> ->
                when (result.status) {
                    RxStatus.LOADING -> {
                        loading.visibility = View.VISIBLE
                    }
                    RxStatus.SUCCESS -> {
                        loading.visibility = View.GONE
                        val task = result.data ?: return@Observer

                        pomodidorCount.text = task.pomidors.toString()
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

                    }
                    RxStatus.ERROR -> {
                        loading.visibility = View.GONE
                        Snackbar.make(parentTimer, " 0", Snackbar.LENGTH_LONG)
                            .setAction(R.string.snackbar_action_retry) {
                                timerViewModel.getTask(itemId)
                            }
                            .show()
                    }
                }
            })
        }
    }

    private fun startCountdown() {
        countdownService?.startTimer()
    }

    private fun stopCountdown() {
        countdownService?.stopTimer()
    }


    private fun showTaskFragment() {

    }

    override fun provideTimer(timer: String) {

    }

    override fun onTimerStatusChanged(isTimerProcessing: Boolean) {

    }

    override fun onSessionCounterUpdate(sessions: Int) {

    }

    override fun countdownFinished() {

    }

    override fun isOnPause(isPause: Boolean) {

    }

    override fun onStop() {
        super.onStop()
        unbindService(serviceConnection)
    }

}