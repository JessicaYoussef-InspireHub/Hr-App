package net.inspirehub.hr.deduction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.core.graphics.toColorInt
import net.inspirehub.hr.appColors
import net.inspirehub.hr.R
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
fun PreviousMonthCard(
    deductionColor: String,
    deductionMessage: String

) {
    val colors = appColors()
    val deductionUiColor = parseDeductionColor(deductionColor)
    val messageLines = remember(deductionMessage) { deductionMessage.lines().map { it.trim() } }
    val context = LocalContext.current
    val language = remember { SharedPrefManager(context).getLanguage() }

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(deductionUiColor.copy(.18f)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = deductionUiColor
                    )

                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.previous_month_overview),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.onBackgroundColor
                    )

                    Text(
                        text = stringResource(R.string.performance_summary),
                        color = colors.onBackgroundColor.copy(.8f)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = deductionUiColor.copy(.08f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Text(
                        text = messageLines.getOrNull(0).orEmpty(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.onBackgroundColor
                    )


                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = buildAnnotatedString {
                            val line = messageLines.getOrNull(1).orEmpty()

                            val regex = Regex("""\d+""")
                            var lastIndex = 0

                            regex.findAll(line).forEach { match ->

                                append(
                                    line.substring(
                                        lastIndex,
                                        match.range.first
                                    )
                                )

                                withStyle(
                                    SpanStyle(
                                        color = deductionUiColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
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
                        color = colors.onBackgroundColor
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = messageLines.getOrNull(2).orEmpty(),
                        fontSize = 14.sp,
                        lineHeight = 24.sp,
                        color = colors.onBackgroundColor
                    )

                    Spacer(Modifier.height(12.dp))


                    Text(
                        text = messageLines.getOrNull(3).orEmpty(),
                        fontSize = 14.sp,
                        lineHeight = 24.sp,
                        color = colors.onBackgroundColor
                    )
                }
            }
        }
    }
}