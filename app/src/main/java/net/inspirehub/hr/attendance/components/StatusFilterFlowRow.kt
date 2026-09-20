package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.toComposeColor

@Composable
fun AttendanceStatusFilterRow(
    selectedAttendanceFilterId: Int?,
    onAttendanceFilterSelected: (Int?) -> Unit,
    items: List<AttendanceFilterItem>
) {
    val colors = appColors()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items.size) { index ->

            val item = items[index]

            val iconColor = item.colorHex
                ?.takeIf { it.isNotBlank() }
                ?.toComposeColor()
                ?: colors.tertiaryColor

            StatusFilterCard(
                title = item.name,
                iconImage = item.iconImage,
                iconColor = iconColor,
                selected = selectedAttendanceFilterId == item.id,
                onClick = {
                    onAttendanceFilterSelected(item.id)
                }
            )
        }
    }
}