package net.inspirehub.hr.wizard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
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
fun WizardSoundFeedbackScreen(
    onContinue: () -> Unit,
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

            IconWizard(Icons.AutoMirrored.Outlined.VolumeUp)

            Spacer(modifier = Modifier.height(28.dp))

            TitleWizard(
                stringResource(R.string.introducing),
                stringResource(R.string.attendance_sound_feedback),
                "\n"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SubTitleWizard(stringResource(R.string.a_simple_sound_will_let_you_know_when_your_attendance_action_is_successfully_completed))

            Spacer(modifier = Modifier.height(30.dp))

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

                    ItemWizard(
                        icon = Icons.Outlined.CheckCircle,
                        title = stringResource(R.string.successful_check_in_out),
                        description = stringResource(R.string.hear_a_confirmation_sound_after_successfully_checking_in_or_checking_out)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    ItemWizard(
                        icon = Icons.AutoMirrored.Outlined.VolumeUp,
                        title = stringResource(R.string.instant_confirmation),
                        description = stringResource(R.string.know_that_your_attendance_has_been_recorded_without_having_to_check_the_screen)
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    ItemWizard(
                        icon = Icons.Outlined.ErrorOutline,
                        title = stringResource(R.string.only_when_successful),
                        description = stringResource(R.string.no_sound_will_play_if_the_request_fails_or_the_server_rejects_the_operation)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            SummaryWizard(stringResource(R.string.your_attendance_confirmed_with_a_sound))

            Spacer(modifier = Modifier.height(24.dp))

            FullButton(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                label = stringResource(R.string.explore_it)
            )

            SkipButton( onSkip )
        }
    }
}