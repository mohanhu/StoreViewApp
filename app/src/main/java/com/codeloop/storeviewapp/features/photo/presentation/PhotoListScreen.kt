package com.codeloop.storeviewapp.features.photo.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.codeloop.storeviewapp.features.utils.view.SwipeRefreshAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoListScreen(
        modifier: Modifier = Modifier,
        title: String,
        uiState: State<PhotoListUiState>,
        accept : (PhotoListUiAction) -> Unit,
        onBackClick: () -> Unit
) {

    val context = LocalContext.current

    var isRefreshing by remember { mutableStateOf(false) }
    var showSheet by remember { mutableStateOf(-1) }

    val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val permissionGranted : () -> Boolean = {
        permissionList.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted.invoke()) {
            onBackClick()
        }
    }

    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing){
            launch {
                snackBarHostState.showSnackbar("Latest images fetching...!", duration = SnackbarDuration.Short)
            }
            delay(2000)
            isRefreshing = false
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar (
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                title = { Text(text = "Images")},
                modifier= Modifier.fillMaxWidth(),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.padding(start =4.dp).clickable {
                            onBackClick()
                        }
                    )
                }
            )
        }
    ){
        Column(
            modifier = modifier.fillMaxWidth().padding(it) ,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            val viewmodel  : PreviewVIewModel = hiltViewModel()
            if (showSheet >= 0){
                PreviewPagerScreen(
                    context = context,
                    viewmodel = viewmodel,
                    currentIndex = showSheet,
                    mediaFile = uiState.value.mediaFile.filterIsInstance<PhotoListUiModel.ImageList>().flatMap { it.mediaFile },
                ) {
                    showSheet = -1
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.value.mediaFile.isNotEmpty()) {
                    SwipeRefreshAction(
                        onRefresh = {
                            isRefreshing = true
                        },
                        isRefreshing = isRefreshing,
                        content = {
                            LazyColumn (
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                itemsIndexed(uiState.value.mediaFile) { index, rowItems ->
                                    when (rowItems) {
                                        is PhotoListUiModel.Header -> {
                                            Row(
                                                modifier = Modifier.fillMaxWidth()
                                                    .padding(vertical = 12.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Start
                                            ) {
                                                Text(
                                                    text = rowItems.message,
                                                    style = MaterialTheme.typography.titleMedium,
                                                )
                                            }
                                        }

                                        is PhotoListUiModel.ImageList -> {
                                            val chunk = rowItems.mediaFile.chunked(3)
                                            chunk.forEachIndexed { rowIndex, rowItems ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    rowItems.forEachIndexed { columnIndex, mediaFile ->
                                                        ImageCard(
                                                            context = context,
                                                            mediaFile = mediaFile,
                                                            modifier = Modifier
                                                                .aspectRatio(1f)
                                                                .weight(1f),
                                                            onImageClick = {
                                                                val files = uiState.value.mediaFile.filterIsInstance<PhotoListUiModel.ImageList>().flatMap { it.mediaFile }
                                                                val index = files.indexOf(it)
                                                                showSheet = index
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
                else{
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No Images",
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
            }
        }
    }
}