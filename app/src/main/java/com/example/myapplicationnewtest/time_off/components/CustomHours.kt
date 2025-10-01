package com.example.myapplicationnewtest.time_off.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R

@Composable
fun CustomHours(
    isCheckedHours: Boolean,
    onCheckedHoursChange: (Boolean) -> Unit,
) {
    Row (
        verticalAlignment = Alignment.CenterVertically,
    ){
        Checkbox(
            checked = isCheckedHours,
            onCheckedChange = onCheckedHoursChange,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.tertiary,
                uncheckedColor = MaterialTheme.colorScheme.surface
            )
        )
        Text(
            text = stringResource(R.string.custom_hours),
            color = MaterialTheme.colorScheme.surface,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
