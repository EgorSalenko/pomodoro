package io.esalenko.pomadoro.db.model.task

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "task_table")
data class Task(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    var category: TaskCategory,
    var description: String,
    var priority: TaskPriority,
    var date: Date?,
    var pomidors: Int = 0,
    var isRunning: Boolean = false,
    var isArchived: Boolean = false,
    var isCompleted: Boolean = false,
    var isCooldown: Boolean = false
) {

}