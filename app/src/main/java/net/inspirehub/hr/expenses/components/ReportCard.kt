package net.inspirehub.hr.expenses.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.ExpenseReport

@Composable
fun ReportCard(
    report: ExpenseReport,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit
) {

    val colors = appColors()
    val isState = report.state != "draft" && report.state != "refused"
    var isExpanded by remember { mutableStateOf(true) }

    fun formatDate(input: String): String {
        return try {
            val parts = input.split("-") // yyyy-MM-dd

            val year = parts[0]
            val month = parts[1].toInt().toString()
            val day = parts[2].toInt().toString()

            "$day-$month-$year"
        } catch (_: Exception) {
            input
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {},
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = report.name,
                    fontWeight = FontWeight.Bold,
                    color = if (isState)
                        colors.onBackgroundColor.copy(alpha = 0.7f)
                    else
                        colors.onBackgroundColor,
                    fontSize = 18.sp
                )
                if (isSelectionMode) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onSelect() },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = if (isSelected)
                                        colors.tertiaryColor
                                    else
                                        colors.transparent,
                                    shape = RoundedCornerShape(50)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected)
                                        colors.tertiaryColor
                                    else
                                        colors.onBackgroundColor,
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Select",
                                    tint = colors.onSecondaryColor,
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "${report.total_amount}",
                color = colors.onBackgroundColor.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )

            Text(
                text = buildAnnotatedString {
                    append("${stringResource(R.string.status)} ")
                    withStyle(
                        style = SpanStyle(
                            color = if (isState)
                                colors.onBackgroundColor.copy(alpha = 0.7f)
                            else
                                colors.tertiaryColor
                        )
                    ) {
                        append(report.state)
                    }
                },
                color = colors.onBackgroundColor.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light,
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.expenses_report),
                    fontWeight = FontWeight.SemiBold,
                    color = if (isState)
                        colors.onBackgroundColor.copy(alpha = 0.7f)
                    else
                        colors.onBackgroundColor,
                    fontSize = 16.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (isState)
                        colors.onBackgroundColor.copy(alpha = 0.7f)
                    else
                        colors.onBackgroundColor,
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (isExpanded) 0f else 180f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))


            AnimatedVisibility(visible = isExpanded) {
                Column {
                    report.expenses.forEach {
                        Column {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {},
                                colors = CardDefaults.cardColors(
                                    containerColor = colors.surfaceVariant
                                ),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        it.name,
                                        color = colors.onBackgroundColor.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        "${it.amount}",
                                        color = colors.onBackgroundColor.copy(alpha = 0.7f)
                                    )

                                    Text(
                                        text = buildAnnotatedString {
                                            append("${stringResource(R.string.you_added_this_expense_on)} ")

                                            withStyle(
                                                style = SpanStyle(
                                                    color =
                                                        if (isState)
                                                            colors.onBackgroundColor.copy(alpha = 0.7f)
                                                        else
                                                            colors.tertiaryColor
                                                )
                                            ) {
                                                append(formatDate(it.date))
                                            }
                                        },
                                        color = colors.onBackgroundColor.copy(alpha = 0.7f),
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}