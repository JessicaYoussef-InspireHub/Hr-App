package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors


@Composable
fun Shapes() {
    val statusList = listOf(
        stringResource(id = R.string.refused),
        stringResource(id = R.string.confirmed),
        stringResource(id = R.string.valid)
    )

    val colors = appColors()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(statusList) { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .wrapContentWidth()
            ) {
                when (text) {
                    "Refused" -> {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(1.dp, colors.tertiaryColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .background(
                                        color = colors.tertiaryColor,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }

                    "Confirmed" -> {
                        Canvas(
                            modifier = Modifier
                                .size(20.dp)
                                .background(colors.transparent, CircleShape)
                                .border(1.dp, colors.tertiaryColor, CircleShape)
                        ) {
                            val spacing = 6.dp.toPx()
                            clipPath(Path().apply {
                                addOval(Rect(0f, 0f, size.width, size.height))
                            }) {
                                for (i in -size.height.toInt()..size.width.toInt() step spacing.toInt()) {
                                    drawLine(
                                        color = colors.tertiaryColor,
                                        start = Offset(i.toFloat(), 0f),
                                        end = Offset(i + size.height, size.height),
                                        strokeWidth = 4f,
                                        cap = StrokeCap.Round
                                    )
                                }
                            }
                        }
                    }

                    else -> {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(colors.tertiaryColor, CircleShape)
                                .border(1.dp, colors.tertiaryColor, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    color = colors.onBackgroundColor
                )
            }
        }
    }
}

