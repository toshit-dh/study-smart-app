package com.example.smartstudy.presentation.screens.task

import com.example.smartstudy.domain.model.Subject
import com.example.smartstudy.utils.Priority

data class TaskStates (
    val title: String = "",
    val description: String = "",
    val dueDate: Long? = null,
    val isTaskCompleted: Boolean = false,
    val priority: Priority = Priority.LOW,
    val relatedToSubject: String? = null,
    val subjects: List<Subject> = emptyList(),
    val subjectId: Int? = null,
    val currentTaskId: Int? = null
)