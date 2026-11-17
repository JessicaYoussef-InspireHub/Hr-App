package net.inspirehub.hr.bottom_app_bar

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors

@Composable
fun MoreMenuItem(
    icon: @Composable () -> Unit,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = appColors()
    val tint = if (selected) color.tertiaryColor else color.onBackgroundColor


    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides tint
                ) {
                    icon()
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    color = tint
                )
            }
        },
        onClick = onClick
    )
}