package net.inspirehub.hr.wizard.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun NewFeature(){
    val colors = appColors()

    Surface(
        color = colors.tertiaryColor.copy(alpha = 0.22f),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(
            2.dp,
            colors.tertiaryColor.copy(alpha = 0.7f)
        )
    ) {
        Text(
            text = stringResource(R.string.new_feature),
            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 9.dp
            ),
            color = colors.tertiaryColor,
            fontSize = 16.sp
        )
    }
}