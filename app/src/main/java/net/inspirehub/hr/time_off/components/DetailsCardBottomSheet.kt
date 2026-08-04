package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.GeneralIcon
import net.inspirehub.hr.appColors
import net.inspirehub.hr.R

@Composable
fun DetailsCardBottomSheet(
    title: String,
    subTitle: String,
    state: String,
    leaveColor: Color,
    onDelete: (() -> Unit)? = null
) {
    val colors = appColors()
    val statusText = state

    val icon = when (state) {
        "validate" -> Icons.Outlined.CheckCircleOutline
        "confirm" -> Icons.Rounded.Schedule
        "draft" -> Icons.Outlined.Schedule
        else -> Icons.Outlined.DoNotDisturbOn
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        border = BorderStroke(
            2.dp,
            colors.surfaceColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = colors.transparent
        ),
        shape = RoundedCornerShape(12.dp)
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GeneralIcon(
                imageVector = icon,
                contentDescription = state,
                tint = leaveColor
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackgroundColor
                )

                Spacer(Modifier.height(2.dp))

                Text(
                    subTitle,
                    fontSize = 13.sp,
                    color = colors.onBackgroundColor.copy(.65f)
                )
            }

            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = leaveColor.copy(alpha = 0.12f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        GeneralIcon(
                            imageVector = icon,
                            contentDescription = state,
                            tint = leaveColor,
                            modifier = Modifier.size(18.dp)
                        )


                        Spacer(Modifier.width(4.dp))

                        Text(
                            statusText,
                            color = leaveColor,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer( modifier = Modifier.width(2.dp) )
                if (onDelete != null) {
                    GeneralIcon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete),
                        modifier = Modifier.size(20.dp)
                            .clickable{ onDelete() },
                        tint = colors.error
                    )
                }
            }
        }
    }
}