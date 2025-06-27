package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun ListHorizontalView(
    context: Context,
    mediaFile: List<MediaFile> = listOf(),
) {

    val photoListState = rememberLazyListState()
    val lazyListState = rememberLazyListState()
    val snapFlingBehavior = rememberSnapperFlingBehavior(lazyListState = lazyListState)

    LaunchedEffect(Unit) {
        snapshotFlow { photoListState.isScrollInProgress }
            .collectLatest { isScrolling ->
                if (!isScrolling) {

                }
            }
    }


    LazyRow (
    state = photoListState,
//                flingBehavior = snapFlingBehavior,
    verticalAlignment = Alignment.CenterVertically
    ){
        itemsIndexed(mediaFile.take(10)) { index, item ->
            Box(
                modifier = Modifier
                    .size(
                        if (index%2==0){
                            124.dp
                        }
                        else{
                            124.dp
                        }
                    )
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,

                ) {
                val request = if (item.mediaFileType== MediaFileType.Image){
                    ImageRequest.Builder(context)
                        .data(item.uri)
                }
                else{
                    ImageRequest.Builder(context)
                        .data(item.uri)
                        .videoFrameMillis(10000)
                        .decoderFactory { result, options, _ ->
                            VideoFrameDecoder(
                                result.source,
                                options
                            )
                        }
                }
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = request
                        .crossfade(true)
                        .build(),
                    contentDescription = "Load",
                    contentScale = ContentScale.Crop,
                    error = {
                        Icon(Icons.Default.Warning, contentDescription = "error",tint = Color.Red)
                    }
                )
            }
        }
    }
}