  package com.example.myapplicationnewtest.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R


@Composable
fun ColorsOfAnnualLeave() {

    val primaryColor = MaterialTheme.colorScheme.tertiary

    Column (
        modifier = Modifier.padding(start = 15.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ){
        Text(
            text = stringResource(R.string.annual_leaves),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        LegendItem(
            boxContent = {
                Canvas(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = CircleShape
                        )
                ) {
                    val spacing = 6.dp.toPx()

                    clipPath(Path().apply {
                        addOval(Rect(0f, 0f, size.width, size.height))
                    }) {
                        for (i in -size.height.toInt()..size.width.toInt() step spacing.toInt()) {
                            drawLine(
                                color = primaryColor,
                                start = Offset(i.toFloat(), 0f),
                                end = Offset(i + size.height, size.height),
                                strokeWidth = 2f,
                                cap = StrokeCap.Square
                            )
                        }
                    }
                }
            },
            label = stringResource(R.string.to_approve)
        )

        LegendItem(
            boxContent = {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(20.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSurface,
                            shape = CircleShape
                        )
                )
            },
            label = stringResource(R.string.approved)
        )

        LegendItem(
            boxContent = {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(4.dp)
                        .background(color = primaryColor, shape = RoundedCornerShape(2.dp))
                )
            },
            label = stringResource(R.string.refused)
        )
    }
}

@Composable
fun LegendItem(
    boxContent: @Composable () -> Unit,
    label: String
){
    Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            boxContent()
            Text(
                text = label ,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
}