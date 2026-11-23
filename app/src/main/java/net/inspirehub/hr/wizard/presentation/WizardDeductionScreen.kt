package net.inspirehub.hr.wizard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.HourglassBottom
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.FullButton
import net.inspirehub.hr.appColors
import net.inspirehub.hr.R
import net.inspirehub.hr.wizard.components.IconWizard
import net.inspirehub.hr.wizard.components.ItemWizard
import net.inspirehub.hr.wizard.components.NewFeature
import net.inspirehub.hr.wizard.components.SkipButton
import net.inspirehub.hr.wizard.components.SubTitleWizard
import net.inspirehub.hr.wizard.components.SummaryWizard
import net.inspirehub.hr.wizard.components.TitleWizard

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

            NewFeature()

            Spacer(modifier = Modifier.height(32.dp))

            IconWizard(Icons.Outlined.HourglassBottom)

            Spacer(modifier = Modifier.height(28.dp))

            TitleWizard(
                stringResource(R.string.introducing),
                stringResource(R.string.deduction) + "!",
                " "
            )

            Spacer(modifier = Modifier.height(12.dp))

            SubTitleWizard(stringResource(R.string.a_new_way_to_keep_track_of_your_attendance_deductions))

            Spacer(modifier = Modifier.height(30.dp))

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

                    ItemWizard(
                        icon = Icons.Outlined.Timer,
                        title = stringResource(R.string.attendance_minimum_gap),
                        description = stringResource(R.string.know_your_allowed_free_minutes_and_track_how_much_you_have_used)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    ItemWizard(
                        icon = Icons.Outlined.BarChart,
                        title = stringResource(R.string.monthly_deduction_summary),
                        description = stringResource(R.string.view_your_total_deductions_and_remaining_free_minutes)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    ItemWizard(
                        icon = Icons.Outlined.CalendarMonth,
                        title = stringResource(R.string.previous_month_overview),
                        description = stringResource(R.string.check_your_previous_month_deduction_details)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SummaryWizard(stringResource(R.string.stay_informed_stay_on_track))

            Spacer(modifier = Modifier.height(24.dp))

            FullButton(
                onClick = { exploreDeduction() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                label = stringResource(R.string.explore_deduction)
            )

            SkipButton( onSkip )
        }
    }
}