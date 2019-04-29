package io.esalenko.pomadoro.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import io.esalenko.pomadoro.BuildConfig
import java.util.concurrent.TimeUnit


class SharedPreferenceManager(private val sharedPreferences: SharedPreferences) {

    companion object {
        const val SHARED_PREFERENCE_NAME = "local_shared_preference"

        const val KEY_TIMER_DURATION = "key_timer_duration"
        const val KEY_SHORT_COOLDOWN_TIMESTAMP = "key_short_cooldown_timestamp"
        const val KEY_LONG_COOLDOWN_TIMESTAMP = "key_long_cooldown_timestamp"
        const val KEY_LAST_STARTED_TASK_TIMER_ID = "key_last_task_timer_id"
        const val KEY_IS_LONG_COOLDOWN_SESSION = "key_is_long_cooldown_session"

        const val LONG_COOLDOWN_SESSION = 4

        var DEFAULT_TIMER_DURATION =
            if (BuildConfig.DEBUG) TimeUnit.MINUTES.toMillis(1) else TimeUnit.MINUTES.toMillis(25)

        var DEFAULT_SHORT_COOLDOWN_DURATION =
            if (BuildConfig.DEBUG) TimeUnit.SECONDS.toMillis(10) else TimeUnit.MINUTES.toMillis(5)
        var DEFAULT_LONG_COOLDOWN_DURATION =
            if (BuildConfig.DEBUG) TimeUnit.SECONDS.toMillis(20) else TimeUnit.MINUTES.toMillis(15)
    }

    var timerDuration: Long
        get() = sharedPreferences.getLong(
            KEY_TIMER_DURATION,
            DEFAULT_TIMER_DURATION
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_TIMER_DURATION, value)
        }

    val cooldownDuration: Long
        get() = if (isLongCooldownSession) longCooldownDuration else shortCooldownDuration

    private var shortCooldownDuration: Long
        get() = sharedPreferences.getLong(
            KEY_SHORT_COOLDOWN_TIMESTAMP,
            DEFAULT_SHORT_COOLDOWN_DURATION
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_SHORT_COOLDOWN_TIMESTAMP, value)
        }

    private var longCooldownDuration: Long
        get() = sharedPreferences.getLong(
            KEY_LONG_COOLDOWN_TIMESTAMP,
            DEFAULT_LONG_COOLDOWN_DURATION
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_LONG_COOLDOWN_TIMESTAMP, value)
        }

    var isLongCooldownSession: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LONG_COOLDOWN_SESSION, false)
        set(value) = sharedPreferences.edit {
            putBoolean(KEY_IS_LONG_COOLDOWN_SESSION, value)
        }

    var lastStartedTaskId: Long
        get() = sharedPreferences.getLong(
            KEY_LAST_STARTED_TASK_TIMER_ID, -1
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_LAST_STARTED_TASK_TIMER_ID, value)
        }
}