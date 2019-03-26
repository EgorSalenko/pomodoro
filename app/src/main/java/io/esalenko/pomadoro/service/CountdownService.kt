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
import dagger.android.AndroidInjection
import io.esalenko.pomadoro.manager.LocalAlarmManager
import io.esalenko.pomadoro.manager.LocalNotificationManager
import io.esalenko.pomadoro.manager.SharedPreferenceManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CountdownService : Service() {

    @Inject
    lateinit var sharedPreferenceManager: SharedPreferenceManager

    @Inject
    lateinit var localNotificationManager: LocalNotificationManager

    private var timerResult: Long = 0

    private val binder = CountdownBinder()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var callback: CountdownCommunicationCallback
    private lateinit var notificationBuilder: NotificationCompat.Builder

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
        AndroidInjection.inject(this)
        super.onCreate()
        this.notificationBuilder = localNotificationManager.notificationBuilder
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    @SuppressLint("CheckResult")
    fun startTimer() {

        val isPause = sharedPreferenceManager.isPause
        // TODO :: Extract strings into resources
        val notification: Notification? =
            localNotificationManager.createNotification(this, "Session in progress", "Working session", REQUEST_CODE)

        val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)

        startForeground(NOTIFICATION_ID, notification)

        addDisposable(Observable
            .interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { timer: Long ->
                timerResult = if (isPause) {
                    sharedPreferenceManager.cooldownDuration - (timer * 1000)
                } else {
                    sharedPreferenceManager.timerDuration - (timer * 1000)
                }
                callback.isOnPause(isPause)
                callback.onTimerStatusChanged(true)
            }
            .takeUntil { timer: Long ->
                timer * 1000 == if (isPause) {
                    sharedPreferenceManager.cooldownDuration
                } else {
                    sharedPreferenceManager.timerDuration
                }
            }
            .doOnComplete {
                callback.isOnPause(isPause)
                callback.onTimerStatusChanged(false)
                if (!isPause) {
                    sharedPreferenceManager.incrementSessionCounter()
                }
                callback.onSessionCounterUpdate(
                    sharedPreferenceManager.sessionCounter
                )
                LocalAlarmManager.startAlarm(this)
                stopForeground(true)
                stopSelf()
            }
            .switchMap {
                return@switchMap Observable.create<Long> { emitter ->
                    emitter.onNext(timerResult)
                }
            }
            .subscribe({ tick: Long ->

                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(tick)
                val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(tick) % 60

                val time: String = String.format("%02d:%02d", minutes, seconds)

                callback.provideTimer(time)

                notificationBuilder
                    .setSound(null)
                    .setContentText("Timer remaining : $time")

                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

            }, { throwable: Throwable ->
                throwable.printStackTrace()
            })
        )

    }

    private fun addDisposable(disposable: Disposable?) {
        compositeDisposable.add(disposable!!)
    }

    fun stopTimer() {
        val isPause: Boolean = sharedPreferenceManager.isPause
        sharedPreferenceManager.isPause = !isPause
        callback.isOnPause(sharedPreferenceManager.isPause)
        callback.onTimerStatusChanged(false)
        compositeDisposable.clear()
        stopSelf()
    }

    fun setCountdownCommunicationCallback(callback: CountdownCommunicationCallback) {
        this.callback = callback
    }

    interface CountdownCommunicationCallback {
        fun provideTimer(timer: String)

        fun isOnPause(isPause: Boolean)

        fun onTimerStatusChanged(isTimerProcessing: Boolean)

        fun onSessionCounterUpdate(sessions: Int)
    }
}