package com.example.smartstudy.presentation.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.smartstudy.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun TaskDatePicker(
    state: DatePickerState,
    isOpen: Boolean,
    confirmButtonText: String = stringResource(R.string.ok),
    dismissButtonText: String = stringResource(id = R.string.cancel),
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit
) {
    if (isOpen)
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(onClick = onConfirmButtonClick) {
                    Text(text = confirmButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = dismissButtonText)
                }
            }
        ) {
            DatePicker(
                state = state,
                dateValidator = { timeStamp ->
                    val selectedDate = Instant
                        .ofEpochMilli(timeStamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    val currentDate = LocalDate.now(ZoneId.systemDefault())
                    selectedDate >= currentDate
                }
            )
        }
}