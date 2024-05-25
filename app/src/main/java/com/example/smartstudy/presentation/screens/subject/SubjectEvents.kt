package com.example.smartstudy.presentation.screens.subject

import androidx.compose.ui.graphics.Color
import com.example.smartstudy.domain.model.Session
import com.example.smartstudy.domain.model.Task

sealed class SubjectEvents {
    data object UpdateSubject: SubjectEvents()
    data object DeleteSubject: SubjectEvents()
    data object DeleteSession: SubjectEvents()
    data object UpdateProgress: SubjectEvents()
    data class OnTaskIsCompleteChange(val task: Task): SubjectEvents()
    data class OnSubjectColorChange(val color: List<Color>): SubjectEvents()
    data class OnSubjectNameChange(val name: String): SubjectEvents()
    data class OnGoalStudyHoursChange(val hours: String): SubjectEvents()
    data class OnDeleteSessionClick(val session: Session): SubjectEvents()
}
