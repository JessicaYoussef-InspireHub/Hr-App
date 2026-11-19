package net.inspirehub.hr.settings.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.FullButton
import net.inspirehub.hr.MyDivider
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.settings.components.LanguageOption

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

            // New feature label
            Surface(
                color = colors.tertiaryColor.copy(alpha = 0.22f),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(
                    2.dp,
                    colors.tertiaryColor.copy(alpha = 0.7f)
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

            Spacer(modifier = Modifier.height(28.dp))

            // Language illustration
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .background(
                        color = colors.surfaceVariant,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Language,
                    contentDescription = stringResource(R.string.language),
                    modifier = Modifier.size(60.dp),
                    tint = colors.tertiaryColor
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Main title
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(color = colors.onBackgroundColor)
                    ) {
                        append(stringResource(R.string.introducing))
                        append("\n")
                    }
                    withStyle(
                        SpanStyle(color = colors.tertiaryColor)
                    ) {
                        append(stringResource(R.string.saudi_arabic))
                        append(" 🇸🇦")
                    }
                },
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.we_have_added_a_new_language_to_make_your_experience_more_familiar_and_comfortable_you_can_now_use_the_app_in_saudi_arabic),
                fontSize = 16.sp,
                color = colors.onBackgroundColor,
                textAlign = TextAlign.Center,
                lineHeight = 25.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

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

            Text(
                text = stringResource(R.string.go_to_settings_language_and_select),
                fontSize = 14.sp,
                color = colors.tertiaryColor,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            FullButton(
                onClick = exploreSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                label = stringResource(R.string.explore_settings)
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

                TextButton(onClick = onSkip) {
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