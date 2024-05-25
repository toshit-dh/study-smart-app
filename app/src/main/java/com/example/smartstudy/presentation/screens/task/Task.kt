package com.example.smartstudy.presentation.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartstudy.R
import com.example.smartstudy.presentation.components.DeleteDialogBox
import com.example.smartstudy.presentation.components.SubjectsBottomSheet
import com.example.smartstudy.presentation.components.TaskCheckBox
import com.example.smartstudy.presentation.components.TaskDatePicker
import com.example.smartstudy.utils.Priority
import com.example.smartstudy.utils.SnackBarEvent
import com.example.smartstudy.utils.changeMillisToString
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant

data class TaskNavArgs(
    val subjectId: Int?,
    val taskId: Int?
)

@Destination(navArgsDelegate = TaskNavArgs::class)
@Composable
fun TaskScreenRoute(
    navigator: DestinationsNavigator
) {
    val viewModel: TaskViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Task(
        states = state,
        snackBarEvent = viewModel.snackBarEventFlow,
        onEvent = viewModel::onEvent,
        onBackButtonClick = {
            navigator.navigateUp()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private
fun TaskScreenTopBar(
    isTaskExists: Boolean,
    isComplete: Boolean,
    checkBoxBorderColor: Color,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onCheckBoxClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.task),
                style = MaterialTheme.typography.headlineMedium
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.arrow_back_nav)
                )
            }
        },
        actions = {
            if (isTaskExists) {
                TaskCheckBox(
                    isComplete = isComplete,
                    borderColor = checkBoxBorderColor,
                    onCheckBoxClick = onCheckBoxClick
                )
                IconButton(
                    onClick = onDeleteButtonClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_this_task)
                    )
                }
            }
        }
    )
}

@Composable
private fun PriorityButton(
    modifier: Modifier = Modifier,
    label: String,
    bgColor: Color,
    labelColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(bgColor)
            .clickable {
                onClick()
            }
            .padding(5.dp)
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = label,
            color = labelColor,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Task(
    states: TaskStates,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onEvent: (TaskEvents) -> Unit,
    onBackButtonClick: () -> Unit
) {
    var taskTitleError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var isDatePickerDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isBottomSheetOpen by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    val snackBarHostState = remember {
        SnackbarHostState()
    }
    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event ->
            when (event) {
                is SnackBarEvent.ShowSnackBar -> snackBarHostState
                    .showSnackbar(
                        message = event.message,
                        duration = event.duration
                    )

                SnackBarEvent.NavigateUp -> {
                    onBackButtonClick()
                }
            }
        }
    }
    taskTitleError = when {
        states.title.isBlank() -> stringResource(R.string.please_enter_a_title_to_the_task)
        states.title.length < 4 -> stringResource(R.string.task_title_is_too_short)
        states.title.length > 30 -> stringResource(R.string.task_title_is_too_long)
        else -> null
    }
    DeleteDialogBox(
        isOpen = isDeleteDialogOpen,
        title = stringResource(R.string.delete_task),
        bodyText = stringResource(R.string.body_text_of_delete_dialog1),
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(TaskEvents.DeleteTask)
            isDeleteDialogOpen = false
        }
    )
    TaskDatePicker(
        state = datePickerState,
        isOpen = isDatePickerDialogOpen,
        onDismissRequest = {
            isDatePickerDialogOpen = false
        },
        onConfirmButtonClick = {
            onEvent(TaskEvents.OnDueDateChanged(datePickerState.selectedDateMillis))
            isDatePickerDialogOpen = false
        }
    )
    SubjectsBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subject = states.subjects,
        onSubjectClick = {
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onEvent(TaskEvents.OnRelatedSubjectSelected(it))
        },
        onDismissRequest = {
            isBottomSheetOpen = false
        }
    )
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            TaskScreenTopBar(
                isTaskExists = states.currentTaskId != null,
                isComplete = states.isTaskCompleted,
                checkBoxBorderColor = states.priority.color,
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = {
                    isDeleteDialogOpen = true
                },
                onCheckBoxClick = {
                    onEvent(TaskEvents.OnIsCompletedChanged)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
                .verticalScroll(state = rememberScrollState())
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = states.title,
                onValueChange = {
                    onEvent(TaskEvents.OnTitleChanged(it))
                },
                label = {
                    Text(
                        text = stringResource(R.string.title)
                    )
                },
                singleLine = true,
                isError = taskTitleError != null && states.title.isNotBlank(),
                supportingText = {
                    Text(
                        text = taskTitleError.orEmpty()
                    )
                }
            )
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = states.description,
                onValueChange = {
                    onEvent(TaskEvents.OnDescriptionChanged(it))
                },
                label = {
                    Text(
                        text = stringResource(R.string.description)
                    )
                },
            )
            Spacer(
                modifier = Modifier.height(20.dp)
            )
            Text(
                text = stringResource(R.string.due_date),
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = states.dueDate.changeMillisToString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = {
                        isDatePickerDialogOpen = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.select_due_date)
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(10.dp)
            )
            Text(
                text = stringResource(R.string.priority),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Priority.entries.forEach { priority ->
                    PriorityButton(
                        modifier = Modifier.weight(1f),
                        label = priority.title,
                        bgColor = priority.color,
                        labelColor = Color.Black,
                        borderColor = if (priority == states.priority) Color.DarkGray else Color.Transparent,
                        onClick = {
                            onEvent(TaskEvents.OnPriorityChanged(priority.value))
                        }
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
            }
            Spacer(
                modifier = Modifier.height(30.dp)
            )
            Text(
                text = stringResource(R.string.related_to_subject),
                style = MaterialTheme.typography.bodySmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val firstSubject = states.subjects.firstOrNull()?.name ?: ""
                Text(
                    text = states.relatedToSubject ?: firstSubject,
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(
                    onClick = {
                        isBottomSheetOpen = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.select_subject)
                    )
                }
            }
            Button(
                enabled = taskTitleError == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                onClick = {
                    onEvent(TaskEvents.SaveTask)
                }
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}



