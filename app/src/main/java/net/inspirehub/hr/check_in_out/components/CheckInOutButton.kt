package net.inspirehub.hr.check_in_out.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun CheckInOutButton(
    attendanceStatus: String,
    isWithinDistance: Boolean,
    onClick: () -> Unit,
    isLoading: Boolean = false,
) {
    val colors = appColors()

    Button(
//        enabled = isWithinDistance ,
        enabled = isWithinDistance && !isLoading,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(8.dp),
        border = if (isWithinDistance) {
            BorderStroke(2.dp, colors.tertiaryColor)
        } else null,
        colors =
            if (attendanceStatus == "checked_in") {
                ButtonDefaults.buttonColors(
                    containerColor = colors.tertiaryColor,
                    contentColor = colors.onSecondaryColor,
                    disabledContainerColor = colors.tertiaryColor.copy(alpha = 0.5f),
                    disabledContentColor = colors.onSecondaryColor.copy(alpha = 0.5f)
                )
            } else {
                ButtonDefaults.buttonColors(
                    containerColor = colors.onSecondaryColor,
                    contentColor = colors.tertiaryColor,
                    disabledContainerColor = colors.surfaceVariant,
                    disabledContentColor = colors.tertiaryColor.copy(alpha = 0.5f)
                )
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                Row{
                    CircularProgressIndicator(
                        color = colors.tertiaryColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
            Text(
                when (attendanceStatus) {
                    "checked_in" -> stringResource(R.string.check_out)
                    "checked_out" -> stringResource(R.string.check_in)
                    else -> stringResource(R.string.loading)
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
