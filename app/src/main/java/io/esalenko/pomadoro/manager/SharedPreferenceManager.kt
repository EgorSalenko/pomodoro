package io.esalenko.pomadoro.manager

import android.content.SharedPreferences
import androidx.core.content.edit
import io.esalenko.pomadoro.BuildConfig
import java.util.concurrent.TimeUnit


class SharedPreferenceManager(private val sharedPreferences: SharedPreferences) {
    fun clear() {
        sharedPreferences.edit {
            clear()
        }
    }

    companion object {
        const val SHARED_PREFERENCE_NAME = "local_shared_preference"

        const val KEY_TIMER_DURATION = "key_timer_duration"
        const val KEY_IS_FIRST_INIT = "key_is_first_init"
        const val KEY_LONG_COOLDOWN_TIMESTAMP = "key_long_cooldown_timestamp"
        const val KEY_LAST_STARTED_TASK_TIMER_ID = "key_last_task_timer_id"
        const val KEY_CACHED_FILTER = "key_cached_filter"
        const val KEY_CACHED_PRIORITY = "key_cached_priority"

        var TIMER_DURATION_IN_MINUTES = 25L
        var COOLDOWN_DURATION_IN_MINUTES = 5L

        var DEFAULT_TIMER_DURATION =
            if (BuildConfig.DEBUG) TimeUnit.MINUTES.toMillis(1) else TimeUnit.MINUTES.toMillis(TIMER_DURATION_IN_MINUTES)
        var DEFAULT_LONG_COOLDOWN_DURATION =
            if (BuildConfig.DEBUG) TimeUnit.SECONDS.toMillis(20) else TimeUnit.MINUTES.toMillis(
                COOLDOWN_DURATION_IN_MINUTES
            )
    }


    val isFirstInit: Boolean
        get() = firstInit == -1

    var firstInit: Int
        get() = sharedPreferences.getInt(KEY_IS_FIRST_INIT, -1)
        set(value) = sharedPreferences.edit {
            putInt(KEY_IS_FIRST_INIT, value)
        }

    var timerDuration: Long
        get() = sharedPreferences.getLong(
            KEY_TIMER_DURATION,
            DEFAULT_TIMER_DURATION
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_TIMER_DURATION, value)
        }

    var cooldownDuration: Long
        get() = sharedPreferences.getLong(
            KEY_LONG_COOLDOWN_TIMESTAMP,
            DEFAULT_LONG_COOLDOWN_DURATION
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_LONG_COOLDOWN_TIMESTAMP, value)
        }

    var lastStartedTaskId: Long
        get() = sharedPreferences.getLong(
            KEY_LAST_STARTED_TASK_TIMER_ID, -1
        )
        set(value) = sharedPreferences.edit {
            putLong(KEY_LAST_STARTED_TASK_TIMER_ID, value)
        }

    var cachedFilterOrdinal: Int
        get() = sharedPreferences.getInt(KEY_CACHED_FILTER, -1)
        set(value) = sharedPreferences.edit {
            putInt(KEY_CACHED_FILTER, value)
        }
    var cachedPriorityOrdinal: Int
        get() = sharedPreferences.getInt(KEY_CACHED_PRIORITY, -1)
        set(value) = sharedPreferences.edit {
            putInt(KEY_CACHED_PRIORITY, value)
        }
}