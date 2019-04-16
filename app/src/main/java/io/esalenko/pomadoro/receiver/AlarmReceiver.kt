package io.esalenko.pomadoro.receiver

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import io.esalenko.pomadoro.manager.LocalNotificationManager
import org.koin.core.KoinComponent
import org.koin.core.get


class AlarmReceiver : BroadcastReceiver(), KoinComponent {


    companion object {
        private const val REQUEST_CODE = 6003
        private const val NOTIFICATION_ID = 7001
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val localNotifcationManager: LocalNotificationManager = get()
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