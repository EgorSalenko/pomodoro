package io.esalenko.pomadoro.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.ui.MainActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CountdownService : Service() {

    private val binder = CountdownBinder()

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var callback: CountdownCommunicationCallback

    private var timerResult: Long = 0

    private lateinit var notificationBuilder: NotificationCompat.Builder

    inner class CountdownBinder : Binder() {
        val countdownService: CountdownService
            get() = this@CountdownService
    }

    companion object {

        private const val CHANNEL_ID = "countdown_service_notification_channel_id"
        private const val CHANNEL_NAME = "countdown_service_notification_channel_name"
        private const val NOTIFICATION_ID = 5002

        private const val REQUEST_CODE = 0

        @JvmStatic
        fun Context.createCountdownServiceIntent(): Intent {
            return Intent(this@createCountdownServiceIntent, CountdownService::class.java)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    @SuppressLint("CheckResult")
    fun startTimer(timerDuration: Long) {

        val notification: Notification? = setupForegroundNotification()
        val notificationManager: NotificationManager = getSystemService(NotificationManager::class.java)

        startForeground(NOTIFICATION_ID, notification)

        addDisposable(Observable
            .interval(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { timer: Long ->
                timerResult = timerDuration - (timer * 1000)
                callback.onTimerStatusChanged(true)
            }
            .takeUntil { timer: Long ->
                timer * 1000 == timerDuration
            }
            .doOnComplete {
                callback.onTimerStatusChanged(false)
                stopSelf()
            }
            .switchMap {
                return@switchMap Observable.create<Long> { emitter ->
                    emitter.onNext(timerResult)
                }
            }
            .subscribe({ tick ->

                val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(tick)
                val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(tick) % 60

                val time: String = String.format("%02d:%02d", minutes, seconds)

                callback.provideTimer(time)

                notificationBuilder.setContentText("Timer remaining : $time")

                notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

            }, {
                it.printStackTrace()
            })
        )

    }

    private fun setupForegroundNotification(): Notification? {

        notificationBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            NotificationCompat.Builder(applicationContext, mChannel.id)
        } else {
            NotificationCompat.Builder(applicationContext)
        }

        return notificationBuilder
            .setAutoCancel(false)
            .setChannelId(CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText("")
            .setContentTitle("Session in progress")
            .setContentIntent(PendingIntent.getActivity(
                applicationContext,
                REQUEST_CODE,
                Intent(this, MainActivity::class.java),
                0
            ))
            .build()
    }

    private fun addDisposable(disposable: Disposable?) {
        compositeDisposable.add(disposable!!)
    }

    fun stopTimer() {
        callback.onTimerStatusChanged(false)
        compositeDisposable.clear()
        stopSelf()
    }

    fun setCountdownCommunicationCallback(callback: CountdownCommunicationCallback) {
        this.callback = callback
    }

    interface CountdownCommunicationCallback {
        fun provideTimer(timer: String)

        fun onTimerStatusChanged(isTimerProcessing: Boolean)
    }
}