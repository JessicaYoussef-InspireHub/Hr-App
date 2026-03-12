package net.inspirehub.hr.check_in_out.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SmallLoading
import net.inspirehub.hr.appColors
import androidx.compose.ui.platform.LocalContext

@Composable
fun CheckOutDialog(
    isOffline: Boolean,
    workedHours: Double?,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val colors = appColors()


    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = { onCancel() },
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
                            .clickable { onCancel() }
                    )
                }
                Text(
                    stringResource(R.string.attention),
                    color = colors.tertiaryColor,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        text = {
            if (isLoading) {
                SmallLoading()
            } else {
                Text(
                    if (!isOffline) {
                        val hoursInt = (workedHours ?: 0.0).toInt()
                        LocalContext.current.resources.getQuantityString(
                            R.plurals.check_out_confirmation,
                            hoursInt,
                            workedHours ?: 0.0
                        )
                    } else {
                        stringResource(R.string.are_you_sure_you_want_to_check_out)
                    },
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.onBackgroundColor
                )
            }
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
                    onClick = { onConfirm() }
                ) {
                    Text(
                        stringResource(R.string.ok),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.inverseOnSurface,
                        contentColor = colors.onSecondaryColor
                    ),
                    shape = RoundedCornerShape(10.dp),
                    onClick = { onCancel() }
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