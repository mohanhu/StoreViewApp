package com.codeloop.storeviewapp.features.music.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile

@Composable
fun MusicCardAnim(
    modifier: Modifier = Modifier,
    mediaFile: MediaFile
) {

    val context = LocalContext.current

    Column(
        modifier = modifier.fillMaxSize().padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val uri = remember {  getUri(context, mediaFile.uri) }

        AnimationTextForward(
            modifier = Modifier.fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = mediaFile.name,
                color = Color.White,
                maxLines = 1,
            )
        }

        Card (
            modifier = Modifier.weight(1f).padding(vertical = 12.dp, horizontal = 20.dp).align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = BorderStroke(2.dp, color = Color("#431965".toColorInt()))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = ImageRequest.Builder(context).data(
                        uri
                    ).crossfade(true)
                        .build(),
                    contentDescription = "Load",
                    contentScale = ContentScale.Crop,
                    error = {
                        AnimationRotation {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = "Play_Music",
                                modifier = Modifier.fillMaxSize().padding(26.dp),
                                tint = Color.White,
                            )
                        }
                    }
                )
            }
        }
    }
}

private fun getUri(context: Context, uri: Uri): Bitmap? {
    return try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, uri)
        val bitmap = retriever.embeddedPicture
        bitmap?.let {
            BitmapFactory.decodeByteArray(it, 0, it.size)
        }
    } catch (e: Exception) {
        null
    }
}