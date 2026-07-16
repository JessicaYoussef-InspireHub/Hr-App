package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceFilter
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOff


@Composable
fun AttendanceStatusFilterRow(
    selectedAttendanceFilter: AttendanceFilter,
    onAttendanceFilterSelected: (AttendanceFilter) -> Unit
) {
    val colors = appColors()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        StatusFilterCard(
            title = stringResource(R.string.all),
            icon = Icons.Outlined.Groups,
            iconColor = colors.tertiaryColor,
            selected = selectedAttendanceFilter == AttendanceFilter.ALL,
            onClick = {
                onAttendanceFilterSelected(AttendanceFilter.ALL)
            }
        )

        StatusFilterCard(
            title = stringResource(R.string.present),
            icon = Icons.Outlined.Person,
            iconColor =  colors.surfaceContainer,
            selected = selectedAttendanceFilter == AttendanceFilter.PRESENT,
            onClick = {
                onAttendanceFilterSelected(AttendanceFilter.PRESENT)
            }
        )

        StatusFilterCard(
            title = stringResource(R.string.late),
            icon = Icons.Outlined.AccessTime,
            iconColor = colors.surfaceContainerLow,
            selected = selectedAttendanceFilter == AttendanceFilter.LATE,
            onClick = {
                onAttendanceFilterSelected(AttendanceFilter.LATE)
            }
        )

        StatusFilterCard(
            title = stringResource(R.string.absent),
            icon = Icons.Outlined.PersonOff,
            iconColor = colors.surfaceContainerHighest,
            selected = selectedAttendanceFilter == AttendanceFilter.ABSENT,
            onClick = {
                onAttendanceFilterSelected(AttendanceFilter.ABSENT)
            }
        )
    }
}