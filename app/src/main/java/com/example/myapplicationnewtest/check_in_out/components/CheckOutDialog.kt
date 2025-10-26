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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R

@Composable
fun CheckOutDialog(
    hours: Int,
    minutes: Int,
    isLoading: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
){
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        onDismissRequest = { onCancel() },
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
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier
                            .clickable { onCancel() }
                    )
                }
                Text(
                    stringResource(R.string.attention) ,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,)
            }
        },
        text = {
            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                   CircularProgressIndicator()
                }
            } else {
            Text(
                stringResource(R.string.check_out_confirmation, hours, minutes),
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,)
        }},
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.tertiary
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
                    containerColor = MaterialTheme.colorScheme.inverseOnSurface,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = { onCancel() }
            ) {
                Text(stringResource(R.string.cancel),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold)
            }
        }
    )
}