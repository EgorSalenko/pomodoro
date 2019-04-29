package io.esalenko.pomadoro.db.model.converter

import androidx.room.TypeConverter
import io.esalenko.pomadoro.db.model.task.TaskCategory


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

