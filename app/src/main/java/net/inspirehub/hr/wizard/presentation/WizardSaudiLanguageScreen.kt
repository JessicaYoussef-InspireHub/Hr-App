package net.inspirehub.hr.wizard.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.FullButton
import net.inspirehub.hr.MyDivider
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.settings.components.LanguageOption
import net.inspirehub.hr.wizard.components.IconWizard
import net.inspirehub.hr.wizard.components.NewFeature
import net.inspirehub.hr.wizard.components.SkipButton
import net.inspirehub.hr.wizard.components.SubTitleWizard
import net.inspirehub.hr.wizard.components.SummaryWizard
import net.inspirehub.hr.wizard.components.TitleWizard

@Composable
fun WizardSaudiLanguageScreen(
    exploreSettings: () -> Unit,
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

            IconWizard(Icons.Outlined.Language)

            Spacer(modifier = Modifier.height(28.dp))

            TitleWizard(
                stringResource(R.string.introducing),
                stringResource(R.string.saudi_arabic) + " 🇸🇦" ,
                "\n"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SubTitleWizard(stringResource(R.string.we_have_added_a_new_language_to_make_your_experience_more_familiar_and_comfortable_you_can_now_use_the_app_in_saudi_arabic))

            Spacer(modifier = Modifier.height(30.dp))

            // Settings screenshot title
            Text(
                text = stringResource(R.string.find_your_new_language_in_settings_screen),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.tertiaryColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Available languages
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.surfaceContainerHigh
                )
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 20.dp , horizontal = 15.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = stringResource(R.string.language),
                            modifier = Modifier.size(25.dp),
                            tint = colors.onBackgroundColor
                        )

                        Text(
                            text = stringResource(R.string.language),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackgroundColor
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    LanguageOption(
                        flag = "🇪🇬",
                        title = stringResource(R.string.language_option_arabic)
                    )

                    MyDivider(
                        horizontalPadding = 14,
                        verticalPadding = 14,
                        color = colors.surfaceColor,
                        thickness = 1
                    )

                    LanguageOption(
                        flag = "🇸🇦",
                        title = stringResource(R.string.language_option_saudi),
                        isNew = true
                    )

                    MyDivider(
                        horizontalPadding = 14,
                        verticalPadding = 14,
                        color = colors.surfaceColor,
                        thickness = 1
                    )

                    LanguageOption(
                        flag = "🇺🇸",
                        title = stringResource(R.string.language_option_english)
                    )
                }
            }



            Spacer(modifier = Modifier.height(32.dp))

            SummaryWizard(stringResource(R.string.go_to_settings_language_and_select))

            Spacer(modifier = Modifier.height(24.dp))

            FullButton(
                onClick = exploreSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                label = stringResource(R.string.explore_settings)
            )

            SkipButton( onSkip )

        }
    }
}