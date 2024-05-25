package com.example.smartstudy.presentation.screens.task

import com.example.smartstudy.domain.model.Subject

sealed class TaskEvents {
    data class OnTitleChanged(val title: String) : TaskEvents()
    data class OnDescriptionChanged(val description: String) : TaskEvents()
    data class OnPriorityChanged(val priority: Int) : TaskEvents()
    data class OnDueDateChanged(val millis: Long?) : TaskEvents()
    data object OnIsCompletedChanged : TaskEvents()
    data class OnRelatedSubjectSelected(val subject: Subject) : TaskEvents()
    data object SaveTask : TaskEvents()
    data object DeleteTask : TaskEvents()
}