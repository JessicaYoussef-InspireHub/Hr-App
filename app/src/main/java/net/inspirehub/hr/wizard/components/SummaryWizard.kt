package net.inspirehub.hr.wizard.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun SummaryWizard(
    summary: String
){

    val colors = appColors()

    Text(
        text = summary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = colors.tertiaryColor,
        textAlign = TextAlign.Center
    )
}