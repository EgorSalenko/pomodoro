package io.esalenko.pomadoro.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    // TODO :: Implement type converter
    val category: TaskCategory,
    val description: String,
    val priority: TaskPriority,
    val date: Date?,
    var isArchived: Boolean = false,
    var isCompleted: Boolean = false
)