package net.inspirehub.hr.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.settings.components.AccountCard
import net.inspirehub.hr.settings.components.AttendanceReminderCard
import net.inspirehub.hr.settings.components.GeneralSettingsCard
import net.inspirehub.hr.settings.components.SecurityCard
import net.inspirehub.hr.sign_in.data.SignInApiService
import net.inspirehub.hr.sign_in.data.getTrackingConfig

@Composable
fun SettingsScreen(
    navController: NavController,
) {
    val colors = appColors()

    val context = androidx.compose.ui.platform.LocalContext.current

    val sharedPref = remember {
        SharedPrefManager(context)
    }

    val scope = rememberCoroutineScope()

    var isRefreshing by remember {
        mutableStateOf(false)
    }

    BackHandler(enabled = true) {
        navController.navigate("CheckInOutScreen") {
            popUpTo("SettingsScreen") {
                inclusive = true
            }
        }
    }

    Scaffold(
        containerColor = colors.onSecondaryColor,

        topBar = {
            MyAppBar(
                label = stringResource(R.string.settings_screen),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },

        bottomBar = {
            BottomBar(
                navController = navController
            )
        }

    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .padding(16.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {

            // ---------------------------------------------------------
            // General Settings
            // ---------------------------------------------------------

            item {

                GeneralSettingsCard(
                    navController = navController
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            // ---------------------------------------------------------
            // Security
            // ---------------------------------------------------------

            item {

                SecurityCard(
                    navController = navController
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            // ---------------------------------------------------------
            // Attendance Reminder
            // ---------------------------------------------------------

            item {

                AttendanceReminderCard()

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }

            // ---------------------------------------------------------
            // Account
            // ---------------------------------------------------------

            item {

                AccountCard(
                    navController = navController
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )
            }

            // ---------------------------------------------------------
            // Refresh Button
            // ---------------------------------------------------------

            item {

                IconButton(

                    onClick = {

                        if (isRefreshing) {
                            return@IconButton
                        }

                        scope.launch {

                            isRefreshing = true

                            try {

                                // =====================================================
                                // STEP 1
                                // Get saved login data
                                // =====================================================

                                val savedEmail = sharedPref.getLoginEmail()
                                val savedPassword = sharedPref.getLoginPassword()
                                val companyId = sharedPref.getCompanyId()
                                val apiKey = sharedPref.getApiKey()


                                // =====================================================
                                // STEP 2
                                // Validate required login data
                                // =====================================================

                                if (
                                    savedEmail.isNullOrBlank() ||
                                    savedPassword.isNullOrBlank() ||
                                    companyId.isNullOrBlank() ||
                                    apiKey.isNullOrBlank()
                                ) {

                                    throw Exception(
                                        "Saved login information is missing"
                                    )
                                }


                                // =====================================================
                                // STEP 3
                                // Call SignIn API again
                                // =====================================================

                                val response =
                                    SignInApiService.signIn(
                                        savedEmail,
                                        savedPassword,
                                        companyId,
                                        apiKey
                                    )


                                // =====================================================
                                // STEP 4
                                // Check API response
                                // =====================================================

                                if (response.result.status == "error") {

                                    throw Exception(
                                        response.result.message.toString()
                                    )
                                }


                                // =====================================================
                                // STEP 5
                                // Get employee data
                                // =====================================================

                                val employeeData =
                                    response.result.message
                                        ?.employee_data
                                        ?.employee_data


                                // =====================================================
                                // STEP 6
                                // Get companies
                                // =====================================================

                                val companies =
                                    response.result.message
                                        ?.company
                                        ?: emptyList()


                                // =====================================================
                                // STEP 7
                                // Get current company address
                                // =====================================================

                                val companyAddress =
                                    response.result.message
                                        ?.company
                                        ?.firstOrNull {
                                            it.name ==
                                                    response.result.company_name
                                        }
                                        ?.address


                                // =====================================================
                                // STEP 8
                                // Save employee data
                                // =====================================================

                                sharedPref.saveToken(
                                    employeeData?.employee_token ?: ""
                                )

                                sharedPref.saveTokenExpiry(
                                    employeeData?.token_expiry ?: ""
                                )

                                sharedPref.saveCompanyId(
                                    companyId
                                )

                                sharedPref.saveApiKey(
                                    apiKey
                                )

                                sharedPref.saveLatitude(
                                    companyAddress?.latitude ?: 0.0
                                )

                                sharedPref.saveLongitude(
                                    companyAddress?.longitude ?: 0.0
                                )

                                sharedPref.saveAllowedDistance(
                                    companyAddress?.allowed_distance ?: 0.0
                                )

                                sharedPref.saveAllowedLocationsIds(
                                    employeeData?.allowed_locations_ids
                                        ?: emptyList()
                                )

                                sharedPref.saveCompaniesLatLng(
                                    companies
                                )

                                sharedPref.saveCompanyUrl(
                                    response.result.company_url ?: ""
                                )

                                sharedPref.saveEmployeeName(
                                    employeeData?.name ?: ""
                                )

                                sharedPref.saveAllowTimeOffWithMinutes(
                                    response.result.allow_time_off_With_minutes
                                )


                                // =====================================================
                                // STEP 9
                                // Get NEW employee token
                                // =====================================================

                                val newEmployeeToken =
                                    employeeData?.employee_token
                                        ?: ""


                                if (newEmployeeToken.isBlank()) {

                                    throw Exception(
                                        "Employee token is missing"
                                    )
                                }


                                // =====================================================
                                // STEP 10
                                // Get latest tracking configuration
                                // =====================================================

                                val trackingConfig =
                                    getTrackingConfig(
                                        context = context,
                                        employeeToken = newEmployeeToken
                                    )


                                // =====================================================
                                // STEP 11
                                // Save tracking configuration
                                // =====================================================

                                sharedPref.saveIsTracked(
                                    trackingConfig.result.is_tracked
                                )

                                sharedPref.saveWorkingHoursOnly(
                                    trackingConfig.result.working_hours_only
                                )

                                sharedPref.saveTrackingIntervalMinutes(
                                    trackingConfig.result.tracking_interval_minutes
                                )

                                sharedPref.saveMinDistanceMeters(
                                    trackingConfig.result.min_distance_meters
                                )

                                sharedPref.saveShowNotification(
                                    trackingConfig.result.show_notification
                                )


                                // =====================================================
                                // SUCCESS
                                // Stay on SettingsScreen
                                // =====================================================

                            } catch (e: Exception) {

                                e.printStackTrace()

                            } finally {

                                isRefreshing = false
                            }
                        }
                    },

                    enabled = !isRefreshing,

                    modifier = Modifier
                        .background(
                            color = colors.surfaceColor,
                            shape = CircleShape
                        )
                ) {

                    if (isRefreshing) {

                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            strokeWidth = 2.dp,
                            color = colors.tertiaryColor
                        )

                    } else {

                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = colors.tertiaryColor
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}