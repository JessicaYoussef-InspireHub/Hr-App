package net.inspirehub.hr.deduction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import androidx.core.graphics.toColorInt
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.utils.formatNumber

private fun parseDeductionColor(color: String): Color {
    return try {
        val hex = color.trim().removePrefix("#")
        Color("#$hex".toColorInt())
    } catch (_: IllegalArgumentException) {
        Color(0xFF4CAF50)
    }
}


@Composable
fun DeductionStatusCard(
    usedMinutes: Int,
    allowedMinutes: Int,
    deductionPercentage: Double,
    deductionColor: String,
    deductionMessage: String
) {
    val progress = (deductionPercentage / 100f).toFloat().coerceIn(0f, 1f)
    val colors = appColors()
    val deductionUiColor = parseDeductionColor(deductionColor)
    val context = LocalContext.current
    val language = remember { SharedPrefManager(context).getLanguage() }
    val messageLines = remember(deductionMessage) { deductionMessage.split("\n") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Row {
                CircularMinutesProgress(
                    progress = progress.coerceAtMost(1f),
                    used = usedMinutes,
                    allowed = allowedMinutes,
                    color = deductionUiColor
                )

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = messageLines.getOrNull(0).orEmpty(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = deductionUiColor
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = buildAnnotatedString {
                            val line = messageLines.getOrNull(1).orEmpty()

                            val regex = Regex("""\d+""")
                            var lastIndex = 0

                            regex.findAll(line).forEach { match ->
                                // Append normal text before the number
                                append(line.substring(lastIndex, match.range.first))

                                // Append number with custom style
                                withStyle(
                                    SpanStyle(
                                        color = deductionUiColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                    )
                                ) {
                                    append(
                                        formatNumber(
                                            match.value,
                                            language
                                        )
                                    )
                                }

                                lastIndex = match.range.last + 1
                            }

                            append(line.substring(lastIndex))
                        },
                        fontSize = 14.sp,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(start = 10.dp),
                        color = colors.onBackgroundColor
                    )

                    Spacer(Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = deductionUiColor.copy(.08f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Text(
                            text = messageLines.getOrNull(2).orEmpty(),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(8.dp),
                            color = colors.onBackgroundColor
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = deductionUiColor.copy(.08f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = messageLines.getOrNull(3).orEmpty(),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp),
                    color = colors.onBackgroundColor
                )
            }
        }
    }
}