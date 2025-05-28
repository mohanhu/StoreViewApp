package com.codeloop.storeviewapp.features.music.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile

@Composable
fun MusicCardAnim(
    mediaFile: MediaFile
) {

    val context = LocalContext.current

    Box(
    modifier = Modifier.fillMaxSize(),
    contentAlignment = Alignment.Center
    ) {

        AnimationTextForward(
            modifier = Modifier.align(Alignment.TopCenter)
                .padding(20.dp)
        ) {
            Text(
                text = mediaFile.name,
                color = Color.White,
                maxLines = 1,
            )
        }

        Card (
            modifier = Modifier.size(200.dp),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent,
                contentColor = Color.White
            ),
            border = BorderStroke(2.dp, color = Color.Cyan)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF4A00E0), Color(0xFF00C9FF))
                        )
                    )
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    modifier = Modifier.size(100.dp),
                    model = ImageRequest.Builder(context).data(
                        mediaFile.uri
                    ).crossfade(true)
                        .build(),
                    contentDescription = "Load",
                    contentScale = ContentScale.Crop,
                    error = {
                        AnimationRotation {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = "Play_Music",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.White,
                            )
                        }
                    }
                )
            }
        }
    }
}