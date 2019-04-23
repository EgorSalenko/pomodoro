package io.esalenko.pomadoro.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
fun Date.formatDateTime(): String {
    return when{
        TimeUnit.SECONDS.toMillis(15) > System.currentTimeMillis() - this.time -> {
            "Just now"
        }
        TimeUnit.MINUTES.toMillis(1) > System.currentTimeMillis() - this.time -> {
            "Less then minute ago"
        }
        TimeUnit.HOURS.toMillis(1) > System.currentTimeMillis() - this.time -> {
            "Hour ago"
        }
        else -> {
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
            sdf.format(this)
        }
    }

}