package net.inspirehub.hr.deduction.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.FullButton
import net.inspirehub.hr.appColors
import net.inspirehub.hr.R
import net.inspirehub.hr.deduction.components.FeatureItem

@Composable
fun WizardDeductionScreen(
    exploreDeduction: () -> Unit,
    onSkip: () -> Unit
) {
    val colors = appColors()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.onSecondaryColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // New feature label
            Surface(
                color = colors.tertiaryColor.copy(alpha = 0.22f),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(
                    width = 2.dp,
                    color = colors.tertiaryColor.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = stringResource(R.string.new_feature),
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 9.dp
                    ),
                    color = colors.tertiaryColor,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Feature illustration
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        color = colors.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.HourglassBottom,
                    contentDescription = stringResource(R.string.deduction),
                    modifier = Modifier.size(55.dp),
                    tint = colors.tertiaryColor
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(color = colors.onBackgroundColor)
                    ) {
                        append(stringResource(R.string.introducing))
                    }

                    withStyle(
                        style = SpanStyle(color = colors.tertiaryColor)
                    ) {
                        append(" ")
                        append(stringResource(R.string.deduction))
                        append("!")
                    }
                },
                fontSize = 33.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.a_new_way_to_keep_track_of_your_attendance_deductions),
                fontSize = 18.sp,
                color = colors.onBackgroundColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Feature details
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    FeatureItem(
                        icon = "⏱️",
                        title = stringResource(R.string.attendance_minimum_gap),
                        description = stringResource(R.string.know_your_allowed_free_minutes_and_track_how_much_you_have_used)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    FeatureItem(
                        icon = "📊",
                        title = stringResource(R.string.monthly_deduction_summary),
                        description = stringResource(R.string.view_your_total_deductions_and_remaining_free_minutes)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    FeatureItem(
                        icon = "📅",
                        title = stringResource(R.string.previous_month_overview),
                        description = stringResource(R.string.check_your_previous_month_deduction_details)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.stay_informed_stay_on_track),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.tertiaryColor
            )

            Spacer(modifier = Modifier.height(24.dp))

            FullButton(
                onClick = { exploreDeduction() },
                modifier = Modifier.fillMaxWidth()
                    .height(56.dp),
                label = stringResource(R.string.explore_deduction)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.tertiaryColor.copy(alpha = 0.25f),
                    thickness = 1.dp
                )

                TextButton(
                    onClick = onSkip
                ) {
                    Text(
                        text = stringResource(R.string.skip),
                        color = colors.tertiaryColor.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = colors.tertiaryColor.copy(alpha = 0.25f),
                    thickness = 1.dp
                )
            }
        }
    }
}