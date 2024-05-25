package com.example.smartstudy.presentation.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.smartstudy.R
import com.example.smartstudy.domain.model.Subject
import kotlin.math.truncate

@Composable
fun AddSubjectDialogBox(
    isOpen: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    title: String = stringResource(R.string.add_update_subject),
    subjectNameRes: String = stringResource(id = R.string.subject_name),
    goalStudyHoursRes: String = stringResource(id = R.string.goal_study_hours),
    selectedColors: List<Color>,
    onColorChange: (List<Color>) -> Unit,
    subjectName: String,
    goalStudyHours: String,
    onSubjectNameChange: (String) -> Unit,
    onGoalStudyHourChange: (String) -> Unit
    ){
    var subjectNameError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    subjectNameError = when {
        subjectName.isBlank() -> stringResource(R.string.please_enter_subject_name)
        subjectName.length < 3 -> stringResource(R.string.subject_name_is_too_short)
        subjectName.length > 20 -> stringResource(R.string.subject_name_is_too_long)
        else -> null
    }
    var goalStudyHoursError by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    goalStudyHoursError = when {
        goalStudyHours.isBlank() -> stringResource(R.string.please_enter_goal_study_hours)
        goalStudyHours.toFloatOrNull() == null -> stringResource(R.string.invalid_number)
        goalStudyHours.toFloat() < 1f -> stringResource(R.string.please_set_at_least_1_hour)
        goalStudyHours.toFloat() > 1000f -> stringResource(R.string.please_set_a_maximum_of_1000_hours)
        else -> null
    }
    if (isOpen)
    AlertDialog(
        title = {
            Text(
                text = title
            )
        },
        text = {
               Column {
                   Row (
                       modifier = Modifier
                           .fillMaxWidth()
                           .padding(bottom = 16.dp),
                       horizontalArrangement = Arrangement.SpaceAround
                   ){
                       Subject.subjectColors.forEach {colors ->
                           Box (
                               modifier = Modifier
                                   .size(24.dp)
                                   .clip(CircleShape)
                                   .border(
                                       width = 1.dp,
                                       color = if (colors == selectedColors) Color.Black else Color.Transparent,
                                       shape = CircleShape
                                   )
                                   .background(brush = Brush.verticalGradient(colors))
                                   .clickable {
                                       onColorChange(colors)
                                   }
                           )
                       }
                   }
                   OutlinedTextField(
                       label = {
                               Text(
                                   text = subjectNameRes
                               )
                       },
                       singleLine = true,
                       value = subjectName,
                       onValueChange = onSubjectNameChange,
                       isError = subjectNameError != null && subjectName.isNotBlank(),
                       supportingText = {
                           Text(
                               text = subjectNameError.orEmpty())
                       }
                   )
                   Spacer(
                       modifier = Modifier.height(10.dp)
                   )
                   OutlinedTextField(
                       label = {
                           Text(
                               text = goalStudyHoursRes
                           )
                       },
                       singleLine = true,
                       value = goalStudyHours,
                       onValueChange = onGoalStudyHourChange,
                       keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                       isError = goalStudyHoursError != null && goalStudyHours.isNotBlank(),
                       supportingText = {
                           Text(
                               text = goalStudyHoursError.orEmpty()
                           )
                       }
                   )
               }
        },
        onDismissRequest = onDismissRequest,
        confirmButton = {
                        TextButton(
                            onClick = onConfirmButtonClick,
                            enabled = subjectNameError == null && goalStudyHoursError == null
                        ) {
                            Text(
                                text = stringResource(R.string.save)
                            )
                        }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(
                    text = stringResource(R.string.cancel)
                )
            }
        }
    )
}