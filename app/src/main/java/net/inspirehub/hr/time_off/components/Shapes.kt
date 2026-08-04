package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import net.inspirehub.hr.GeneralIcon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShapesCard() {

    val colors = appColors()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            2.dp,
            colors.surfaceColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = colors.transparent
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShapesItem(
                Icons.Outlined.CheckCircleOutline,
                stringResource(R.string.valid)
            )

            ShapesItem(
                Icons.Outlined.Schedule,
                stringResource(R.string.confirmed)
            )

            ShapesItem(
                Icons.Outlined.DoNotDisturbOn,
                stringResource(R.string.refused)
            )        }
    }
}

@Composable
fun ShapesItem(
    icon: ImageVector,
    text: String
) {
    val colors = appColors()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        GeneralIcon(
            imageVector = icon,
            contentDescription = text,
            tint = colors.onBackgroundColor,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            color = colors.onBackgroundColor
        )
    }
}