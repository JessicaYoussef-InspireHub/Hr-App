package net.inspirehub.hr.wizard.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun TitleWizard(
    firstTitle : String,
    secondTitle : String,
    space : String
){

    val colors = appColors()

    Text(
        text = buildAnnotatedString {
            withStyle(
                SpanStyle(color = colors.onBackgroundColor)
            ) {
                append(firstTitle)
            }
            withStyle(
                SpanStyle(color = colors.tertiaryColor)
            ) {
                append(space)
                append(secondTitle)
            }
        },
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        lineHeight = 38.sp
    )
}