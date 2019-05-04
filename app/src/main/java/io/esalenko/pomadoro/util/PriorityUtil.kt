package io.esalenko.pomadoro.util

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.db.model.task.Priority

@ColorRes
fun Priority.getPriorityColor(): Int = when (this) {
    Priority.LOW -> {
        R.color.priority_low
    }
    Priority.MID -> {
        R.color.priority_mid
    }
    Priority.HIGH -> {
        R.color.priority_high
    }
}

@DrawableRes
fun Priority.getPriorityIcon(): Int = when (this) {
    Priority.LOW -> {
        R.drawable.ic_priority_low
    }
    Priority.MID -> {
        R.drawable.ic_priority_mid
    }
    Priority.HIGH -> {
        R.drawable.ic_priority_high
    }
}