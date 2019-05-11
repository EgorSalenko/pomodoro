package io.esalenko.pomadoro.db.model.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_category")
data class Category(
    @PrimaryKey(autoGenerate = true)
    var categoryId: Long = 0L,
    @ColumnInfo(name = "category_name")
    var categoryName: String,
    val isDefault: Boolean = false
) {
    override fun toString(): String {
        return categoryName
    }
}

