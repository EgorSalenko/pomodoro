package io.esalenko.pomadoro.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.concurrent.TimeUnit


class SharedPreferenceManager(private val sharedPreferences: SharedPreferences) {

    companion object {
        const val SHARED_PREFERENCE_NAME = "local_shared_preference"

        const val KEY_TIMER_DURATION = "key_timer_duration"
        const val KEY_IS_PAUSE = "key_is_pause"
        const val KEY_SHORT_COOLDOWN_TIMESTAMP = "key_short_cooldown_timestamp"
        const val KEY_LONG_COOLDOWN_TIMESTAMP = "key_long_cooldown_timestamp"
        const val KEY_SESSIONS_COUNTER = "key_sessions_counter"
        const val KEY_CACHED_TASK_DESCRIPTION = "key_cached_task_description"

        const val LONG_COOLDOWN_SESSION = 4

        var DEFAULT_TIMER_TIMESTAMP = TimeUnit.MINUTES.toMillis(25)

        var DEFAULT_SHORT_COOLDOWN_TIMESTAMP = TimeUnit.MINUTES.toMillis(5)
        var DEFAULT_LONG_COOLDOWN_TIMESTAMP = TimeUnit.MINUTES.toMillis(15)
    }

    var isPause: Boolean
        get() = sharedPreferences.getBoolean(
            KEY_IS_PAUSE,
            false
        )
        set(value) = sharedPreferences.edit {
            putBoolean(KEY_IS_PAUSE, value)
        }

    var timerDuration: Long
        get() = sharedPreferences.getLong(
            KEY_TIMER_DURATION,
            DEFAULT_TIMER_TIMESTAMP
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_TIMER_DURATION, value)
        }

    val cooldownDuration: Long
        get() = if (isLongCooldownSession) longCooldownDuration else shortCooldownDuration

    var shortCooldownDuration: Long
        get() = sharedPreferences.getLong(
            KEY_SHORT_COOLDOWN_TIMESTAMP,
            DEFAULT_SHORT_COOLDOWN_TIMESTAMP
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_SHORT_COOLDOWN_TIMESTAMP, value)
        }

    var longCooldownDuration: Long
        get() = sharedPreferences.getLong(
            KEY_LONG_COOLDOWN_TIMESTAMP,
            DEFAULT_LONG_COOLDOWN_TIMESTAMP
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_LONG_COOLDOWN_TIMESTAMP, value)
        }

    private var _sessionsCounter: Int
        get() = sharedPreferences.getInt(KEY_SESSIONS_COUNTER, 0)
        set(value) = sharedPreferences.edit {
            putInt(KEY_SESSIONS_COUNTER, value)
        }

    val sessionCounter: Int
        get() = _sessionsCounter

    var cachedTaskDescription: String
        get() =
            sharedPreferences
                .getString(KEY_SESSIONS_COUNTER, "Task Description is empty")
                ?: "Task Description is empty"
        set(value) = sharedPreferences.edit {
            putString(KEY_CACHED_TASK_DESCRIPTION, value)
        }

    fun incrementSessionCounter() {
        _sessionsCounter++
    }

    private val isLongCooldownSession: Boolean
        get() = _sessionsCounter == LONG_COOLDOWN_SESSION


}