package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.SubcomposeAsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.codeloop.storeviewapp.R
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType

@Composable
fun CircleMergeImageCard(
    context: Context,
    maxLimit: Int = 4,
    mediaFileType: MediaFileType = MediaFileType.Image,
    images: List<String> = listOf()
) {
    Box(
    modifier = Modifier,
    contentAlignment = Alignment.Center
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(space = (-10).dp, alignment = Alignment.End),
            contentPadding = PaddingValues(0.dp),
            reverseLayout = false
        ) {
            itemsIndexed(images.take(maxLimit)) { index, item ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (index<maxLimit-1){
                        val request = if (mediaFileType== MediaFileType.Image){
                            ImageRequest.Builder(context)
                                .data(item)
                        }
                        else{
                            ImageRequest.Builder(context)
                                .data(item)
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
                            .crossfade(10)
                            .placeholder(R.drawable.folder_thumb_nail)
                            .error(R.drawable.folder_thumb_nail)
                            .build(),
                            contentDescription = "Load",
                            contentScale = ContentScale.Crop,
                            error = {
                                Icon(Icons.Default.Warning, contentDescription = "error",tint = Color.Red)
                            }
                        )
                    }
                    else{
                        val count = ((images.size-maxLimit)+1).takeIf { it < 99 }?:99
                        Text("+$count", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}