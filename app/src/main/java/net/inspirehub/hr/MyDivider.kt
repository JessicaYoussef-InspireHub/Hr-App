package net.inspirehub.hr

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.graphics.Color

@Composable
fun MyDivider(
    horizontalPadding: Int = 0,
    verticalPadding: Int = 0,
    color: Color,
    thickness: Int = 0
) {
    HorizontalDivider(
        thickness = thickness.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding.dp, vertical = verticalPadding.dp),
        color = color
    )
}