package io.esalenko.pomadoro.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.service.CountdownService
import io.esalenko.pomadoro.service.CountdownService.Companion.createCountdownServiceIntent
import io.esalenko.pomadoro.ui.common.BaseActivity
import io.esalenko.pomadoro.ui.fragment.TaskFragment


class TimerActivity : BaseActivity(), CountdownService.CountdownCommunicationCallback {

    override val layoutRes: Int
        get() = R.layout.activity_timer

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
        if (savedInstanceState == null) {
            TaskFragment.newInstance(itemId).replace(R.id.fragmentContainer, TaskFragment.TAG, addToBackStack = false)
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