package io.esalenko.pomadoro.domain.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(@PrimaryKey(autoGenerate = true) val id: Long = 0L,
                val taskDescription : String,
                @NonNull
                val duration : Long,
                val date : Long)