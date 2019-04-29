package io.esalenko.pomadoro.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.TimerState
import io.esalenko.pomadoro.manager.LocalAlarmManager
import io.esalenko.pomadoro.manager.LocalNotificationManager
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

class CountdownService : Service(), KoinComponent, AnkoLogger {

    private var timerResult: Long = 0

    private var timerDuration: Long = SharedPreferenceManager.DEFAULT_TIMER_DURATION

    private val localNotificationManager: LocalNotificationManager by inject()

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var callback: CountdownCommunicationCallback? = null

    private val binder = CountdownBinder()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    inner class CountdownBinder : Binder() {
        val countdownService: CountdownService
            get() = this@CountdownService
    }

    companion object {
        private const val NOTIFICATION_ID = 5002
        private const val REQUEST_CODE = 8002

        @JvmStatic
        fun Context.createCountdownServiceIntent(): Intent {
            return Intent(this@createCountdownServiceIntent, CountdownService::class.java)
        }
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NotificationManager::class.java)
        this.notificationBuilder = localNotificationManager.notificationBuilder
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    @SuppressLint("CheckResult")
    fun startTimer(taskId: Long, isCooldown: Boolean) {

        val notification: Notification? =
            localNotificationManager.createNotification(
                this,
                getString(R.string.session_in_progress),
                getString(R.string.working_session),
                REQUEST_CODE
            )

        startForeground(NOTIFICATION_ID, notification)

        timerDuration = if (isCooldown) {
            // TODO :: Add SPM call
            SharedPreferenceManager.DEFAULT_SHORT_COOLDOWN_DURATION
        } else {
            SharedPreferenceManager.DEFAULT_TIMER_DURATION
        }


        Observable
            .interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { timer: Long ->
                callback?.onTimerStateChangeListener(TimerState.WORKING)
                timerResult = timerDuration - (timer * 1000)
            }
            .takeUntil { timer: Long ->
                timer * 1000 == timerDuration
            }
            .doOnComplete {
                callback?.onTimerStateChangeListener(TimerState.FINISHED)
                LocalAlarmManager.startAlarm(this, taskId, isCooldown)
                stopForeground(true)
                stopSelf()
            }
            .switchMap {
                Observable.create<Long> {
                    it.onNext(timerResult)
                }
            }
            .subscribe(
                { tick: Long ->

                    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(tick)
                    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(tick) % 60

                    val time: String = String.format("%02d:%02d", minutes, seconds)

                    callback?.onTimerResult(time)

                    notificationBuilder
                        .setSound(null)
                        .setContentText(getString(R.string.timer_remaining, time))

                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

                },
                { error ->
                    error { error }
                }
            )
            .addDisposable()
    }

    private fun Disposable.addDisposable() {
        compositeDisposable.add(this)
    }

    fun stopTimer() {
        notificationManager.cancel(NOTIFICATION_ID)
        compositeDisposable.clear()
        callback?.onTimerStateChangeListener(TimerState.STOPPED)
        stopForeground(true)
        stopSelf()
    }

    fun setCountdownCommunicationCallback(callback: CountdownCommunicationCallback) {
        this.callback = callback
    }

    interface CountdownCommunicationCallback {
        fun onTimerResult(timer: String)

        fun onTimerStateChangeListener(timerState: TimerState)
    }
}