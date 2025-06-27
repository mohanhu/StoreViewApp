package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicVideo
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.codeloop.storeviewapp.R
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile

@Composable
fun ImageCard(
    context: Context,
    modifier: Modifier = Modifier,
    mediaFile : MediaFile,
    onImageClick : (MediaFile) -> Unit
) {
    Card(
        modifier = modifier
            .size(width = 100.dp, height = 100.dp)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(1.dp)
            ),
        onClick = {
            onImageClick.invoke(mediaFile)
        },
        shape = RoundedCornerShape(1.dp)
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(context).data(
                mediaFile.uri
            ).crossfade(true)
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
}


@Composable
fun VideoThumbNailCard(
    context: Context,
    mediaFile: MediaFile,
    modifier: Modifier = Modifier,
    onImageClick: (MediaFile) -> Unit
) {
    Card(
        modifier = modifier
            .size(width = 100.dp, height = 100.dp)
            .border(
                width = 1.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(1.dp)
            ),
        onClick = {
            onImageClick.invoke(mediaFile)
        },
        shape = RoundedCornerShape(1.dp)
    ) {
        SubcomposeAsyncImage(
            model =  ImageRequest.Builder(context)
                .data(data = mediaFile.uri)
                .videoFrameMillis(10000)
                .decoderFactory { result, options, _ ->
                    VideoFrameDecoder(
                        result.source,
                        options
                    )
                }
                .build(),
            contentDescription = "Video Thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize() ,
            error = {
                Icon(Icons.Default.Warning, contentDescription = "error",tint = Color.Red)
            }
        )
    }
}


@Composable
fun MusicCard(
    context: Context,
    mediaFile: MediaFile,
    onMusicClick: (MediaFile) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().height(80.dp)
            .padding(4.dp)
            .border(
                width = 1.dp,
                color = Color.Cyan,
                shape = RoundedCornerShape(8.dp)
            ),
        onClick = {
            onMusicClick.invoke(mediaFile)
        }
    ) {

        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(10.dp)
        ) {

            Box(
                modifier = Modifier.size(50.dp)
                    .clip(CircleShape)
                    .background(Color.Cyan),
                contentAlignment = Alignment.Center
            ){
                Icon(
                    Icons.Default.MusicVideo,
                    contentDescription = "Music",
                    modifier = Modifier.size(30.dp)
                )
            }

            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ){
                Text(
                    text = mediaFile.name,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = mediaFile.uri.path.toString(),
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

    HorizontalDivider(modifier = Modifier.height(1.dp).padding(vertical = 5.dp), color = Color.Gray)
}




























