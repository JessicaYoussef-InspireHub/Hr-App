package com.example.myapplicationnewtest.check_in_out.components

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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.appColors

@Composable
fun OfflineCheckOutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val colors = appColors()
    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = onDismiss,
        title = {
            Column (
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = colors.tertiaryColor,
                        modifier = Modifier
                            .clickable { onDismiss() }
                    )
                }
                Text(
                    stringResource(R.string.attention) ,
                    color = colors.tertiaryColor,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,)
            }
        },
        text = {
                Text(
                    stringResource(R.string.are_you_sure_you_want_to_check_out_now_the_operation_will_be_saved_and_sent_when_the_internet_is_available),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.onBackgroundColor,)
            },
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    contentColor = colors.onSecondaryColor,
                    containerColor = colors.tertiaryColor
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = { onConfirm() }
            ) {
                Text(stringResource(R.string.ok),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.inverseOnSurface,
                    contentColor = colors.onSecondaryColor
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = { onDismiss() }
            ) {
                Text(stringResource(R.string.cancel),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    )
}

