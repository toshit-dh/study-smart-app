package com.example.smartstudy.presentation.screens.task

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartstudy.domain.model.Task
import com.example.smartstudy.domain.repository.SubjectRepository
import com.example.smartstudy.domain.repository.TaskRepository
import com.example.smartstudy.presentation.screens.navArgs
import com.example.smartstudy.utils.Priority
import com.example.smartstudy.utils.SnackBarEvent
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
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId = savedStateHandle.navArgs<TaskNavArgs>().taskId
    private val subId = savedStateHandle.navArgs<TaskNavArgs>().subjectId

    private val _state = MutableStateFlow(TaskStates())
    val state = combine(
        _state,
        subjectRepository.getSubjects()
    ) { state, subjects ->
        state.copy(
            subjects = subjects
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = TaskStates()
        )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    init {
        fetchTask()
        fetchSubject()
    }

    fun onEvent(event: TaskEvents) {
        when (event) {
            TaskEvents.DeleteTask -> deleteTask()

            is TaskEvents.OnDescriptionChanged -> {
                _state.update {
                    it.copy(
                        description = event.description
                    )
                }
            }

            is TaskEvents.OnDueDateChanged -> {
                _state.update {
                    it.copy(
                        dueDate = event.millis
                    )
                }
            }

            TaskEvents.OnIsCompletedChanged -> {
                _state.update {
                    it.copy(
                        isTaskCompleted = !state.value.isTaskCompleted
                    )
                }
            }

            is TaskEvents.OnPriorityChanged -> {
                _state.update {
                    it.copy(
                        priority = Priority.fromInt(event.priority)
                    )
                }
            }

            is TaskEvents.OnRelatedSubjectSelected -> {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            is TaskEvents.OnTitleChanged -> {
                _state.update {
                    it.copy(
                        title = event.title
                    )
                }
            }

            TaskEvents.SaveTask -> saveTask()
        }
    }

    private fun deleteTask() {
        viewModelScope.launch {
            try {
                val currentTask = state.value.currentTaskId
                if (currentTask != null) {
                    withContext(Dispatchers.IO) {
                        taskRepository.deleteTask(currentTask)
                    }
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            message = "Task deleted",
                            duration = SnackbarDuration.Short
                        )
                    )
                    _snackBarEventFlow.emit(
                        SnackBarEvent.NavigateUp
                    )
                } else
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            message = "Couldn't delete task. Task not found",
                            duration = SnackbarDuration.Short
                        )
                    )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't delete task. ${e.message}",
                        duration = SnackbarDuration.Short
                    )
                )
            }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            if (state.value.subjectId == null || state.value.relatedToSubject == null) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Please select subject related to this task.",
                        duration = SnackbarDuration.Short
                    )
                )
                return@launch
            }
            try {
                taskRepository.upsertTask(
                    task = Task(
                        title = state.value.title,
                        description = state.value.description,
                        dueDate = state.value.dueDate ?: Instant.now().toEpochMilli(),
                        relatedToSubject = state.value.relatedToSubject!!,
                        priority = state.value.priority.value,
                        isCompleted = state.value.isTaskCompleted,
                        taskSubjectId = state.value.subjectId!!,
                        taskId = state.value.currentTaskId
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Task Added Successfully",
                        duration = SnackbarDuration.Short
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.NavigateUp
                )
            } catch (e: Exception) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't Add Task. ${e.message}",
                        duration = SnackbarDuration.Short
                    )
                )
            }
        }
    }

    private fun fetchTask() {
        viewModelScope.launch {
            taskId?.let {
                taskRepository.getTask(taskId)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            priority = Priority.fromInt(task.priority),
                            dueDate = task.dueDate,
                            isTaskCompleted = task.isCompleted,
                            relatedToSubject = task.relatedToSubject,
                            subjectId = task.taskSubjectId,
                            currentTaskId = task.taskId
                        )
                    }
                }
            }
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            subId?.let {
                subjectRepository.getSubject(subId)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectId = subject.subjectId,
                            relatedToSubject = subject.name
                        )
                    }
                }
            }
        }
    }
}