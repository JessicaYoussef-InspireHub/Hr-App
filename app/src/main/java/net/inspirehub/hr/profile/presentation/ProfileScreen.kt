
package net.inspirehub.hr.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.profile.data.ProfileUiData
import net.inspirehub.hr.profile.data.ProfileWorkLocation


@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
) {
    val context = LocalContext.current
    val profile = remember { loadProfileFromPrefs(SharedPrefManager(context)) }
    val colors = appColors()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.surfaceVariant)
    ) {

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.surfaceColor)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.onBackgroundColor
                )
            }

            Text(
                text = "My Profile",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackgroundColor
            )

            IconButton(onClick = onMoreClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More",
                    tint = colors.onBackgroundColor
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Profile Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colors.tertiaryColor
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        modifier = Modifier
                            .size(82.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile.name
                                .split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.toString() }
                                .joinToString(""),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.tertiaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = profile.name,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = profile.jobTitle,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            // Personal Information
            ProfileSectionCard(
                title = "Personal Information"
            ) {
                ProfileInfoRow(
                    icon = {
                        Icon(
                            Icons.Default.MailOutline,
                            contentDescription = null
                        )
                    },
                    label = "Email Address",
                    value = profile.email
                )

                ProfileSectionDivider()

                ProfileInfoRow(
                    icon = {
                        Icon(
                            Icons.Default.Cake,
                            contentDescription = null
                        )
                    },
                    label = "Birthday",
                    value = profile.birthday
                )
            }

            // Work Information
            ProfileSectionCard(
                title = "Work Information"
            ) {
                ProfileInfoRow(
                    icon = {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null
                        )
                    },
                    label = "Department",
                    value = profile.department
                )

                ProfileSectionDivider()

                ProfileInfoRow(
                    icon = {
                        Icon(
                            Icons.Default.WorkOutline,
                            contentDescription = null
                        )
                    },
                    label = "Company",
                    value = profile.company
                )
            }

            // Work Locations
            ProfileSectionCard(
                title = "Work Locations"
            ) {
                profile.locations.forEachIndexed { index, location ->

                    ProfileLocationRow(location = location)

                    if (index != profile.locations.lastIndex) {
                        ProfileSectionDivider()
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/** "2000-07-02" (as the server sends it) -> "2 July 2000". Anything else is shown as is. */
private fun formatBirthday(raw: String): String {
    if (raw.equals("false", ignoreCase = true)) return "-"
    return try {
        java.time.LocalDate.parse(raw.take(10)).format(
            java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy", java.util.Locale.ENGLISH)
        )
    } catch (e: Exception) {
        raw
    }
}

private fun loadProfileFromPrefs(prefs: SharedPrefManager): ProfileUiData {
    val allowedIds = prefs.getAllowedLocationsIds()
    val addresses = prefs.getCompanyAddresses()

    val locations = prefs.getCompaniesLatLng()
        .filter { allowedIds.isEmpty() || it.id in allowedIds }
        .map { site ->
            ProfileWorkLocation(
                name = site.name,
                address = addresses[site.id].orEmpty(),
                allowedDistance = site.allowedDistance.toInt()
            )
        }

    return ProfileUiData(
        name = prefs.getEmployeeName().orEmpty(),
        email = prefs.getEmployeeEmail() ?: "-",
        jobTitle = prefs.getEmployeeJobTitle().orEmpty(),
        department = prefs.getEmployeeDepartment() ?: "-",
        company = prefs.getEmployeeCompany() ?: "-",
        birthday = prefs.getEmployeeBirthday()?.let(::formatBirthday) ?: "-",
        locations = locations
    )
}

@Composable
private fun ProfileSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = appColors()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackgroundColor
            )

            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    val colors = appColors()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.tertiaryColor.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            CompositionIcon(
                content = icon,
                tint = colors.tertiaryColor
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = label,
                fontSize = 11.sp,
                color = colors.onBackgroundColor.copy(alpha = 0.55f)
            )

            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onBackgroundColor
            )
        }
    }
}

@Composable
private fun CompositionIcon(
    content: @Composable () -> Unit,
    tint: Color
) {
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.material3.LocalContentColor provides tint,
        content = content
    )
}

@Composable
private fun ProfileLocationRow(
    location: ProfileWorkLocation
) {
    val colors = appColors()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.tertiaryColor.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = colors.tertiaryColor
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = location.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackgroundColor
            )

            Text(
                text = location.address,
                fontSize = 11.sp,
                color = colors.onBackgroundColor.copy(alpha = 0.60f)
            )

            Text(
                text = "Allowed distance: ${location.allowedDistance} m",
                fontSize = 11.sp,
                color = colors.tertiaryColor,
                fontWeight = FontWeight.Medium
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = colors.onBackgroundColor.copy(alpha = 0.45f)
        )
    }
}

@Composable
private fun ProfileSectionDivider() {
    val colors = appColors()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                colors.onBackgroundColor.copy(alpha = 0.08f)
            )
    )
}