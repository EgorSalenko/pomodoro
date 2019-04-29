package io.esalenko.pomadoro.db.model.converter

import androidx.room.TypeConverter
import io.esalenko.pomadoro.db.model.task.TaskPriority


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

