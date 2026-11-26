package net.inspirehub.hr.check_in_out.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun WelcomeText(
    label : String
){

    val colors = appColors()

    Text(
        text = label,
        color = colors.onBackgroundColor.copy(alpha = 0.7f),
        fontSize = 15.sp
    )
}