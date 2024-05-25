package com.example.smartstudy.presentation.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartstudy.R
import com.example.smartstudy.domain.model.Session
import com.example.smartstudy.domain.model.Subject
import com.example.smartstudy.domain.model.Task
import com.example.smartstudy.presentation.components.AddSubjectDialogBox
import com.example.smartstudy.presentation.components.CountCard
import com.example.smartstudy.presentation.components.DeleteDialogBox
import com.example.smartstudy.presentation.components.StudySessionList
import com.example.smartstudy.presentation.components.SubjectCard
import com.example.smartstudy.presentation.components.tasksList
import com.example.smartstudy.presentation.screens.destinations.SessionScreenRouteDestination
import com.example.smartstudy.presentation.screens.destinations.SubjectScreenRouteDestination
import com.example.smartstudy.presentation.screens.destinations.TaskScreenRouteDestination
import com.example.smartstudy.presentation.screens.subject.SubjectNavArgs
import com.example.smartstudy.presentation.screens.task.TaskNavArgs
import com.example.smartstudy.utils.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

@RootNavGraph(start = true)
@Destination
@Composable
fun DashboardScreenRoute(
    navigator: DestinationsNavigator
) {
    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    Dashboard(
        state = state,
        tasks = tasks,
        sessions = sessions,
        snackBarEvent = viewModel.snackBarEventFlow,
        onEvent = viewModel::onEvent,
        onSubjectClick = { subjectId ->
            subjectId?.let {
                val navArg = SubjectNavArgs(subjectId = it)
                navigator.navigate(SubjectScreenRouteDestination(navArg))
            }
        },
        onStartSessionClick = {
            navigator.navigate(SessionScreenRouteDestination())
        },
        onTaskClick = { task ->
            val navArg = TaskNavArgs(taskId = task.taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArg))
        }
    )
}

@Composable
private fun Dashboard(
    state: DashboardState,
    tasks: List<Task>,
    sessions: List<Session>,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onEvent: (DashboardEvents) -> Unit,
    onSubjectClick: (Int?) -> Unit,
    onTaskClick: (Task) -> Unit,
    onStartSessionClick: () -> Unit,
    taskSectionTitle: String = stringResource(id = R.string.upcoming_tasks),
    sessionSectionTitle: String = stringResource(R.string.recent_study_sessions),
    emptyTaskListText: String = stringResource(id = R.string.empty_task_list_text),
    emptySessionListText: String = stringResource(R.string.empty_list_text_session)
) {
    var isOpenAddDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isOpenDeleteDialog by rememberSaveable {
        mutableStateOf(false)
    }
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

                SnackBarEvent.NavigateUp -> {}
            }
        }
    }
    AddSubjectDialogBox(
        isOpen = isOpenAddDialog,
        onDismissRequest = { isOpenAddDialog = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvents.SaveSubject)
            isOpenAddDialog = false
        },
        selectedColors = emptyList(),
        onColorChange = { onEvent(DashboardEvents.OnSubjectCardColorChange(it)) },
        subjectName = state.subjectName,
        goalStudyHours = state.goalStudyHours,
        onSubjectNameChange = { onEvent(DashboardEvents.OnSubjectNameChange(it)) },
        onGoalStudyHourChange = { onEvent(DashboardEvents.OnGoalHourChange(it)) }
    )
    DeleteDialogBox(
        isOpen = isOpenDeleteDialog,
        title = stringResource(id = R.string.delete_session),
        bodyText = stringResource(id = R.string.body_text_of_delete_dialog),
        onDismissRequest = { isOpenDeleteDialog = false },
        onConfirmButtonClick = { isOpenDeleteDialog = false }
    )
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = { DashboardTopBar() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)

        ) {
            item {
                CountCardSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudyHours.toString(),
                    goalStudyHours = state.totalGoalStudyHours.toString()
                )
            }
            item {
                SubjectsSection(
                    modifier = Modifier.fillMaxWidth(),
                    subjectList = state.subjects,
                    onAddIconClick = {
                        isOpenAddDialog = true
                    },
                    onSubjectClick = onSubjectClick
                )
            }
            item {
                Button(
                    onClick = onStartSessionClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.start_a_study_session)
                    )
                }
            }
            tasksList(
                sectionTitle = taskSectionTitle,
                taskList = tasks,
                emptyListText = emptyTaskListText,
                onCheckBoxClick = { onEvent(DashboardEvents.OnTaskIsCompleteChange(it)) },
                onTaskCardClick = onTaskClick
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            StudySessionList(
                sectionTitle = sessionSectionTitle,
                emptyListText = emptySessionListText,
                sessionList = sessions,
                onDeleteIconClick = {
                    onEvent(DashboardEvents.OnDeleteSessionClick(it))
                    isOpenDeleteDialog = true
                }
            )
        }
    }
}

@Composable
private fun CountCardSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalStudyHours: String
) {
    Row {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = stringResource(R.string.subject_count),
            count = subjectCount.toString()
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = stringResource(R.string.studied_hours),
            count = studiedHours
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = stringResource(R.string.goal_study_hours),
            count = goalStudyHours
        )
    }
}

@Composable
private fun SubjectsSection(
    modifier: Modifier,
    subjectList: List<Subject>,
    emptyListText: String = stringResource(R.string.empty_list_text),
    onAddIconClick: () -> Unit,
    onSubjectClick: (Int?) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.subjects),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = onAddIconClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_a_subject)
                )
            }
        }
        if (subjectList.isEmpty()) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.img_books),
                contentDescription = emptyListText
            )
            Text(
                modifier = Modifier.fillMaxSize(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
            ) {
                items(subjectList)
                { subject ->
                    SubjectCard(
                        subjectName = subject.name,
                        gradientColor = subject.color.map { Color(it) },
                        onClick = {
                            onSubjectClick(subject.subjectId)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardTopBar() {
    CenterAlignedTopAppBar(title = {
        Text(
            text = stringResource(R.string.smart_study),
            style = MaterialTheme.typography.headlineMedium
        )
    })
}

