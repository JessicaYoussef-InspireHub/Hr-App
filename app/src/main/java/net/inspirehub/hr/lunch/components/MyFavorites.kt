package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors

@Composable
fun MyFavorite(){
    val colors = appColors()
    Box(
        modifier = Modifier
            .background(colors.surfaceContainerHigh, CircleShape)
            .clickable { }
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "favorite",
            tint = colors.onBackgroundColor,
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )
    }
}