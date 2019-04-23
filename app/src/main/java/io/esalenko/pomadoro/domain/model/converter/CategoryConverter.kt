package io.esalenko.pomadoro.domain.model.converter

import androidx.room.TypeConverter
import io.esalenko.pomadoro.domain.model.TaskCategory


class CategoryConverter {

    @TypeConverter
    fun fromIndexToCategory(value: Int?): TaskCategory? {
        return value?.let { TaskCategory.values()[value] }
    }

    @TypeConverter
    fun categoryToIndex(category: TaskCategory?): Int? {
        return category?.ordinal
    }

}

