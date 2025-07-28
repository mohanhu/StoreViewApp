package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
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
import com.codeloop.storeviewapp.R
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import dev.chrisbanes.snapper.ExperimentalSnapperApi
import dev.chrisbanes.snapper.rememberSnapperFlingBehavior

@OptIn(ExperimentalSnapperApi::class)
@Composable
fun ListHorizontalView(
    context: Context,
    mediaFile: List<MediaFile> = listOf(),
) {
    if (mediaFile.isEmpty()) return

    val photoListState = rememberLazyListState()
    val snapperFling = rememberSnapperFlingBehavior(lazyListState = photoListState)

    LazyRow(
        state = photoListState,
        flingBehavior = snapperFling,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        itemsIndexed(mediaFile.take(10), key = { _, item -> item.id }) { _, item ->
            val request = remember(item.uri) {
                val builder = ImageRequest.Builder(context).data(item.uri)
                if (item.mediaFileType == MediaFileType.Video) {
                    builder
                        .videoFrameMillis(10000)
                        .decoderFactory { result, options, _ ->
                            VideoFrameDecoder(result.source, options)
                        }
                }
                builder
                    .crossfade(true)
                    .placeholder(R.drawable.folder_thumb_nail)
                    .error(R.drawable.folder_thumb_nail)
                    .build()
            }

            Box(
                modifier = Modifier
                    .size(124.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = request,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    error = {
                        Icon(Icons.Default.Warning, contentDescription = "error", tint = Color.Red)
                    }
                )
            }
        }
    }
}
