package net.inspirehub.hr.Profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import net.inspirehub.hr.SharedPrefManager

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController
) {
    val context = LocalContext.current

    // Read saved profile information from SharedPreferences.
    val sharedPref = remember {
        SharedPrefManager(context)
    }

    val employeeName = sharedPref.getEmployeeName()
    val email = sharedPref.getLoginEmail()
    val companyId = sharedPref.getCompanyId()
    val companyUrl = sharedPref.getCompanyUrl()


    val allowedLocations = sharedPref.getAllowedLocationsIds()
    val companies = sharedPref.getCompaniesLatLng()

    val allowTimeOff = sharedPref.getAllowTimeOffWithMinutes()
    val isTracked = sharedPref.getIsTracked()
    val workingHoursOnly = sharedPref.getWorkingHoursOnly()
    val trackingInterval = sharedPref.getTrackingIntervalMinutes()
    val minDistance = sharedPref.getMinDistanceMeters()
    val showNotification = sharedPref.getShowNotification()
    val attendanceStatus = sharedPref.getAttendanceStatus()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile")
                },
                navigationIcon = {
                    TextButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = employeeName ?: "Employee",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = email ?: "Email not available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item {
                ProfileSection(title = "Employee Information") {
                    ProfileInfoRow(
                        label = "Employee Name",
                        value = employeeName
                    )

                    ProfileInfoRow(
                        label = "Email",
                        value = email
                    )

                    ProfileInfoRow(
                        label = "Attendance Status",
                        value = attendanceStatus
                    )
                }
            }

            item {
                ProfileSection(title = "Company Information") {
                    ProfileInfoRow(
                        label = "Company ID",
                        value = companyId
                    )

                    ProfileInfoRow(
                        label = "Company URL",
                        value = companyUrl
                    )
                }
            }

            item {
                ProfileSection(title = "Account Information") {
                    ProfileInfoRow(
                        label = "Employee Token",
                        value = if (sharedPref.getToken().isNullOrBlank()) {
                            "Not available"
                        } else {
                            "••••••••••••"
                        }
                    )


                }
            }

            item {
                ProfileSection(title = "Work Location") {

                    ProfileInfoRow(
                        label = "Allowed Locations IDs",
                        value = allowedLocations.joinToString()
                    )
                }
            }

            item {
                ProfileSection(title = "Application Settings") {
                    ProfileInfoRow(
                        label = "Time Off With Minutes",
                        value = enabledText(allowTimeOff)
                    )

                    ProfileInfoRow(
                        label = "Location Tracking",
                        value = enabledText(isTracked)
                    )

                    ProfileInfoRow(
                        label = "Working Hours Only",
                        value = enabledText(workingHoursOnly)
                    )

                    ProfileInfoRow(
                        label = "Tracking Interval",
                        value = "$trackingInterval minutes"
                    )

                    ProfileInfoRow(
                        label = "Minimum Distance",
                        value = "$minDistance meters"
                    )

                    ProfileInfoRow(
                        label = "Show Tracking Notification",
                        value = enabledText(showNotification)
                    )
                }
            }

            item {
                Text(
                    text = "Saved Companies",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (companies.isEmpty()) {
                item {
                    Text(
                        text = "No saved companies found.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
            } else {
                items(companies) { company ->
                    ProfileSection(title = company.name) {
                        ProfileInfoRow(
                            label = "Location ID",
                            value = company.id.toString()
                        )

                        ProfileInfoRow(
                            label = "Latitude",
                            value = company.lat.toString()
                        )

                        ProfileInfoRow(
                            label = "Longitude",
                            value = company.lng.toString()
                        )

                        ProfileInfoRow(
                            label = "Allowed Distance",
                            value = "${company.allowedDistance} meters"
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.padding(bottom = 12.dp))
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value?.takeIf { it.isNotBlank() } ?: "Not available",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun enabledText(value: Boolean): String {
    return if (value) "Enabled" else "Disabled"
}
