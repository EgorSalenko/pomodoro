package io.esalenko.pomadoro.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(@PrimaryKey(autoGenerate = true) val id: Long = 0L,
                val type: String,
                val description: String,
                val priority: Int,
                val date: Long,
                var isArchived: Boolean = false
)