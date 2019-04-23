package io.esalenko.pomadoro.domain.model.converter

import androidx.room.TypeConverter
import io.esalenko.pomadoro.domain.model.TaskPriority


class PriorityConverter {

    @TypeConverter
    fun fromIndexToPriority(value: Int?): TaskPriority? {
        return value?.let { TaskPriority.values()[value] }
    }

    @TypeConverter
    fun priorityToIndex(value: TaskPriority?): Int? {
        return value?.ordinal
    }

}

