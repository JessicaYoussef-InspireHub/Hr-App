package com.example.myapplicationnewtest.time_off.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.appColors

@Composable
fun RetryButton(
    onClick: () -> Unit,
) {
    val colors = appColors()

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth().height(60.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.tertiaryColor,
            contentColor = colors.onSecondaryColor,
        )
    ) {
        Text(
            stringResource(R.string.retry),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSecondaryColor
        )
    }
}
