package io.esalenko.pomadoro.util

import android.annotation.SuppressLint
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat", "CheckResult")
fun Date.observeFormatDateTime(): Flowable<String> {
    return Flowable.timer(TimeUnit.SECONDS.toMillis(5), TimeUnit.MILLISECONDS)
        .toObservable()
        .switchMap {
            Observable.create<String> { emitter ->
                val result: String = this.formatDate()
                emitter.onNext(result)
            }
        }
        .toFlowable(BackpressureStrategy.DROP)
}

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
        else -> {
            val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm")
            sdf.format(this)
        }
    }
}