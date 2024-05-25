package com.example.smartstudy.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.smartstudy.R

@Composable
fun DeleteDialogBox(
    isOpen: Boolean,
    title: String,
    bodyText: String ,
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
){
    if (isOpen)
        AlertDialog(
            title = {
                Text(
                    text = title
                )
            },
            text = {
                Text(
                    text = bodyText
                )
            },
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = onConfirmButtonClick,
                ) {
                    Text(
                        text = stringResource(R.string.delete)
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