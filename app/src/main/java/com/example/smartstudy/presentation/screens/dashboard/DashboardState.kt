package com.example.smartstudy.presentation.screens.dashboard

import androidx.compose.ui.graphics.Color
import com.example.smartstudy.domain.model.Session
import com.example.smartstudy.domain.model.Subject

data class DashboardState(
    val totalSubjectCount: Int = 0,
    val totalStudyHours: Float = 0f,
    val totalGoalStudyHours: Float = 0f,
    val subjects: List<Subject> = emptyList(),
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val subjectCardColors: List<Color> = Subject.subjectColors.random(),
    val session: Session? = null
)