package com.codeloop.storeviewapp.features.photo.presentation

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import coil.compose.SubcomposeAsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.codeloop.storeviewapp.R
import com.codeloop.storeviewapp.features.music.presentation.AnimationTextForward
import com.codeloop.storeviewapp.features.music.presentation.MusicCardAnim
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import kotlinx.coroutines.delay
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewPagerScreen(
    context: Context,
    viewmodel: PreviewVIewModel,
    currentIndex: Int,
    mediaFile: List<MediaFile>,
    cancelSheetDialog: () -> Unit
) {

    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true, confirmValueChange = { it != SheetValue.PartiallyExpanded })

    val pagerState = rememberPagerState(initialPage = currentIndex) { mediaFile.size }
    val fullScreen = rememberSaveable { mutableStateOf(false) }

    var lifecycle: Lifecycle.Event by remember { mutableStateOf(Lifecycle.Event.ON_CREATE) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycle = event
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            viewmodel.playerRelease()
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(fullScreen.value) {
        (context as Activity)?.apply {
            requestedOrientation = if (fullScreen.value) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            }
        }
    }


    ModalBottomSheet(
        contentWindowInsets = { WindowInsets.safeDrawing },
        onDismissRequest = {
            fullScreen.value = false
            (context as Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            cancelSheetDialog.invoke()
        },
        dragHandle = {

        },
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxSize(),
        sheetState = modalBottomSheetState,
    ){
        HorizontalPager(
            state = pagerState,
            key = { mediaFile[it].id },
        ) { index ->

            when(mediaFile[index].mediaFileType){
                MediaFileType.Image -> {
                    Box(modifier = Modifier.fillMaxSize()){

                        SubcomposeAsyncImage(
                            modifier = Modifier.fillMaxSize(),
                            model = ImageRequest.Builder(context).data(
                                mediaFile[index].uri
                            ).crossfade(true)
                                .crossfade(10)
                                .placeholder(R.drawable.folder_thumb_nail)
                                .error(R.drawable.folder_thumb_nail)
                                .build(),
                            contentDescription = "Load",
                            error = {
                                Icon(Icons.Default.Warning, contentDescription = "error",tint = Color.Red)
                            }
                        )
                        AnimationTextForward(
                            modifier = Modifier.align(Alignment.TopCenter)
                                .padding(20.dp)
                        ) {
                            Text(
                                text = mediaFile[index].name,
                                color = Color.Black,
                                maxLines = 1,
                            )
                        }
                    }
                }
                MediaFileType.Video ->{

                    val isBuffering = remember { mutableStateOf(true) }
                    val hasError = remember { mutableStateOf(false) }
                    val player = viewmodel.player
                    val isPlay = remember { mutableStateOf(player.isPlaying) }
                    val currentDuration = remember { mutableFloatStateOf(0f) }

                    val currentUrl = mediaFile[pagerState.currentPage].uri
                    LaunchedEffect((index == pagerState.currentPage)) {
                        viewmodel.setMediaItem(currentUrl){}
                        while(true) {
                            currentDuration.floatValue = player.currentPosition.toFloat()
                            delay(1000)
                        }
                    }

                    LaunchedEffect(viewmodel.player) {

                        viewmodel.player.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                isBuffering.value = state == Player.STATE_BUFFERING
                            }

                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                isPlay.value = isPlaying
                            }

                            override fun onPositionDiscontinuity(
                                oldPosition: Player.PositionInfo,
                                newPosition: Player.PositionInfo,
                                reason: Int
                            ) {
                                currentDuration.floatValue = viewmodel.player.currentPosition.toFloat()
                                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                            }

                            override fun onPlayerError(error: PlaybackException) {
                                hasError.value = true
                                isBuffering.value = false
                            }
                        })
                    }

                    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                        if (index == pagerState.currentPage) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                SubcomposeAsyncImage(
                                    model =  ImageRequest.Builder(context)
                                        .data(data = currentUrl)
                                        .videoFrameMillis(10000)
                                        .decoderFactory { result, options, _ ->
                                            VideoFrameDecoder(
                                                result.source,
                                                options
                                            )
                                        }
                                        .build(),
                                    contentDescription = "Video Thumbnail",
                                    modifier = Modifier.fillMaxSize() ,
                                    error = {
                                        Icon(Icons.Default.Warning, contentDescription = "error",tint = Color.Red)
                                    }
                                )
                                AndroidView(
                                    factory = {
                                        PlayerView(it).apply {
                                            useController = false
                                            this.player = viewmodel.player
                                        }
                                    },
                                    update = {
                                        when(lifecycle){
                                            Lifecycle.Event.ON_RESUME -> {
                                                it.onResume()
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

                                Box(modifier = Modifier
                                    .matchParentSize()
                                ) {

                                    AnimationTextForward(
                                        modifier = Modifier.align(Alignment.TopCenter)
                                            .padding(20.dp)
                                    ) {
                                        Text(
                                            text = mediaFile[index].name,
                                            color = Color.White,
                                            maxLines = 1,
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            if (isPlay.value) player.pause() else player.play()
                                        },
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(64.dp)
                                    ) {
                                        AnimatedContent(
                                            targetState = isPlay.value,
                                            transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                                            label = "isPlaying"
                                        ) { isPlaying ->
                                            Icon(
                                                imageVector =  if (isPlaying) Icons.Default.PauseCircleOutline else Icons.Default.PlayCircleOutline ,
                                                contentDescription = "Play/Pause",
                                                tint = Color.White,
                                                modifier = Modifier.size(48.dp)
                                            )
                                        }
                                    }


                                    Row(
                                        modifier = Modifier.padding(12.dp).align(Alignment.BottomCenter),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = formatVideoDuration(player.currentPosition,player.duration),
                                            color = Color.White,
                                            modifier = Modifier.padding(8.dp),
                                            textAlign = TextAlign.Center,
                                            fontSize = 10.sp
                                        )

                                        Slider(
                                            value = currentDuration.floatValue,
                                            onValueChange = {
                                                player.seekTo(it.toLong())
                                            },
                                            onValueChangeFinished = {

                                            },
                                            valueRange = 0f..(player.duration.takeIf { it>0 }?:0f).toFloat(),
                                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp).height(2.dp).weight(1f).align(Alignment.CenterVertically),
                                            colors = SliderDefaults.colors(
                                                thumbColor = Color.White,
                                                activeTrackColor = Color.White,
                                                inactiveTrackColor = Color.Gray,
                                                activeTickColor = Color.White,
                                                inactiveTickColor = Color.Gray,
                                                disabledThumbColor = Color.White,
                                            ),
                                            thumb = {
                                                Box(
                                                    modifier = Modifier
                                                        .size(10.dp)
                                                        .background(
                                                            Color.White,
                                                            shape = RoundedCornerShape(5.dp)
                                                        )
                                                )
                                            },
                                        )
                                        IconButton(
                                            onClick = {
                                                fullScreen.value = !fullScreen.value
                                            },
                                            modifier = Modifier
                                        ) {
                                            AnimatedContent(
                                                targetState = fullScreen.value,
                                                label = "fullScreen"
                                            ) { orientation ->
                                                Icon(
                                                    imageVector = if (orientation) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                                    contentDescription = "Fullscreen",
                                                    tint = Color.White
                                                )
                                            }
                                        }
                                    }
                                }

                                if (isBuffering.value || hasError.value) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize() ,
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (hasError.value) {
                                            Text("Playback Error", color = Color.White)
                                        } else {
                                            CircularProgressIndicator(color = Color.Cyan, trackColor = Color.Blue)
                                        }
                                    }
                                }
                            }
                        }
                        else{
                            Box(modifier = Modifier.fillMaxSize())
                        }
                    }
                }

                MediaFileType.Music ->{

                    val isBuffering = remember { mutableStateOf(true) }
                    val hasError = remember { mutableStateOf(false) }
                    val player = viewmodel.player
                    val isPlay = remember { mutableStateOf(player.isPlaying) }
                    val currentDuration = remember { mutableFloatStateOf(0f) }

                    val currentUrl = mediaFile[pagerState.currentPage].uri
                    LaunchedEffect((index == pagerState.currentPage)) {
                        viewmodel.setMediaItem(currentUrl){}
                        while(true) {
                            currentDuration.floatValue = player.currentPosition.toFloat()
                            delay(1000)
                        }
                    }

                    LaunchedEffect(viewmodel.player) {

                        viewmodel.player.addListener(object : Player.Listener {
                            override fun onPlaybackStateChanged(state: Int) {
                                isBuffering.value = state == Player.STATE_BUFFERING
                            }

                            override fun onIsPlayingChanged(isPlaying: Boolean) {
                                isPlay.value = isPlaying
                            }

                            override fun onPositionDiscontinuity(
                                oldPosition: Player.PositionInfo,
                                newPosition: Player.PositionInfo,
                                reason: Int
                            ) {
                                currentDuration.floatValue = viewmodel.player.currentPosition.toFloat()
                                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
                            }

                            override fun onPlayerError(error: PlaybackException) {
                                hasError.value = true
                                isBuffering.value = false
                            }
                        })
                    }

                    if (index == pagerState.currentPage){
                        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                            AndroidView(
                                factory = {
                                    PlayerView(it).apply {
                                        useController = false
                                        this.player = viewmodel.player
                                    }
                                },
                                update = {
                                    when(lifecycle){
                                        Lifecycle.Event.ON_RESUME -> {
                                            it.onResume()
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

                            MusicCardAnim(mediaFile[index])

                            Box(modifier = Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isPlay.value) player.pause() else player.play()
                                    },
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                        .size(64.dp)
                                ) {
                                    AnimatedContent(
                                        targetState = isPlay.value,
                                        transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                                        label = "isPlaying"
                                    ) { isPlaying ->
                                        Icon(
                                            imageVector =  if (isPlaying) Icons.Default.PauseCircleOutline else Icons.Default.PlayCircleOutline ,
                                            contentDescription = "Play/Pause",
                                            tint = Color.White,
                                            modifier = Modifier.size(48.dp)
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.padding(12.dp).align(Alignment.BottomCenter),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = formatVideoDuration(player.currentPosition,player.duration),
                                        color = Color.White,
                                        modifier = Modifier.padding(8.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 10.sp
                                    )

                                    Slider(
                                        value = currentDuration.floatValue,
                                        onValueChange = {
                                            player.seekTo(it.toLong())
                                        },
                                        onValueChangeFinished = {

                                        },
                                        valueRange = 0f..(player.duration.takeIf { it>0 }?:0f).toFloat(),
                                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp).height(2.dp).weight(1f).align(Alignment.CenterVertically),
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color.White,
                                            activeTrackColor = Color.White,
                                            inactiveTrackColor = Color.Gray,
                                            activeTickColor = Color.White,
                                            inactiveTickColor = Color.Gray,
                                            disabledThumbColor = Color.White,
                                        ),
                                        thumb = {
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .background(
                                                        Color.White,
                                                        shape = RoundedCornerShape(5.dp)
                                                    )
                                            )
                                        },
                                    )
                                    IconButton(
                                        onClick = {
                                            fullScreen.value = !fullScreen.value
                                        },
                                        modifier = Modifier
                                    ) {
                                        AnimatedContent(
                                            targetState = fullScreen.value,
                                            label = "fullScreen"
                                        ) { orientation ->
                                            Icon(
                                                imageVector = if (orientation) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                                contentDescription = "Fullscreen",
                                                tint = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            if (isBuffering.value || hasError.value) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (hasError.value) {
                                        Text("Playback Error", color = Color.White)
                                    } else {
                                        CircularProgressIndicator(color = Color.Cyan, trackColor = Color.Blue)
                                    }
                                }
                            }
                        }
                    }
                    else{
                        Box(modifier = Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
}

private fun formatVideoDuration(current: Long,total: Long) : String {
    if (current <0 || total < 0) return "00:00/00:00"
    return "${current.toDuration()}/${total.toDuration()}"
}

private fun Long.toDuration() : String {
    val total  = this/1000
    val hour  = total/3600
    val minutes = total/60
    val second = total%60
    return if (hour>0){
        String.format(Locale.getDefault(),"%02d:%02d:%02d",hour,minutes,second)
    }else{
        String.format(Locale.getDefault(),"%02d:%02d",minutes,second)
    }
}