package com.codeloop.storeviewapp.features.photo.presentation

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.codeloop.storeviewapp.features.photo.domain.model.Folder
import com.codeloop.storeviewapp.features.photo.domain.model.MediaFileType
import com.codeloop.storeviewapp.features.utils.zone.ZoneTimer

@Composable
fun Folder2ListItem(
    modifier: Modifier = Modifier,
    context: Context,
    folder: Folder,
    onFolderClick: (Folder) -> Unit,
) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Cyan
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .clickable{
                    onFolderClick.invoke(folder)
                }
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column (
                    modifier = Modifier,
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceBetween
                ){
                    Text(folder.name, style = MaterialTheme.typography.titleMedium)

                    val createdDate = ZoneTimer.formatByYearTimePattern(folder.date, pattern = "MMMM-dd-yyyy")

                    Text(createdDate.plus(" . ").plus(folder.fileCount.toString().plus(" Files")), style = MaterialTheme.typography.bodySmall)
                }
                CircleMergeImageCard(
                    context = context,
                    mediaFileType = folder.mediaFile.firstOrNull()?.mediaFileType?: MediaFileType.Image,
                    images = folder.mediaFile.map { it.uri.toString() }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            ListHorizontalView(
                context = context,
                mediaFile = folder.mediaFile,
            )
        }
    }
}