package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.material3.*

@Composable
fun AttachmentFlowRow(
    selected: Boolean?,
    onChange: (Boolean?) -> Unit
) {
    val colors = appColors()

    val items = listOf(
        Triple(true,stringResource(R.string.has_attachment), "has"),
        Triple(false, stringResource(R.string.no_attachment), "none")
    )

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (value, label, _) ->

            FilterChip(
                selected = selected == value,
                onClick = {
                    onChange(value)
                },
                label = { Text(label) },
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == value,
                    borderColor = colors.onBackgroundColor.copy(alpha = 0.12f),
                    selectedBorderColor = colors.tertiaryColor
                ),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors.tertiaryColor,
                    containerColor = colors.transparent,
                    selectedLabelColor = colors.onSecondaryColor,
                    labelColor = colors.onBackgroundColor
                )
            )
        }
    }
}