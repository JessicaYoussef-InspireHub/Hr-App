package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors


@Composable
fun TimeSummaryItem(
    title: String,
    value: String,
    imageVector: ImageVector,
    color: Color
) {

    val colors = appColors()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            title,
            color = colors.onSurface,
            style = MaterialTheme.typography.labelLarge,
        )

        Spacer(Modifier.height(6.dp))

        Icon(
            imageVector = imageVector,
            contentDescription = title,
            tint = color
        )

        Spacer(Modifier.height(6.dp))

        Text(
            value,
            fontWeight = FontWeight.Bold,
            color = colors.onBackgroundColor

        )
    }
}