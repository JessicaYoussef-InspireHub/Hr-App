package net.inspirehub.hr.deduction.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.utils.getLocalizedMinuteText


@Composable
fun CircularMinutesProgress(
    progress: Float,
    used: Int,
    allowed: Int,
    color: Color
) {
    val colors = appColors()
    val context = LocalContext.current
    val language = remember { SharedPrefManager(context).getLanguage() }

    Box(
        modifier = Modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawArc(
                color = colors.onTertiaryContainer,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(14.dp.toPx(), cap = StrokeCap.Butt)
            )

            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = progress * 360,
                useCenter = false,
                style = Stroke(14.dp.toPx(), cap = StrokeCap.Butt)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                    ) {
                        append(formatNumber(used.toString(), language))
                    }

                    withStyle(
                        SpanStyle(
                            fontSize = 22.sp,
                            color = colors.onBackgroundColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(" / ${formatNumber(allowed.toString(), language)}")
                    }
                }
            )

            Text(
                text = getLocalizedMinuteText(used, language),
                color = colors.onBackgroundColor
            )
        }
    }
}