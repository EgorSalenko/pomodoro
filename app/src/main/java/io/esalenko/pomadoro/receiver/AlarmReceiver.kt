package io.esalenko.pomadoro.receiver

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.android.AndroidInjection
import io.esalenko.pomadoro.manager.LocalNotificationManager
import javax.inject.Inject


class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var localNotifcationManager: LocalNotificationManager

    companion object {
        private const val REQUEST_CODE = 6003
        private const val NOTIFICATION_ID = 7001
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        val notification: Notification? = localNotifcationManager.createNotification(
            context,
            "Session has been finished",
            "Your session has been finished",
            REQUEST_CODE,
            isAlarm = true
        )

        val notificationManager = context?.getSystemService(NotificationManager::class.java)

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }

}