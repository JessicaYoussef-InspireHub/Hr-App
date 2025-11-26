package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun CustomHours(
    isCheckedHours: Boolean,
    onCheckedHoursChange: (Boolean) -> Unit,
) {
    val colors = appColors()

    Row (
        verticalAlignment = Alignment.CenterVertically,
    ){
        Checkbox(
            checked = isCheckedHours,
            onCheckedChange = onCheckedHoursChange,
            colors = CheckboxDefaults.colors(
                checkedColor = colors.onSecondaryColor,
                uncheckedColor = colors.onBackgroundColor
            )
        )
        Text(
            text = stringResource(R.string.custom_hours),
            color = colors.onBackgroundColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
