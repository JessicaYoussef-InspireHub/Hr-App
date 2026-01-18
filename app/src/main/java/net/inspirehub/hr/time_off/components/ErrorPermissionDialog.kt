package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorPermissionDialog(
    message: String,
    onDismiss: () -> Unit
) {
    val colors = appColors()

    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = colors.tertiaryColor,
                        modifier = Modifier
                            .clickable { onDismiss() }
                    )
                }
                Text(
                    stringResource(R.string.invalid_request),
                    color = colors.tertiaryColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        text = {
            Text(
                message,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = colors.onBackgroundColor,
            )
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.tertiaryColor,
                    contentColor = colors.onSecondaryColor
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    stringResource(R.string.ok),
                    color = colors.onSecondaryColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
    )
}
