package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors


@Composable
fun HalfDayCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = appColors()


    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            2.dp,
            colors.surfaceColor
        ),
        colors = CardDefaults.cardColors(
            containerColor = colors.transparent
        ),
        shape = RoundedCornerShape(12.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp , vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
                text = stringResource(R.string.half_day),
                color = colors.onBackgroundColor,
                fontSize = 14.sp
            )

            Switch(
                modifier = Modifier.scale(0.8f),
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.onSecondaryColor,
                    checkedTrackColor = colors.tertiaryColor,
                    uncheckedThumbColor = colors.onSecondaryColor,
                    uncheckedTrackColor = colors.onBackgroundColor,
                    uncheckedBorderColor = colors.transparent,
                    checkedBorderColor = colors.transparent
                )
            )
        }
    }
}
