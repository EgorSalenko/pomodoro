package io.esalenko.pomadoro.util

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import io.esalenko.pomadoro.R
import io.esalenko.pomadoro.domain.model.TaskPriority

@ColorRes
fun TaskPriority.getPriorityColor(): Int = when (this) {
    TaskPriority.LOW -> {
        R.color.priority_low
    }
    TaskPriority.MID -> {
        R.color.priority_mid
    }
    TaskPriority.HIGH -> {
        R.color.priority_high
    }
}

@DrawableRes
fun TaskPriority.getPriorityIcon(): Int = when (this) {
    TaskPriority.LOW -> {
        R.drawable.ic_priority_low
    }
    TaskPriority.MID -> {
        R.drawable.ic_priority_mid
    }
    TaskPriority.HIGH -> {
        R.drawable.ic_priority_high
    }
}