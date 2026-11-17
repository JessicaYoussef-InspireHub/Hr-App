package net.inspirehub.hr.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkOutline
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
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

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

            // Profile label
            Surface(
                color = colors.tertiaryColor.copy(alpha = 0.22f),
                shape = RoundedCornerShape(50.dp),
                border = BorderStroke(
                    2.dp,
                    colors.tertiaryColor.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = stringResource(R.string.profile_feature),
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 9.dp
                    ),
                    color = colors.tertiaryColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Profile illustration
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
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(65.dp),
                    tint = colors.tertiaryColor
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(38.dp)
                        .background(
                            colors.tertiaryColor,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Business,
                        contentDescription = null,
                        tint = colors.onSecondaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(color = colors.onBackgroundColor)
                    ) {
                        append(stringResource(R.string.your_profile))
                    }
                    withStyle(
                        SpanStyle(color = colors.tertiaryColor)
                    ) {
                        append("\n")
                        append(stringResource(R.string.all_in_one_place))
                    }
                },
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.profile_wizard_description),
                fontSize = 16.sp,
                color = colors.onBackgroundColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Profile features
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
                    ProfileWizardItem(
                        icon = Icons.Default.Person,
                        title = stringResource(R.string.personal_information),
                        description = stringResource(
                            R.string.profile_personal_description
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    ProfileWizardItem(
                        icon = Icons.Default.WorkOutline,
                        title = stringResource(R.string.work_information),
                        description = stringResource(
                            R.string.profile_work_description
                        )
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 18.dp),
                        color = colors.inverseOnSurface
                    )

                    ProfileWizardItem(
                        icon = Icons.Default.LocationOn,
                        title = stringResource(R.string.work_locations),
                        description = stringResource(
                            R.string.profile_locations_description
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.profile_wizard_footer),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.tertiaryColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            FullButton(
                onClick = exploreProfile,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                label = stringResource(R.string.view_my_profile)
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
                    color = colors.tertiaryColor.copy(alpha = 0.25f)
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
                    color = colors.tertiaryColor.copy(alpha = 0.25f)
                )
            }
        }
    }
}

@Composable
private fun ProfileWizardItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    val colors = appColors()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    colors.tertiaryColor.copy(alpha = 0.12f),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.tertiaryColor,
                modifier = Modifier.size(23.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackgroundColor
            )

            Text(
                text = description,
                fontSize = 13.sp,
                color = colors.onBackgroundColor.copy(alpha = 0.7f),
                lineHeight = 19.sp
            )
        }
    }
}