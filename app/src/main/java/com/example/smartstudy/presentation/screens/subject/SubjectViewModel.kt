package com.example.smartstudy.presentation.screens.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartstudy.domain.model.Subject
import com.example.smartstudy.domain.model.Task
import com.example.smartstudy.domain.repository.SessionRepository
import com.example.smartstudy.domain.repository.SubjectRepository
import com.example.smartstudy.domain.repository.TaskRepository
import com.example.smartstudy.presentation.screens.navArgs
import com.example.smartstudy.utils.SnackBarEvent
import com.example.smartstudy.utils.toHours
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository,
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val subjectId: Int = savedStateHandle.navArgs<SubjectNavArgs>().subjectId
    private val _state = MutableStateFlow(SubjectStates())
    val state = combine(
        _state,
        taskRepository.getAllUpcomingTasksForSubject(subjectId),
        taskRepository.getCompletedTasksForSubject(subjectId),
        sessionRepository.getRecentSessionsForSubject(subjectId),
        sessionRepository.getSessionDurationForSubject(subjectId)
    ) { state, upcomingTasks, completedTasks, recentSessions, totalSessionsDuration ->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTasks,
            recentSession = recentSessions,
            studiedHours = totalSessionsDuration.toHours()
        )
    }
        .stateIn(
            scope = viewModelScope,
            initialValue = SubjectStates(),
            started = SharingStarted.WhileSubscribed(5000)
        )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    init {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvents) {
        when (event) {
            SubjectEvents.DeleteSession -> deleteSession()

            SubjectEvents.DeleteSubject -> deleteSubject()

            is SubjectEvents.OnDeleteSessionClick -> {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            is SubjectEvents.OnGoalStudyHoursChange -> {
                _state.update {
                    it.copy(
                        goalStudyHours = event.hours
                    )
                }
            }

            is SubjectEvents.OnSubjectColorChange -> {
                _state.update {
                    it.copy(
                        subjectCardColor = event.color
                    )
                }
            }

            is SubjectEvents.OnSubjectNameChange -> {
                _state.update {
                    it.copy(
                        subjectName = event.name
                    )
                }

            }

            is SubjectEvents.OnTaskIsCompleteChange -> updateTask(event.task)

            SubjectEvents.UpdateSubject -> updateSubject()

            SubjectEvents.UpdateProgress -> {
                val goalHours = state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours / goalHours).coerceIn(0f, 1f)
                    )
                }
            }
        }
    }

    fun fetchSubject() {
        viewModelScope.launch {
            subjectRepository.getSubject(subjectId)?.let { subject ->
                _state.update {
                    it.copy(
                        subjectName = subject.name,
                        currentSubjectId = subject.subjectId,
                        goalStudyHours = subject.goalHours,
                        subjectCardColor = subject.color.map { int ->
                            Color(int)
                        }
                    )
                }
            }
        }
    }

    private fun updateSubject() {
        viewModelScope.launch {
            try {
                subjectRepository.upsertSubject(
                    subject =
                    Subject(
                        subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours,
                        color = state.value.subjectCardColor.map {
                            it.toArgb()
                        }
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Subject updated",
                        duration = SnackbarDuration.Short
                    )
                )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't update subject. ${e.message}",
                        duration = SnackbarDuration.Short
                    )
                )
            }
        }
    }

    private fun deleteSubject() {
        viewModelScope.launch {
            try {
                val currentSubject = state.value.currentSubjectId
                if (currentSubject != null) {
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(currentSubject)
                    }
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            message = "Subject deleted",
                            duration = SnackbarDuration.Short
                        )
                    )
                    _snackBarEventFlow.emit(
                        SnackBarEvent.NavigateUp
                    )
                } else
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            message = "Couldn't delete subject. Subject not found",
                            duration = SnackbarDuration.Short
                        )
                    )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't delete subject. ${e.message}",
                        duration = SnackbarDuration.Short
                    )
                )
            }
        }
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                taskRepository.upsertTask(
                    task.copy(
                        isCompleted = !task.isCompleted
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = if (task.isCompleted) "Saved in completed tasks" else "Saved in incomplete tasks",
                        duration = SnackbarDuration.Short
                    )
                )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't update task> ${e.message}",
                        duration = SnackbarDuration.Short
                    )
                )
            }
        }
    }

    private fun deleteSession() {
        viewModelScope.launch {
            try {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "Session deleted successfully")
                    )
                }
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't delete session. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }
        }
    }
}