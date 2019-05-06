package io.esalenko.pomadoro.util

import android.os.SystemClock


const val DELAY = 650L
var mLastClickTime: Long = 0L

fun avoidDoubleClick(body: () -> Unit) {
    if (SystemClock.elapsedRealtime() - mLastClickTime < DELAY) {
        return
    }
    body()
    mLastClickTime = SystemClock.elapsedRealtime()
}