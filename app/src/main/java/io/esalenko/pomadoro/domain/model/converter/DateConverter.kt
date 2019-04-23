package io.esalenko.pomadoro.domain.model.converter

import androidx.room.TypeConverter
import java.util.*


class DateConverter {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(value) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

}

