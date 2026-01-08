package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun DialogActionsRow(
    onConfirm: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isCalendarDialog: Boolean = false
){
    val colors = appColors()
    val confirmText = if (isCalendarDialog) stringResource(R.string.apply) else stringResource(R.string.save)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {


        Button(
            onClick = { onConfirm() },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                contentColor = colors.onSecondaryColor,
                containerColor = colors.tertiaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
//                        .height(20.dp)
                        .padding(2.dp),
                    color = colors.tertiaryColor,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    confirmText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }


        Spacer(modifier = Modifier.width(8.dp))


        Button(
            onClick = { onDiscard() },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.inverseOnSurface,
                contentColor = colors.onSecondaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                stringResource(R.string.discard),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

    }
}
