package io.esalenko.pomadoro.manager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import io.esalenko.pomadoro.db.model.task.Task
import io.esalenko.pomadoro.receiver.AlarmReceiver
import io.esalenko.pomadoro.repository.TaskRepository
import org.koin.core.KoinComponent


class LocalAlarmManager(private val taskRxRepository: TaskRepository) : KoinComponent {

    private var alarmMgr: AlarmManager? = null
    private lateinit var alarmIntent: PendingIntent

    fun startAlarm(ctx: Context?, taskId: Long, isCooldown: Boolean) {
        updateTask(taskId, isCooldown)
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

    @SuppressLint("CheckResult")
    private fun updateTask(taskId: Long, cooldown: Boolean) {
        taskRxRepository
            .get(taskId)
            .map {
                it.apply {
                    isCooldown = !cooldown
                    isRunning = false
                    if (!cooldown) pomidors += 1
                }
            }
            .subscribe { task: Task, _ ->
                taskRxRepository.add(task)
            }
    }

    @SuppressLint("CheckResult")
    fun stopAlarm(taskId: Long) {
        taskRxRepository
            .get(taskId)
            .map {
                it.apply {
                    isRunning = false
                }
            }
            .subscribe { task: Task, _ ->
                taskRxRepository.add(task)
            }
    }
}