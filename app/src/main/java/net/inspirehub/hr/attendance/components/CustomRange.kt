package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.DateRangeIcon
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.formatLocalizedShortDate
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomRange(
    title: String,
    value: LocalDate?,
    language: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = appColors()
    val formattedValue = formatLocalizedShortDate(value, language)

    OutlinedTextField(
        value = formattedValue,
        onValueChange = { onClick() },
        modifier = modifier
            .clickable { onClick() },
        readOnly = true,
        enabled = false,
        singleLine = true,
        label = {
            Text(
                title,
                color = colors.onBackgroundColor
            )
        },
        leadingIcon = {
            DateRangeIcon()
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = colors.onBackgroundColor.copy(alpha =.12f),
            disabledContainerColor = colors.transparent,
            disabledTextColor = colors.onBackgroundColor,
            disabledLabelColor = colors.onBackgroundColor
        )
    )
}