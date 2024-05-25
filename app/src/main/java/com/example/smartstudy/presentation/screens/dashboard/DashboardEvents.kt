package com.example.smartstudy.presentation.screens.dashboard

import androidx.compose.ui.graphics.Color
import com.example.smartstudy.domain.model.Session
import com.example.smartstudy.domain.model.Task

sealed class DashboardEvents {
    data class OnDeleteSessionClick(val session: Session) : DashboardEvents()
    data class OnTaskIsCompleteChange(val task: Task) : DashboardEvents()
    data class OnSubjectCardColorChange(val colors: List<Color>) : DashboardEvents()
    data class OnSubjectNameChange(val name: String) : DashboardEvents()
    data class OnGoalHourChange(val hours: String) : DashboardEvents()
    data object SaveSubject : DashboardEvents()
    data object DeleteSession : DashboardEvents()
}