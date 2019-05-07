package io.esalenko.pomadoro.manager

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import io.esalenko.pomadoro.R


class LocalNotificationManager {

    companion object {
        private const val CHANNEL_ID = "countdown_service_notification_channel_id"
        private const val CHANNEL_NAME = "countdown_service_notification_channel_name"
    }

    fun <T : Context> createNotification(
        ctx: Context?,
        title: String,
        content: String,
        requestCode: Int,
        isVibrate: Boolean = false,
        clazz: Class<T>,
        bundle: Bundle? = null
    ): Notification? {

        val notificationBuilder: NotificationCompat.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: String = CHANNEL_NAME
            val importance: Int = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                vibrationPattern = if (isVibrate) {
                    enableVibration(true)
                    longArrayOf(1000L, 1000L, 1000L, 1000L)
                } else {
                    enableVibration(false)
                    longArrayOf(0L)
                }
            }

            val notificationManager: NotificationManager =
                ctx?.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

            NotificationCompat.Builder(ctx, mChannel.id)
        } else {
            NotificationCompat.Builder(ctx)
        }

        notificationBuilder
            .setChannelId(CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_tomato)
            .setContentText(content)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    ctx,
                    requestCode,
                    bundle.let {
                        Intent(ctx, clazz).apply {
                            if (bundle != null) {
                                putExtras(bundle)
                            }
                        }
                    }
                    ,
                    0
                )
            )

        if (isVibrate) {
            notificationBuilder.setVibrate(longArrayOf(1000L, 1000L, 1000L))

        } else {
            notificationBuilder.setVibrate(longArrayOf(-1L))
        }
        return notificationBuilder.build()
    }
}
