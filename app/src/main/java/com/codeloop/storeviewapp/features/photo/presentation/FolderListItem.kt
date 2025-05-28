package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codeloop.storeviewapp.R
import com.codeloop.storeviewapp.features.photo.domain.model.Folder

@Composable
fun FolderListItem(
    context: Context,
    folder: Folder,
    onFolderClick: (Folder) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onFolderClick.invoke(folder)
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        Box{
            AsyncImage(
                modifier = Modifier.size(50.dp),
                model = ImageRequest.Builder(context).data(
                    R.drawable.folder_thumb_nail
                ).crossfade(true)
                    .crossfade(20)
                    .placeholder(R.drawable.folder_thumb_nail)
                    .error(R.drawable.folder_thumb_nail)
                    .build(),
                contentDescription = "Load",
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clipToBounds()
                    .size(24.dp)
                    .background(Color.Red, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = folder.fileCount.toString(),
                    modifier = Modifier.padding(2.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = folder.name , color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(start = 16.dp))
            Text(text = folder.fileCount.toString().plus(" files") , color = Color.LightGray , modifier = Modifier.padding(start = 16.dp))
        }
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth().size(1.dp))
}