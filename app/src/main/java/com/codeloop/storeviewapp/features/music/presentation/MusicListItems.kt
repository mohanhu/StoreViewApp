package com.codeloop.storeviewapp.features.music.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFile

@Composable
fun MusicListItems(
    modifier: Modifier = Modifier,
    context: Context,
    folder: MediaFile,
    onFolderClick: (MediaFile) -> Unit,
) {

    val uri = remember(folder.uri) { getUri(context, folder.uri) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onFolderClick(folder) }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                uri?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Album Art",
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop
                    )
                } ?: Icon(
                    Icons.Default.MusicNote,
                    contentDescription = "Play_Music",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp),
                    tint = Color.White,
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    folder.name, modifier = Modifier.padding(4.dp),
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    folder.relativePath, modifier = Modifier.padding(4.dp),
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        HorizontalDivider(Modifier
            .height(1.dp)
            .padding(vertical = 4.dp))
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
