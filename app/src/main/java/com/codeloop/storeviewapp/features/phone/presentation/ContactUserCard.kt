package com.codeloop.storeviewapp.features.phone.presentation

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.codeloop.storeviewapp.R

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactUserCard(
    context: Context,
    phoneList : PhoneList,
    dialCall : (PhoneList) -> Unit,
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                enabled = true,
                onClick = {

                },
                onLongClick = {
                    dialCall.invoke(phoneList)
                }
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {

        Box(
            modifier = Modifier.size(50.dp)
                .clip(CircleShape)
                .border(2.dp, Color.Green, CircleShape)
        ){
            if (phoneList.userImage.isEmpty()){
                Text(
                    text = phoneList.userName.initial(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                )
            }
            else{
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(phoneList.userImage.toUri())
                        .crossfade(true)
                        .placeholder(R.drawable.contact_logo)
                        .error(R.drawable.contact_logo)
                        .build(),
                    contentDescription = "Phone",
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = phoneList.userName , color = MaterialTheme.colorScheme.onBackground, modifier = Modifier.padding(start = 16.dp))
            Text(text = phoneList.number , color = Color.LightGray , modifier = Modifier.padding(start = 16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))
    }
    HorizontalDivider(modifier = Modifier.fillMaxWidth().size(1.dp))
}

private fun String.initial() :String {
    return split(" ").joinToString("") {
        it.first().uppercase()
    }.take(3).ifEmpty { "M" }
}