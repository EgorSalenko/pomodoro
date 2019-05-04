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

    companion object {
        const val KEY_FINISHED_TASK_IS_COMPLETED = "key_finished_task_is_completed"
        const val KEY_FINISHED_TASK_ID = "key_finished_task_id"
        const val KEY_FINISHED_TASK_MSG = "key_finished_task_msg"
    }

    fun startAlarm(ctx: Context?, taskId: Long, isCooldown: Boolean, msg: String? = null) {
        updateTask(taskId, isCooldown)
        alarmMgr = ctx?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(ctx, AlarmReceiver::class.java).apply {
            putExtra(KEY_FINISHED_TASK_ID, taskId)
            putExtra(KEY_FINISHED_TASK_IS_COMPLETED, false)
            if (msg != null) {
                putExtra(KEY_FINISHED_TASK_MSG, msg)
            }
        }
        alarmIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0)

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