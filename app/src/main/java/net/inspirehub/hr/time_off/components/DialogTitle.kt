package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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

@Composable
fun DialogTitle(
    onDiscard: () -> Unit
) {
    val colors = appColors()

    Column {
        Row(
            Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.time_off_request),
                fontWeight = FontWeight.Bold,
                color = colors.tertiaryColor,
                fontSize = 20.sp,
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(R.string.close),
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onDiscard()
                    },
                tint = colors.tertiaryColor
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth()
                .background(colors.inverseOnSurface)
        )
    }
}