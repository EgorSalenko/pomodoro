package io.esalenko.pomadoro.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
fun Date.formatDate(): String {
    return when {
        TimeUnit.SECONDS.toMillis(15) > System.currentTimeMillis() - this.time -> {
            "Just now"
        }
        TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis() - this.time -> {
            "Less then minute ago"
        }
        TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis() - this.time -> {
            "5 minute ago"
        }
        TimeUnit.MINUTES.toMillis(15) > System.currentTimeMillis() - this.time -> {
            "15 minutes ago"
        }
        TimeUnit.HOURS.toMillis(1) > System.currentTimeMillis() - this.time -> {
            "Hour ago"
        }
        TimeUnit.HOURS.toMillis(24) > System.currentTimeMillis() - this.time -> {
            val sdf = SimpleDateFormat("HH:mm")
            sdf.format(this)
        }
        else -> {
            val sdf = SimpleDateFormat("dd.MM")
            sdf.format(this)
        }
    }
}