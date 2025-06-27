package com.codeloop.storeviewapp.features.video.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.SubcomposeAsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.codeloop.storeviewapp.features.photo.presentation.Folder2ListItem
import com.codeloop.storeviewapp.features.utils.permissions.rememberPermissions
import com.codeloop.storeviewapp.features.utils.view.AlertDialogs
import com.codeloop.storeviewapp.features.utils.view.SwipeRefreshAction
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VideoScreen(
    modifier: Modifier = Modifier,
    title: String,
    uiState: State<VideoUiState>,
    accept: (VideoUiAction) -> Unit,
    onItemClick : (String) -> Unit = {}
) {

    val context = LocalContext.current

    var player = remember { ExoPlayer.Builder(context).build() }
    val isPlay = remember { mutableStateOf(player.isPlaying) }
    val isAudio  = remember { mutableStateOf(player.volume != 0f) }

    var lifecycle : Lifecycle.Event by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner  = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            playerRelease(player)
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var showPermissionDialog by rememberSaveable { mutableStateOf(false) }

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

    val cameraPermissionState = rememberPermissions(
        context = context,
        onPermissionGranted = {
            accept.invoke(VideoUiAction.PermissionGranted)
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
                playerRelease(player)
                permissionGranted.invoke()
                snackBarHostState.showSnackbar("Latest videos fetching...!", duration = SnackbarDuration.Short)
            }
            delay(2000)
            isRefreshing = false
        }
    }

    LaunchedEffect(Unit) {
        if (permissionGranted.invoke()) {
            accept.invoke(VideoUiAction.PermissionGranted)
        }
    }

    Scaffold (
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) {
        Box(
            modifier = modifier.fillMaxSize().padding(it),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.value.mediaFileFolders.isNotEmpty()) {

                val mediaList = remember { uiState.value.mediaFileFolders.flatMap { it.mediaFile } }
                val videoItem =  mediaList.randomOrNull()

                videoItem?.let {
                    LaunchedEffect(it) {
                        setMediaItem(player, it.uri)
                    }
                }
                LaunchedEffect(player) {
                    player.addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            isPlay.value = isPlaying
                        }
                    })
                }

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
                            item {
                                Row(
                                    modifier = Modifier.height(400.dp)
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Card(
                                        modifier = Modifier
                                            .weight(1.8f),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(2.dp, Color.LightGray)
                                    ) {
                                        Box(modifier = Modifier.fillMaxSize()) {

                                            SubcomposeAsyncImage(
                                                modifier = Modifier.fillMaxSize(),
                                                model = ImageRequest.Builder(context)
                                                    .data(videoItem?.uri)
                                                    .videoFrameMillis(1000)
                                                    .decoderFactory { result, options, _ ->
                                                        VideoFrameDecoder(
                                                            result.source,
                                                            options
                                                        )
                                                    }
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Load",
                                                contentScale = ContentScale.Crop,
                                                error = {
                                                    Icon(
                                                        Icons.Default.Warning,
                                                        contentDescription = "error",
                                                        tint = Color.Red
                                                    )
                                                }
                                            )
                                            AndroidView(
                                                factory = {
                                                    PlayerView(it).apply {
                                                        useController = false
                                                        this.player = player
                                                    }
                                                },
                                                update = {
                                                    it.player = player
                                                    println("VideoScreen lifecycle : update $lifecycle")
                                                    when(lifecycle){
                                                        Lifecycle.Event.ON_RESUME -> {
                                                            it.onResume()
                                                            it.player?.play()
                                                        }
                                                        Lifecycle.Event.ON_PAUSE -> {
                                                            it.onPause()
                                                            it.player?.pause()
                                                        }
                                                        else -> {}
                                                    }
                                                },
                                                modifier = Modifier.fillMaxSize()
                                            )

                                            Column(
                                                modifier = Modifier
                                                    .padding(vertical = 12.dp)
                                                    .align(Alignment.BottomEnd),
                                                verticalArrangement = Arrangement.Center,
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        println("AnimatedContent IconButton ${isAudio.value}")
                                                        isAudio.value = !isAudio.value
                                                        player.volume = if (isAudio.value) 1f else 0f
                                                    },
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .padding(vertical = 8.dp)
                                                        .clip(CircleShape),
                                                ) {
                                                    AnimatedContent(
                                                        targetState = isAudio.value,
                                                        label = "isPlaying",
                                                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                                                    ) { isAudio ->
                                                        Icon(
                                                            modifier = Modifier.fillMaxSize(),
                                                            imageVector =  if (isAudio) Icons.Default.VolumeUp else Icons.Default.VolumeOff ,
                                                            contentDescription = "Audio",
                                                            tint = Color.White,
                                                        )
                                                    }
                                                }

                                                IconButton(
                                                    onClick = {
                                                        if (isPlay.value) player.pause() else player.play()
                                                    },
                                                    modifier = Modifier
                                                        .size(50.dp)
                                                        .padding(vertical = 8.dp)
                                                        .clip(CircleShape),
                                                ) {
                                                    AnimatedContent(
                                                        targetState = isPlay.value,
                                                        label = "isPlaying",
                                                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                                                    ) { isPlaying ->
                                                        Icon(
                                                            modifier = Modifier.fillMaxSize(),
                                                            imageVector =  if (isPlaying) Icons.Default.PauseCircleOutline else Icons.Default.PlayCircleOutline ,
                                                            contentDescription = "Play",
                                                            tint = Color.White,
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.size(4.dp))
                                    Column(
                                        modifier = Modifier.weight(1f),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        LazyColumn {
                                            items(mediaList.shuffled().take(3)) {
                                                SubcomposeAsyncImage(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(140.dp)
                                                        .padding(bottom = 4.dp)
                                                        .clip(RoundedCornerShape(12.dp))
                                                        .border(2.dp, color = Color.LightGray,RoundedCornerShape(12.dp)
                                                        ),
                                                    model = ImageRequest.Builder(context)
                                                        .data(it.uri)
                                                        .videoFrameMillis(1000)
                                                        .decoderFactory { result, options, _ ->
                                                            VideoFrameDecoder(
                                                                result.source,
                                                                options
                                                            )
                                                        }
                                                        .crossfade(true)
                                                        .build(),
                                                    contentDescription = "Load",
                                                    contentScale = ContentScale.Crop,
                                                    error = {
                                                        Icon(
                                                            Icons.Default.Warning,
                                                            contentDescription = "error",
                                                            tint = Color.Red
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                HorizontalDivider(modifier = Modifier.height(1.dp), color = Color.LightGray)
                            }

                            items(uiState.value.mediaFileFolders) { folder ->
                                Folder2ListItem(
                                    context = context,
                                    folder = folder,
                                    onFolderClick = {
                                        onItemClick.invoke(it.relativePath)
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
                                accept.invoke(VideoUiAction.PermissionGranted)
                            } else {
                                cameraPermissionState.launch(permissionList)
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Fetch Videos",
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

private fun playerRelease(player: Player) {
    player.stop()
    player.clearMediaItems()
}

private fun setMediaItem(player: Player, uri: Uri) {
    println("setMediaItem player >> $uri")
    player.stop()
    player.clearMediaItems()
    player.addMediaItem(MediaItem.fromUri(uri))
    player.setMediaItem(MediaItem.fromUri(uri))
    player.prepare()
    player.playWhenReady = true
    player.repeatMode = Player.REPEAT_MODE_ALL
}
