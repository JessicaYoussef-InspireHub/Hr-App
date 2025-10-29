package com.example.myapplicationnewtest.time_off.components

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
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.appColors


@Composable
fun DialogActionsRow(
    onConfirm: () -> Unit,
    onDiscard: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
){
    val colors = appColors()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        Spacer(modifier = Modifier.width(16.dp))

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
                        .height(20.dp)
                        .padding(2.dp),
                    color = colors.tertiaryColor,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    stringResource(R.string.save),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
