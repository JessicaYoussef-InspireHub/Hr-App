package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun CreateAnotherReport(
    onConfirm: () -> Unit,
    isLoading: Boolean
) {
    val colors = appColors()

    Button(
        onClick = { onConfirm() },
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp , horizontal = 5.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = colors.onSecondaryColor,
            containerColor = colors.tertiaryColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(stringResource(R.string.create_another_report),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold)
    }
}