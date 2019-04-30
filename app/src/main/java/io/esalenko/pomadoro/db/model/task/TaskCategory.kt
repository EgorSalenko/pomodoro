package io.esalenko.pomadoro.db.model.task


enum class TaskCategory(val categoryName: String) {
    NONE(""),
    WORK("Work"),
    SELF_IMPROVEMENT("Self Improvement"),
    READING("Reading"),
    EDUCATION("Education");

    override fun toString(): String {
        return this.categoryName
    }
}
