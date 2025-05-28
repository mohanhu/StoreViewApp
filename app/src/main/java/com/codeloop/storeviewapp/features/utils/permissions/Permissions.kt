package com.codeloop.storeviewapp.features.utils.permissions

import android.app.Activity
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat

@Composable
fun rememberPermissions(
    context: Context = LocalContext.current,
    onPermissionGranted: () -> Unit,
    onShowSettingsDialog: () -> Unit,
    onRetry: () -> Unit
): ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>> {

    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            val isGranted = result.all { it.value }
            if (isGranted) {
                onPermissionGranted.invoke()
            }

            result.forEach { permission, grant ->
                if (!grant) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            context as Activity, permission
                        )
                    ) {
                        onShowSettingsDialog.invoke()
                    } else {
                        onRetry.invoke()
                    }
                }
            }
        }
    )
}