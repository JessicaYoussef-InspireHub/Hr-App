package net.inspirehub.hr.check_in_out.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun AttendanceStatusCard(
    attendanceStatus: String,
    checkInTime: String
) {
    val colors = appColors()

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = colors.tertiaryColor.copy(alpha = 0.55f),
                shape = CircleShape
            )
            .padding(
                horizontal = 18.dp,
                vertical = 9.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (
            attendanceStatus == "checked_in" || attendanceStatus == "checked_out"
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = colors.tertiaryColor,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = when (attendanceStatus) {
                "checked_in" -> stringResource(R.string.checked_in) + " - $checkInTime"

                "checked_out" ->  stringResource(R.string.checked_in) + " - $checkInTime"

                else -> stringResource(R.string.loading)
            },
            color = colors.tertiaryColor,
            fontSize = 18.sp
        )
    }
}