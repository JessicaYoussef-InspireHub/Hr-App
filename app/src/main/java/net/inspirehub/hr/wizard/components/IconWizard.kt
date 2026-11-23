package net.inspirehub.hr.wizard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors

@Composable
fun IconWizard(
    icon : ImageVector
){

    val colors = appColors()

    Box(
        modifier = Modifier
            .size(120.dp)
            .background(
                color = colors.surfaceVariant,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = " ",
            modifier = Modifier.size(65.dp),
            tint = colors.tertiaryColor
        )
    }
}