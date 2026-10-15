package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.ArrowDropDownIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

data class PermissionDuration(
    val title: String,
    val hours: Double
)

@Composable
fun PermissionDurationDropdown(
    selectedOption: PermissionDuration,
    onOptionSelected: (PermissionDuration) -> Unit
) {
    val options = listOf(
        PermissionDuration(stringResource(R.string.duration_30_minutes), 0.5),
        PermissionDuration(stringResource(R.string.duration_1_hour), 1.0),
        PermissionDuration(stringResource(R.string.duration_1_hour_30_minutes), 1.5),
        PermissionDuration(stringResource(R.string.duration_2_hours), 2.0)
    )

    var expanded by remember { mutableStateOf(false) }
    val colors = appColors()

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expanded = true
            },
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
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = selectedOption.title,
                    color = colors.onBackgroundColor,
                    fontSize = 15.sp
                )
            }

            ArrowDropDownIcon(expanded = expanded)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    color = colors.surfaceContainerHigh,
                    shape = RoundedCornerShape(12.dp)
                ),
            border = BorderStroke(
                2.dp,
                colors.surfaceColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.title,
                            color = colors.onBackgroundColor,
                            fontSize = 15.sp
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}