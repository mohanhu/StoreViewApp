package com.codeloop.storeviewapp.features.utils.view

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AlertDialogs(
    context: Context,
    title: String = "",
    message: String = "",
    positiveButtonText: String = "",
    negativeButtonText: String = "",
    onDismiss: () -> Unit,
    onPermissionGranted: () -> Unit,
    onNegativeClick: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = {
                    onPermissionGranted.invoke()
                }
            ) {
                Text(text =  positiveButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onNegativeClick
            ) {
                Text(text = negativeButtonText)
            }
        }
    )

}