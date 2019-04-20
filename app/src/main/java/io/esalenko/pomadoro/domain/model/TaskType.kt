package io.esalenko.pomadoro.domain.model


enum class TaskType(val type: String) {
    WORK("Work"),
    SELF_IMPROVEMENT("Self Improvement"),
    ART("Art"),
    EDUCATION("Education"),
    SPORT("Sport")
}