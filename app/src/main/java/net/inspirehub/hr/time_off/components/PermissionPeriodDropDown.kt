package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.ArrowDropDownIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun PermissionPeriodDropdown(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf(
        stringResource(R.string.morning_permission),
        stringResource(R.string.mid_day_permission),
        stringResource(R.string.evening_permission)
    )

    var expanded by remember { mutableStateOf(false) }
    val colors = appColors()

    Box {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { expanded = true },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedOption,
                color = colors.onBackgroundColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )

            ArrowDropDownIcon(expanded = expanded)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.onSecondaryColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = colors.onBackgroundColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
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