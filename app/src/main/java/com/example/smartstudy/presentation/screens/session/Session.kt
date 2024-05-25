package com.example.smartstudy.presentation.screens.session

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartstudy.R
import com.example.smartstudy.presentation.components.DeleteDialogBox
import com.example.smartstudy.presentation.components.StudySessionList
import com.example.smartstudy.presentation.components.SubjectsBottomSheet
import com.example.smartstudy.utils.Constants.ACTION_SERVICE_CANCEL
import com.example.smartstudy.utils.Constants.ACTION_SERVICE_START
import com.example.smartstudy.utils.Constants.ACTION_SERVICE_STOP
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit

@Destination(
    deepLinks = [
        DeepLink(
            action = Intent.ACTION_VIEW,
            uriPattern = "smart_study://dashboard/session"
        )
    ]
)
@Composable
fun SessionScreenRoute(
    navigator: DestinationsNavigator,
    timerService: SessionTimerService
) {
    val viewModel: SessionViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    Session(
        states = state,
        onEvent = viewModel::onEvent,
        timerService,
        onBackButtonClick = {
            navigator.navigateUp()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Session(
    states: SessionStates,
    onEvent: (SessionEvents) -> Unit,
    timerService: SessionTimerService,
    onBackButtonClick: () -> Unit,
    emptyListText: String = stringResource(id = R.string.empty_list_text_session)
) {
    val context = LocalContext.current

    var isBottomSheetOpen by remember {
        mutableStateOf(false)
    }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var isDeleteDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    val hours by timerService.hours
    val minutes by timerService.minutes
    val seconds by timerService.seconds
    val currentTimerState by timerService.currentTimerState
    SubjectsBottomSheet(
        sheetState = sheetState,
        isOpen = isBottomSheetOpen,
        subject = states.subjects,
        onSubjectClick = { subject ->
            scope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) isBottomSheetOpen = false
            }
            onEvent(SessionEvents.OnRelatedSubjectChange(subject))
        },
        onDismissRequest = {
            isBottomSheetOpen = false
        }
    )
    DeleteDialogBox(
        isOpen = isDeleteDialogOpen,
        title = stringResource(R.string.delete_task),
        bodyText = stringResource(R.string.body_text_of_delete_dialog1),
        onDismissRequest = { isDeleteDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(SessionEvents.DeleteSession)
            isDeleteDialogOpen = false
        }
    )
    Scaffold(
        topBar = {
            SessionScreenTopBar(
                onBackButtonClick = onBackButtonClick
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            item {
                TimerSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    hours,
                    minutes,
                    seconds
                )
            }
            item {
                RelatedToSubjectSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    relatedToSubject = states.relatedToSubject ?: "",
                    selectSubjectButtonClick = {
                        isBottomSheetOpen = true
                    },
                    seconds = seconds
                )
            }
            item {
                ButtonSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    startButtonClick = {
                        if (states.subjectId != null)
                            ServiceHelper.triggerForegroundService(
                                context,
                                action = if (currentTimerState == TimerState.STARTED) ACTION_SERVICE_STOP else ACTION_SERVICE_START
                            )
                        else
                            onEvent(SessionEvents.NotifyToUpdateSubject)
                    },
                    cancelButtonClick = {
                        ServiceHelper.triggerForegroundService(
                            context,
                            ACTION_SERVICE_CANCEL
                        )
                    },
                    finishButtonClick = {
                        val duration = timerService.duration.toLong(DurationUnit.SECONDS)
                        if (duration >= 36)
                            ServiceHelper.triggerForegroundService(
                                context,
                                ACTION_SERVICE_CANCEL
                            )
                        onEvent(SessionEvents.SaveSession(duration))
                    },
                    currentTimerState,
                    seconds
                )
            }
            StudySessionList(
                sectionTitle = "Recent Study Sessions",
                emptyListText = emptyListText,
                sessionList = states.sessions,
                onDeleteIconClick = {
                    onEvent(SessionEvents.OnDeleteSession(it))
                    isDeleteDialogOpen = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionScreenTopBar(
    onBackButtonClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.study_session),
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
        }
    )
}

@Composable
private fun TimerSection(
    modifier: Modifier,
    hours: String,
    minutes: String,
    seconds: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(250.dp)
                .border(
                    5.dp,
                    MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Row {
                AnimatedContent(
                    targetState = hours,
                    label = hours,
                    transitionSpec = {
                        timerTextAnimation()
                    }
                ) {
                    Text(
                        text = "$it:",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                    )
                }
                AnimatedContent(
                    targetState = minutes,
                    label = minutes,
                    transitionSpec = {
                        timerTextAnimation()
                    }
                ) {
                    Text(
                        text = "$it:",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                    )
                }
                AnimatedContent(
                    targetState = seconds,
                    label = seconds,
                    transitionSpec = {
                        timerTextAnimation()
                    }
                ) {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 45.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RelatedToSubjectSection(
    modifier: Modifier,
    relatedToSubject: String,
    seconds: String,
    selectSubjectButtonClick: () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.related_to_subject),
            style = MaterialTheme.typography.bodySmall
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = relatedToSubject,
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(
                onClick = selectSubjectButtonClick,
                enabled = seconds == "00"
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = stringResource(R.string.select_subject)
                )
            }
        }
    }
}

@Composable
private fun ButtonSection(
    modifier: Modifier,
    startButtonClick: () -> Unit,
    cancelButtonClick: () -> Unit,
    finishButtonClick: () -> Unit,
    timerState: TimerState,
    seconds: String,
    textModifier: Modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = startButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = when (timerState) {
                    TimerState.STARTED -> MaterialTheme.colorScheme.error
                    TimerState.STOPPED -> MaterialTheme.colorScheme.primary
                    TimerState.IDLE -> MaterialTheme.colorScheme.primary
                }
            )
        ) {
            Text(
                modifier = textModifier,
                text = when (timerState) {
                    TimerState.STARTED -> stringResource(R.string.stop)
                    TimerState.STOPPED -> stringResource(R.string.resume)
                    TimerState.IDLE -> stringResource(R.string.start)
                }
            )
        }
        Button(
            onClick = cancelButtonClick,
            enabled = timerState != TimerState.STARTED && seconds != "00"
        ) {
            Text(
                modifier = textModifier,
                text = stringResource(R.string.cancel)
            )
        }
        Button(
            onClick = finishButtonClick,
            enabled = timerState != TimerState.STARTED && seconds != "00"
        ) {
            Text(
                modifier = textModifier,
                text = stringResource(R.string.finish)
            )
        }
    }
}

private fun timerTextAnimation(duration: Int = 600): ContentTransform {
    return slideInVertically(tween(duration)) { it } +
            fadeIn(tween(duration)) togetherWith
            slideOutVertically(tween(duration)) { -it } +
            fadeOut(tween(duration))
}