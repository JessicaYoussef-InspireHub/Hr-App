package com.example.myapplicationnewtest.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.clipPath


@Composable
fun Shapes() {
    val statusList = listOf("Refused", "Confirmed", "Valid")
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    LazyRow(
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(35.dp)
    ) {
        items(statusList) { text ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .width(140.dp)
                    .padding(vertical = 4.dp)
            ) {
                when (text) {
                    "Refused" -> {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .border(1.dp, tertiaryColor, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(2.dp)
                                    .background(
                                        color = tertiaryColor,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }

                    "Confirmed" -> {
                        Canvas(
                            modifier = Modifier
                                .size(20.dp)
                                .background(Color.Transparent, CircleShape)
                                .border(1.dp, tertiaryColor, CircleShape)
                        ) {
                            val spacing = 6.dp.toPx()
                            clipPath(Path().apply {
                                addOval(Rect(0f, 0f, size.width, size.height))
                            }) {
                                for (i in -size.height.toInt()..size.width.toInt() step spacing.toInt()) {
                                    drawLine(
                                        color = tertiaryColor,
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
                                .background(tertiaryColor, CircleShape)
                                .border(1.dp, tertiaryColor, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}

