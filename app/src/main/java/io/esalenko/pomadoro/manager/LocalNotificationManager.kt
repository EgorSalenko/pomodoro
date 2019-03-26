package io.esalenko.pomadoro.manager

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.ui.MainActivity
import javax.inject.Inject


class LocalNotificationManager @Inject constructor(ctx: Context) {

    companion object {
        private const val CHANNEL_ID = "countdown_service_notification_channel_id"
        private const val CHANNEL_NAME = "countdown_service_notification_channel_name"
    }

    val notificationBuilder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name: String = CHANNEL_NAME
        val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
        val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            .apply {
                enableVibration(true)
                setSound(null, null)
            }

        val notificationManager: NotificationManager =
            ctx.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(mChannel)

        NotificationCompat.Builder(ctx, mChannel.id)
    } else {
        NotificationCompat.Builder(ctx)
    }

    fun createNotification(
        ctx: Context?,
        title: String,
        content: String,
        requestCode: Int,
        isAlarm: Boolean = false
    ): Notification? =
        ctx?.setupNotification(title, content, requestCode, isAlarm)

    private fun Context?.setupNotification(
        title: String,
        content: String = "",
        requestCode: Int,
        isAlarm: Boolean
    ): Notification {

        notificationBuilder
            .setChannelId(CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText(content)
            .setContentTitle(title)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    requestCode,
                    Intent(this, MainActivity::class.java),
                    0
                )
            )

        return if (isAlarm) {
            val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            notificationBuilder
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000L, 1000L, 1000L))
                .setSound(alarmSound)
                .build()
        } else {
            notificationBuilder
                .setAutoCancel(false)
                .setSound(null)
                .build()
        }
    }
}
