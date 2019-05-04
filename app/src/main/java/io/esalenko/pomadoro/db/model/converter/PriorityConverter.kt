package io.esalenko.pomadoro.db.model.converter

import androidx.room.TypeConverter
import io.esalenko.pomadoro.db.model.task.Priority


class PriorityConverter {

    @TypeConverter
    fun fromIndexToPriority(value: Int?): Priority? {
        return value?.let { Priority.values()[value] }
    }

    @TypeConverter
    fun priorityToIndex(value: Priority?): Int? {
        return value?.ordinal
    }

}

