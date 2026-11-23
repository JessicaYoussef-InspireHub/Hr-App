package net.inspirehub.hr.wizard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun SkipButton(
    onSkip: () -> Unit
){

    val colors = appColors()


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = colors.tertiaryColor.copy(alpha = 0.25f),
            thickness = 1.dp
        )

        TextButton(onClick = onSkip) {
            Text(
                text = stringResource(R.string.skip),
                color = colors.tertiaryColor.copy(alpha = 0.7f),
                fontSize = 16.sp
            )
        }

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = colors.tertiaryColor.copy(alpha = 0.25f),
            thickness = 1.dp
        )
    }
}