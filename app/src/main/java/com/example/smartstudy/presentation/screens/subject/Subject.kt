package com.example.smartstudy.presentation.screens.subject

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartstudy.R
import com.example.smartstudy.domain.model.Task
import com.example.smartstudy.presentation.components.AddSubjectDialogBox
import com.example.smartstudy.presentation.components.CountCard
import com.example.smartstudy.presentation.components.DeleteDialogBox
import com.example.smartstudy.presentation.components.StudySessionList
import com.example.smartstudy.presentation.components.tasksList
import com.example.smartstudy.presentation.screens.task.TaskNavArgs
import com.example.smartstudy.presentation.screens.destinations.TaskScreenRouteDestination
import com.example.smartstudy.presentation.screens.session.SessionViewModel
import com.example.smartstudy.utils.SnackBarEvent
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest

data class SubjectNavArgs(
    val subjectId: Int
)

@Destination(navArgsDelegate = SubjectNavArgs::class)
@Composable
fun SubjectScreenRoute(
    navigator: DestinationsNavigator
) {
    val viewModel: SubjectViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Subject(
        state = state,
        onEvent = viewModel::onEvent,
        snackBarEvent = viewModel.snackBarEventFlow,
        onBackButtonClick = {
            navigator.navigateUp()
        },
        onAddTaskClick = {
            val navArg = TaskNavArgs(taskId = null, subjectId = -1)
            navigator.navigate(TaskScreenRouteDestination(navArg))
        },
        onTaskClick = { task ->
            val navArg = TaskNavArgs(taskId = task.taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArg))
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Subject(
    state: SubjectStates,
    onEvent: (SubjectEvents) -> Unit,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onBackButtonClick: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTaskClick: (Task) -> Unit,
    taskSectionTitle: String = stringResource(id = R.string.upcoming_tasks),
    taskSectionTitle1: String = stringResource(R.string.completed_tasks),
    sessionSectionTitle: String = stringResource(R.string.recent_study_sessions),
    emptyTaskListText: String = stringResource(id = R.string.empty_task_list_text),
    emptyTaskListText1: String = stringResource(id = R.string.empty_task_list_text1),
    emptySessionListText: String = stringResource(R.string.empty_list_text_session)
) {
    val listState = rememberLazyListState()
    val isFABExpanded = remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var isOpenAddDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var isOpenDeleteDialogSubject by rememberSaveable {
        mutableStateOf(false)
    }
    var isOpenDeleteDialogSession by rememberSaveable {
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

                SnackBarEvent.NavigateUp -> {
                    onBackButtonClick()
                }
            }
        }
    }
    LaunchedEffect(key1 = state.studiedHours, key2 = state.goalStudyHours) {
        onEvent(SubjectEvents.UpdateProgress)
    }
    AddSubjectDialogBox(
        isOpen = isOpenAddDialog,
        onDismissRequest = { isOpenAddDialog = false },
        onConfirmButtonClick = {
            onEvent(SubjectEvents.UpdateSubject)
            isOpenAddDialog = false
        },
        selectedColors = state.subjectCardColor,
        onColorChange = { onEvent(SubjectEvents.OnSubjectColorChange(it)) },
        subjectName = state.subjectName,
        goalStudyHours = state.goalStudyHours,
        onSubjectNameChange = { onEvent(SubjectEvents.OnSubjectNameChange(it)) },
        onGoalStudyHourChange = { onEvent(SubjectEvents.OnGoalStudyHoursChange(it)) }
    )
    DeleteDialogBox(
        isOpen = isOpenDeleteDialogSubject,
        title = stringResource(id = R.string.delete_subject),
        bodyText = stringResource(id = R.string.body_text_of_delete_dialog2),
        onDismissRequest = { isOpenDeleteDialogSubject = false },
        onConfirmButtonClick = {
            onEvent(SubjectEvents.DeleteSubject)
            isOpenDeleteDialogSubject = false
            onBackButtonClick()
        }
    )
    DeleteDialogBox(
        isOpen = isOpenDeleteDialogSession,
        title = stringResource(id = R.string.delete_session),
        bodyText = stringResource(id = R.string.body_text_of_delete_dialog),
        onDismissRequest = { isOpenDeleteDialogSession = false },
        onConfirmButtonClick = {
            onEvent(SubjectEvents.DeleteSession)
            isOpenDeleteDialogSession = false
        }
    )
    Scaffold(
        snackbarHost = {
                       SnackbarHost(hostState = snackBarHostState)
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                title = state.subjectName,
                onBackButtonClick = onBackButtonClick,
                onEditButtonClick = { isOpenAddDialog = true },
                onDeleteButtonClick = { isOpenDeleteDialogSubject = true },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddTaskClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_task)
                )
                if (isFABExpanded.value)
                    Text(
                        text = stringResource(id = R.string.add_task)
                    )
            }
        }
    ) { it ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                SubjectOverviewSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    studyHours = state.studiedHours.toString(),
                    goalHours = state.goalStudyHours,
                    progress = state.progress
                )
            }
            tasksList(
                sectionTitle = taskSectionTitle,
                taskList = state.upcomingTasks,
                emptyListText = emptyTaskListText,
                onCheckBoxClick = { task ->
                    onEvent(SubjectEvents.OnTaskIsCompleteChange(task))
                },
                onTaskCardClick = onTaskClick
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            tasksList(
                sectionTitle = taskSectionTitle1,
                taskList = state.completedTasks,
                emptyListText = emptyTaskListText1,
                onCheckBoxClick = { task ->
                    onEvent(SubjectEvents.OnTaskIsCompleteChange(task))
                },
                onTaskCardClick = onTaskClick
            )
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            StudySessionList(
                sectionTitle = sessionSectionTitle,
                emptyListText = emptySessionListText,
                sessionList = state.recentSession,
                onDeleteIconClick = {
                    onEvent(SubjectEvents.OnDeleteSessionClick(it))
                    isOpenDeleteDialogSession = true }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopBar(
    title: String,
    onBackButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    LargeTopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onBackButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.arrow_back_nav)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(
                onClick = onDeleteButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.arrow_back_nav)
                )
            }
            IconButton(
                onClick = onEditButtonClick
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.arrow_back_nav)
                )
            }
        }
    )
}

@Composable
private fun SubjectOverviewSection(
    modifier: Modifier,
    studyHours: String,
    goalHours: String,
    progress: Float,
) {
    val percentageProgress = remember(progress) {
        (progress * 100).toInt().coerceIn(0, 100)
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCard(
            modifier = Modifier
                .weight(1f),
            headingText = stringResource(id = R.string.studied_hours),
            count = studyHours
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        CountCard(
            modifier = Modifier
                .weight(1f),
            headingText = stringResource(id = R.string.goal_study_hours),
            count = goalHours
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 0.5f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(text = percentageProgress.toString())
        }
    }
}
