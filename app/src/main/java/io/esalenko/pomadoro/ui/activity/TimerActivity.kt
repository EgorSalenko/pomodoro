package io.esalenko.pomadoro.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.service.CountdownService
import io.esalenko.pomadoro.service.CountdownService.Companion.createCountdownServiceIntent
import io.esalenko.pomadoro.ui.common.BaseActivity


class TimerActivity : BaseActivity(), CountdownService.CountdownCommunicationCallback {

    override val layoutRes: Int
        get() = R.layout.activity_timer

    private var isBound: Boolean = false

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