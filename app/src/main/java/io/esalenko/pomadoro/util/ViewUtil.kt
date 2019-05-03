package io.esalenko.pomadoro.util

import android.os.SystemClock


var mLastClickTime: Long = 0L

fun avoidDoubleClick(body: () -> Unit) {
    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
        return
    }
    body()
    mLastClickTime = SystemClock.elapsedRealtime()
}