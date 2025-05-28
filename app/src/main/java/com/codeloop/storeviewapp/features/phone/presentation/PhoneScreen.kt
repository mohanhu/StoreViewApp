package com.codeloop.storeviewapp.features.phone.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.codeloop.storeviewapp.features.utils.permissions.rememberPermissions
import com.codeloop.storeviewapp.features.utils.view.AlertDialogs
import com.codeloop.storeviewapp.features.utils.view.SwipeRefreshAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun PhoneScreen(
    title: String,
    modifier: Modifier = Modifier,
    uiState : State<PhoneUiState>,
    accept : (PhoneUiAction) -> Unit,
) {

    val context = LocalContext.current
    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    val permissionList =  arrayOf(Manifest.permission.READ_CONTACTS)

    val permissionGranted : () -> Boolean = {
        permissionList.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    val cameraPermissionState = rememberPermissions(
        context = context,
        onPermissionGranted = {
            accept.invoke(PhoneUiAction.PermissionGranted)
        },
        onShowSettingsDialog = {
            showPermissionDialog = true
        },
        onRetry = {
            Toast.makeText(context, "Retry", Toast.LENGTH_SHORT).show()
        }
    )

    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing){
            launch {
                snackBarHostState.showSnackbar("Latest Numbers fetching...!", duration = SnackbarDuration.Short)
            }
            accept.invoke(PhoneUiAction.FetchPhoneList)
            delay(2000)
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        if (permissionGranted.invoke()) {
            accept.invoke(PhoneUiAction.PermissionGranted)
        }
    }

    Scaffold(
        snackbarHost = {SnackbarHost(hostState = snackBarHostState) }
    ) {
        Box(
            modifier = modifier.fillMaxSize().padding(it),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.value.phoneList.isNotEmpty()) {
                SwipeRefreshAction(
                    onRefresh = {
                        isRefreshing = true
                    },
                    isRefreshing = isRefreshing,
                    content = {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            items(uiState.value.phoneList) { phoneList ->
                                ContactUserCard(
                                    context = context,
                                    phoneList = phoneList,
                                    dialCall = {
                                        val intent = Intent(Intent.ACTION_DIAL)
                                        intent.data = Uri.parse("tel:${phoneList.number}")
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .clickable {
                            if (permissionGranted()) {
                                accept.invoke(PhoneUiAction.PermissionGranted)
                            } else {
                                cameraPermissionState.launch(permissionList)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Fetch Contacts",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(16.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(54.dp),
                        trackColor = Color.Cyan,
                        color = Color.Green,
                        strokeWidth = 2.dp
                    )
                }
            }

            if (showPermissionDialog) {
                AlertDialogs(
                    context = context,
                    title = "Permission Required",
                    message = "You have denied the permission. Please enable it in the app settings.",
                    positiveButtonText = "Go to Settings",
                    negativeButtonText = "Cancel",
                    onDismiss = {
                        showPermissionDialog = false
                    },
                    onPermissionGranted = {
                        showPermissionDialog = false
                        val intent =
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                        context.startActivity(intent)
                    },
                    onNegativeClick = {
                        showPermissionDialog = false
                    }
                )
            }
        }
    }
}