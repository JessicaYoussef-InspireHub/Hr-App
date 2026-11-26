package net.inspirehub.hr.check_in_out.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun YourStatusText() {

    val colors = appColors()

    Text(
        text = buildAnnotatedString {

            withStyle(SpanStyle(color = colors.tertiaryColor)) {
                append(stringResource(R.string.you_are_in_first_line))
            }
            append("\n")
            append(stringResource(R.string.you_are_in_second_line))
        },
        color = colors.onBackgroundColor,
        fontSize = 120.sp,
        lineHeight = 100.sp,
        fontWeight = FontWeight.ExtraBold
    )
}