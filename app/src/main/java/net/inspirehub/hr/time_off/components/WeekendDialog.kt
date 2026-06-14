package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.SmallButtons
import net.inspirehub.hr.appColors


@Composable
fun WeekendAndPublicHolidayDialog(
    message: String,
    onDismiss: () -> Unit
) {
    val colors = appColors()

    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = onDismiss,
        confirmButton = {
            SmallButtons(
                onConfirm = { onDismiss() },
                onDismiss = { onDismiss() },
                confirmButtonText = stringResource(R.string.ok),
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CloseIcon(
                        onClick = {
                            onDismiss()
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    message,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.tertiaryColor,
                    textAlign = TextAlign.Center
                )
            }
        },
    )
}

