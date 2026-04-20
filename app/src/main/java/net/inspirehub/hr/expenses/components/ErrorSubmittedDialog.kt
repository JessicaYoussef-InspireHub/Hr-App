package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
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
fun ErrorSubmittedDialog(
    message: String,
    onDismiss: () -> Unit
) {
    val colors = appColors()

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = colors.surfaceVariant,
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                    stringResource(R.string.error_dialog),
                    color = colors.tertiaryColor,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },

        text = {
            Text(
                text = message,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = colors.onBackgroundColor
            )
        },

        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colors.onSecondaryColor,
                        containerColor = colors.tertiaryColor
                    ),
                    shape = RoundedCornerShape(10.dp),
                    onClick = { onDismiss() }
                ) {
                    Text(
                        stringResource(R.string.ok),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        },
    )
}