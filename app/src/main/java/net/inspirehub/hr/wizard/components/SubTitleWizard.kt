package net.inspirehub.hr.wizard.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun SubTitleWizard(
    subTitle : String
){

    val colors = appColors()

    Text(
        text = subTitle,
        fontSize = 16.sp,
        color = colors.onBackgroundColor,
        textAlign = TextAlign.Center,
        lineHeight = 24.sp
    )
}