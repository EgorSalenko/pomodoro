package io.esalenko.pomadoro.domain.model


enum class TaskCategory(val category: String) {
    NONE(""),
    WORK("Work"),
    SELF_IMPROVEMENT("Self Improvement"),
    READING("Reading"),
    EDUCATION("Education");

    override fun toString(): String {
        return this.category
    }
}

