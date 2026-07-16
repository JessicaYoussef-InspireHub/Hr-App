package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R

enum class TimeFilter {
    DAY,
    WEEK,
    MONTH,
    QUARTER,
    YEAR,
    CUSTOM
}
@Composable
fun TimeFilterFlowRow(
    selectedFilter: TimeFilter,
    onFilterSelected: (TimeFilter) -> Unit
) {

    val filters = listOf(
        TimeFilter.DAY to stringResource(R.string.day),
        TimeFilter.WEEK to stringResource(R.string.week),
        TimeFilter.MONTH to stringResource(R.string.month),
        TimeFilter.QUARTER to stringResource(R.string.quarter),
        TimeFilter.YEAR to stringResource(R.string.year),
        TimeFilter.CUSTOM to stringResource(R.string.custom)
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        filters.forEach { (filter, label) ->

            TimeFilterCard(
                title = label,
                selected = selectedFilter == filter,
                onClick = {
                    onFilterSelected(filter)
                }
            )
        }
    }
}