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
fun ExpenseFlowRow(
    selectedStatuses: Set<String>,
    onStatusChange: (Set<String>) -> Unit,
    expenses: Boolean = true
) {
    val colors = appColors()

    val statusItems = if (expenses) {
        listOf(
        "all" to stringResource(R.string.all),
        "draft" to stringResource(R.string.draft),
        "reported" to stringResource(R.string.reported),
        "approved" to stringResource(R.string.approved),
        "refused" to stringResource(R.string.refused)
    ) } else {
        listOf(
            "all" to stringResource(R.string.all),
            "draft" to stringResource(R.string.draft),
            "submit" to stringResource(R.string.submitted),
            "approve" to stringResource(R.string.approved),
            "cancel" to stringResource(R.string.refused)
        )
    }

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        statusItems.forEach { (key, label) ->

            FilterChip(
                selected = selectedStatuses.contains(key),
                onClick = {
                    onStatusChange(
                        when {
                            key == "all" -> setOf("all")

                            selectedStatuses.contains(key) -> {
                                selectedStatuses - key
                            }

                            else -> {
                                (selectedStatuses + key) - "all"
                            }
                        }
                    )
                },
                label = {
                    Text(label)
                },
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selectedStatuses.contains(key),
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