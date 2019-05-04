package io.esalenko.pomadoro.receiver

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import io.esalenko.pomadoro.manager.LocalAlarmManager
import io.esalenko.pomadoro.manager.LocalNotificationManager
import io.esalenko.pomadoro.ui.activity.MainActivity
import org.koin.core.KoinComponent
import org.koin.core.get


class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    companion object {
        const val KEY_TASK_IS_COMPLETED = "key_task_is_completed"
        const val KEY_TASK_ID = "key_task_id"
        private const val REQUEST_CODE = 6003
        private const val NOTIFICATION_ID = 7001
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        val taskId: Long = intent?.getLongExtra(LocalAlarmManager.KEY_FINISHED_TASK_ID, -1) ?: -1
        val isCompleted: Boolean =
            intent?.getBooleanExtra(LocalAlarmManager.KEY_FINISHED_TASK_IS_COMPLETED, false) ?: false
        val msg = intent?.getStringExtra(LocalAlarmManager.KEY_FINISHED_TASK_MSG)

        val localNotificationManager: LocalNotificationManager = get()
        val bundle: Bundle = Bundle().apply {
            putLong(KEY_TASK_ID, taskId)
            putBoolean(KEY_TASK_IS_COMPLETED, isCompleted)
        }
        val notification: Notification? = localNotificationManager.createNotification(
            context,
            "$msg session has been finished",
            "Click to see details",
            REQUEST_CODE,
            isVibrate = true,
            clazz = MainActivity::class.java,
            bundle = bundle
        )

        val notificationManager = context?.getSystemService(NotificationManager::class.java)

        notificationManager?.notify(NOTIFICATION_ID, notification)
    }
}