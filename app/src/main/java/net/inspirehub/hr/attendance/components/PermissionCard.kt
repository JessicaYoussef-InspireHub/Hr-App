package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.DescriptionIcon
import net.inspirehub.hr.GeneralIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun PermissionCard(
    hasPermission: Boolean
) {

    val colors = appColors()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.outline.copy(alpha = 0.12f)
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            DescriptionIcon( size = 42.dp )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    stringResource(R.string.permission),
                    color = colors.outline,
                    fontWeight = FontWeight.Bold
                )
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = if (hasPermission) {
                            stringResource(R.string.permission_recorded)
                        } else {
                            stringResource(R.string.no_permission_on_this_day)
                        },
                        color = colors.onBackgroundColor.copy(alpha = 0.75f)
                    )

                    if (hasPermission) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ){
                            GeneralIcon(
                                imageVector = Icons.Outlined.CheckCircle,
                                contentDescription = stringResource(R.string.permission_recorded),
                                modifier = Modifier.size(20.dp),
                                tint = colors.tertiaryColor
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = stringResource(R.string.approved),
                                color = colors.tertiaryColor
                            )
                        }
                    }
                }
            }
        }
    }
}