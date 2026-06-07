package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*

@Composable
fun ResetAndApplyButtons(
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    val colors = appColors()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.transparent),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onApply() },
            colors = ButtonDefaults.buttonColors(
                contentColor = colors.onSecondaryColor,
                containerColor = colors.tertiaryColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = stringResource(R.string.apply),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
            onClick = { onReset() },
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.inverseOnSurface,
                contentColor = colors.onSecondaryContainer
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
        Text(
            text = stringResource(R.string.reset),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )}
    }
}