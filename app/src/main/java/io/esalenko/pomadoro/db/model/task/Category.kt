package io.esalenko.pomadoro.db.model.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_category")
data class Category(
    @PrimaryKey @ColumnInfo(name = "category_name")
    val categoryName: String
) {
    override fun toString(): String {
        return categoryName
    }
}

