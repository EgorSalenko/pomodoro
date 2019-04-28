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
import io.esalenko.pomadoro.manager.LocalAlarmManager
import io.esalenko.pomadoro.manager.LocalNotificationManager
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.error
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

class CountdownService : Service(), KoinComponent, AnkoLogger {

    // TODO :: Make variables declaration more robust
    var isRunning: Boolean = false
    var isPause: Boolean = false
    private var timerResult: Long = 0

    private val sharedPreferenceManager: SharedPreferenceManager by inject()
    private val localNotificationManager: LocalNotificationManager by inject()

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private lateinit var callback: CountdownCommunicationCallback

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
    fun startTimer() {

        callback.onTaskInProgress(true)

        val timerDuration: Long = if (isPause) {
            sharedPreferenceManager.cooldownDuration
        } else {
            sharedPreferenceManager.timerDuration
        }

        val notification: Notification? =
            localNotificationManager.createNotification(
                this,
                getString(R.string.session_in_progress),
                getString(R.string.working_session),
                REQUEST_CODE
            )

        startForeground(NOTIFICATION_ID, notification)

        addDisposable(Observable
            .interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { timer: Long ->
                timerResult = timerDuration - (timer * 1000)
                callback.onTimerProcessingListener(true)
            }
            .takeUntil { timer: Long ->
                timer * 1000 == timerDuration
            }
            .doOnComplete {

                callback.onTimerProcessingListener(false)

                callback.onTaskIsPaused(!isPause)
                callback.onTaskInProgress(false)

                callback.onCountdownFinished(isPause)

                LocalAlarmManager.startAlarm(this)
                stopForeground(true)
                stopSelf()
            }
            .switchMap {
                Single.just<Long>(timerResult).toObservable()
            }
            .subscribe(
                { tick: Long ->

                    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(tick)
                    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(tick) % 60

                    val time: String = String.format("%02d:%02d", minutes, seconds)

                    callback.provideTimer(time)

                    notificationBuilder
                        .setSound(null)
                        .setContentText(getString(R.string.timer_remaining, time))

                    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

                },
                { error ->
                    error { error }
                }
            )
        )

    }

    private fun addDisposable(disposable: Disposable?) {
        compositeDisposable.add(disposable!!)
    }

    fun stopTimer() {
        notificationManager.cancel(NOTIFICATION_ID)

        callback.onTaskIsPaused(false)
        callback.onTaskInProgress(false)

        callback.onTimerProcessingListener(false)
        compositeDisposable.clear()
        stopSelf()
    }


    fun setCountdownCommunicationCallback(callback: CountdownCommunicationCallback) {
        this.callback = callback
    }

    interface CountdownCommunicationCallback {
        fun provideTimer(timer: String)

        fun onTimerProcessingListener(isTimerProcessing: Boolean)

        fun onCountdownFinished(isPause: Boolean)

        fun onTaskInProgress(inProgress: Boolean)

        fun onTaskIsPaused(isPaused: Boolean)

        fun onTaskSessionUpdate()
    }
}