package com.codeloop.storeviewapp.features

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.codeloop.storeviewapp.features.photo.presentation.CircleMergeImageCard
import com.codeloop.storeviewapp.features.photo.presentation.ListHorizontalView

@Composable
fun PreviewScreen(
    modifier: Modifier = Modifier
) {

    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Cyan
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column (
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    Text("Old friends in Paris", style = MaterialTheme.typography.titleMedium)
                    Text("May 2023 • 14 photos", style = MaterialTheme.typography.bodySmall)
                }
                CircleMergeImageCard(
                    context = context
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            ListHorizontalView(
                context = context,
            )
        }
    }
}