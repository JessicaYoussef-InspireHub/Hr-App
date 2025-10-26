package com.example.myapplicationnewtest.check_in_out.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R

@Composable
fun CheckInOutButton(
    attendanceStatus: String,
    isWithinDistance: Boolean,
    onClick: () -> Unit
) {

    Button(
        enabled = isWithinDistance,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(8.dp),
        border = if (isWithinDistance) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.tertiary)
        } else null,
        colors =
        if (attendanceStatus == "checked_in") {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.5f)
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                contentColor = MaterialTheme.colorScheme.tertiary,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            )
        }
    ) {
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
