package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors


@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirmDelete: suspend () -> Unit
) {
    val colors = appColors()

    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = { onDismiss() },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = colors.tertiaryColor,
                        modifier = Modifier.clickable { onDismiss() }
                    )
                }
                Text(
                    text = stringResource(R.string.delete_confirmation),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.tertiaryColor,
                    textAlign = TextAlign.Start
                )
                Text(
                    stringResource(R.string.are_you_sure_you_want_to_delete_this_request),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.onBackgroundColor,
                    textAlign = TextAlign.Start,
                )
            }
        },
        confirmButton = {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            onConfirmDelete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = colors.onSecondaryColor,
                        containerColor = colors.tertiaryColor
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        stringResource(R.string.yes_delete),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }


                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.inverseOnSurface,
                        contentColor = colors.onSecondaryColor
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        stringResource(R.string.cancel),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
    )
}
