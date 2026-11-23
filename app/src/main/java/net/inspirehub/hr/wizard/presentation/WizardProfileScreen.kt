package net.inspirehub.hr.wizard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.FullButton
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.wizard.components.IconWizard
import net.inspirehub.hr.wizard.components.ItemWizard
import net.inspirehub.hr.wizard.components.NewFeature
import net.inspirehub.hr.wizard.components.SkipButton
import net.inspirehub.hr.wizard.components.SubTitleWizard
import net.inspirehub.hr.wizard.components.SummaryWizard
import net.inspirehub.hr.wizard.components.TitleWizard

@Composable
fun WizardProfileScreen(
    exploreProfile: () -> Unit,
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

            IconWizard(Icons.Default.Person)

            Spacer(modifier = Modifier.height(28.dp))

            TitleWizard(
                stringResource(R.string.your_profile),
                stringResource(R.string.all_in_one_place),
                "\n"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SubTitleWizard(stringResource(R.string.everything_you_need_to_know_about_your_employee_profile_organized_in_one_simple_place))

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
                        icon = Icons.Default.Person,
                        title = stringResource(R.string.personal_information),
                        description = stringResource(R.string.view_your_email_address_and_personal_details)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    ItemWizard(
                        icon = Icons.Default.WorkOutline,
                        title = stringResource(R.string.work_information),
                        description = stringResource(R.string.find_your_job_title_department_and_company_information)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    ItemWizard(
                        icon = Icons.Default.LocationOn,
                        title = stringResource(R.string.work_locations),
                        description = stringResource(R.string.explore_your_assigned_work_locations_and_their_allowed_distances)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SummaryWizard(stringResource(R.string.your_work_information_always_within_reach))

            Spacer(modifier = Modifier.height(24.dp))

            FullButton(
                onClick = exploreProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                label = stringResource(R.string.explore_profile)
            )

            SkipButton( onSkip )
        }
    }
}