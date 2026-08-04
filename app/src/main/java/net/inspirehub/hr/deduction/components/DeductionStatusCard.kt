package net.inspirehub.hr.deduction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.GeneralIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.deduction.presentation.DeductionStatus
import net.inspirehub.hr.deduction.presentation.DeductionUiState

@Composable
fun DeductionStatusCard(
    state: DeductionUiState
) {
    val progress = state.usedMinutes.toFloat() / state.allowedMinutes.toFloat()
    val color = appColors()

    val mainColor =
        when (state.status) {
            DeductionStatus.Safe -> color.surfaceContainer
            DeductionStatus.Warning -> Color(0xffF59E0B)
            DeductionStatus.Exceeded -> Color(0xffEF4444)
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Row {
                CircularMinutesProgress(
                    progress = progress.coerceAtMost(1f),
                    used = state.usedMinutes,
                    allowed = state.allowedMinutes,
                    color = mainColor
                )

                Spacer(Modifier.width(20.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        GeneralIcon(
                            imageVector =
                                when (state.status) {
                                    DeductionStatus.Safe -> Icons.Outlined.CheckCircle

                                    DeductionStatus.Warning -> Icons.Default.WarningAmber

                                    DeductionStatus.Exceeded -> Icons.Default.WarningAmber
                                },
                            contentDescription = stringResource(
                                when (state.status) {
                                    DeductionStatus.Safe -> R.string.you_are_doing_great
                                    DeductionStatus.Warning -> R.string.heads_up
                                    DeductionStatus.Exceeded -> R.string.limit_exceeded
                                }
                            ),
                            Modifier.size(25.dp),
                            tint = mainColor
                        )

                        Spacer(Modifier.width(8.dp))

                        Text(
                            text = stringResource(
                                when (state.status) {
                                    DeductionStatus.Safe -> R.string.you_are_doing_great
                                    DeductionStatus.Warning -> R.string.heads_up
                                    DeductionStatus.Exceeded -> R.string.limit_exceeded
                                }
                            ),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = mainColor
                        )

                    }

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = buildAnnotatedString {

                            append(stringResource(R.string.you_have_used))

                            withStyle(
                                 SpanStyle(
                                    color = mainColor,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(" ${state.usedMinutes} minutes ")
                            }

                            append(stringResource(R.string.out_of_your))

                            withStyle(
                                SpanStyle(
                                    color = mainColor,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(" ${state.allowedMinutes} ")
                            }

                            append(stringResource(R.string.allowed_this_month))
                        },
                        fontSize = 17.sp,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = mainColor.copy(.08f)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Outlined.Schedule,
                                null,
                                tint = mainColor
                            )

                            Spacer(Modifier.width(10.dp))

                            Text(
                                when (state.status) {

                                    DeductionStatus.Safe ->
                                        "You still have ${state.remainingMinutes} minutes left to use safely."

                                    DeductionStatus.Warning ->
                                        "Only ${state.remainingMinutes} minutes remain before deductions may apply."

                                    DeductionStatus.Exceeded ->
                                        "You've exceeded the limit by ${state.usedMinutes - state.allowedMinutes} minutes."
                                },
                                fontSize = 15.sp
                            )

                        }

                    }

                }

            }

            Spacer(Modifier.height(18.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = mainColor.copy(.08f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {

                    Icon(
                        if (state.status == DeductionStatus.Safe)
                            Icons.Default.Celebration
                        else
                            Icons.Default.WarningAmber,
                        null,
                        tint = mainColor
                    )

                    Spacer(Modifier.width(12.dp))

                    Text(
                        when (state.status) {

                            DeductionStatus.Safe ->
                                "Keep up the good time management — no deductions in sight!"

                            DeductionStatus.Warning ->
                                "You're getting close to the limit. Try to arrive on time."

                            DeductionStatus.Exceeded ->
                                "Additional late minutes may result in higher deductions."
                        },
                        fontWeight = FontWeight.SemiBold
                    )

                }

            }

        }

    }

}