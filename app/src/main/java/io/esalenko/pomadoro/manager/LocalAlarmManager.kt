package io.esalenko.pomadoro.manager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.esalenko.pomadoro.receiver.AlarmReceiver


object LocalAlarmManager {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    fun startAlarm(ctx: Context?) {
        alarmMgr = ctx?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(ctx, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(ctx, 0, intent, 0)
        }

        alarmMgr?.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            alarmIntent
        )
    }
}