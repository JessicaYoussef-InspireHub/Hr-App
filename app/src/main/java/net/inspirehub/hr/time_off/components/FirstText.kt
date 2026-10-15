package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun FirstText(
    label: String,
    icon: ImageVector
){
    val colors = appColors()

    Row (
        verticalAlignment = Alignment.CenterVertically
    ){

        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colors.tertiaryColor
        )

        Spacer(modifier = androidx.compose.ui.Modifier.width(6.dp))

        Text(
            text = label,
            color = colors.onBackgroundColor,
            fontSize = 16.sp
        )
    }
}