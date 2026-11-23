package net.inspirehub.hr.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.MyDivider
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.settings.data.SettingsViewModel
import net.inspirehub.hr.sign_in.data.SignInApiService
import net.inspirehub.hr.sign_in.data.getTrackingConfig


@Composable
fun AccountCard(
    navController: NavController,
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column {
        Text(
            stringResource(R.string.account_settings),
            color = colors.tertiaryColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        2.dp,
                        colors.inverseOnSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = colors.onSecondaryColor
            )
        ) {
            Column {

                SettingsItem(
                    stringResource(R.string.refresh_configurations),
                    icon = Icons.Default.Refresh,
                    onClick = {

                        scope.launch {

                            try {

                                // Get saved login data
                                val savedEmail = sharedPref.getLoginEmail()
                                val savedPassword = sharedPref.getLoginPassword()
                                val companyId = sharedPref.getCompanyId()
                                val apiKey = sharedPref.getApiKey()

                                // Validate required login data
                                if (
                                    savedEmail.isNullOrBlank() ||
                                    savedPassword.isNullOrBlank() ||
                                    companyId.isNullOrBlank() ||
                                    apiKey.isNullOrBlank()
                                ) {

                                    throw Exception("Saved login information is missing")
                                }

                                // Call SignIn API again
                                val response =
                                    SignInApiService.signIn(
                                        context,
                                        savedEmail,
                                        savedPassword,
                                        companyId,
                                        apiKey
                                    )

                                // Check API response
                                if (response.result.status == "error") {
                                    throw Exception(response.result.message.toString())
                                }

                                // Get employee data
                                val employeeData = response.result.message
                                        ?.employee_data
                                        ?.employee_data

                                // Get companies
                                val companies = response.result.message
                                        ?.company
                                        ?: emptyList()

                                // Get current company address
                                val companyAddress = response.result.message
                                        ?.company
                                        ?.firstOrNull {
                                            it.name ==
                                                    response.result.company_name
                                        }
                                        ?.address

                                // Save employee data
                                sharedPref.saveToken(employeeData?.employee_token ?: "")

                                sharedPref.saveTokenExpiry(employeeData?.token_expiry ?: "")

                                sharedPref.saveCompanyId(companyId)

                                sharedPref.saveApiKey(apiKey)

                                sharedPref.saveLatitude(companyAddress?.latitude ?: 0.0)

                                sharedPref.saveLongitude(companyAddress?.longitude ?: 0.0)

                                sharedPref.saveAllowedDistance(companyAddress?.allowed_distance ?: 0.0)

                                sharedPref.saveAllowedLocationsIds(employeeData?.allowed_locations_ids ?: emptyList())

                                sharedPref.saveCompaniesLatLng(companies)

                                sharedPref.saveCompanyUrl(response.result.company_url ?: "")

                                sharedPref.saveEmployeeName(employeeData?.name ?: "")

                                sharedPref.saveEmployeeProfile(employeeData, response.result.company_name)

                                sharedPref.saveAllowTimeOffWithMinutes(response.result.allow_time_off_With_minutes)

                                // Get NEW employee token
                                val newEmployeeToken = employeeData?.employee_token ?: ""

                                if (newEmployeeToken.isBlank()) {
                                    throw Exception(
                                        "Employee token is missing"
                                    )
                                }

                                // Get latest tracking configuration
                                val trackingConfig =
                                    getTrackingConfig(
                                        context = context,
                                        employeeToken = newEmployeeToken
                                    )

                                // Save tracking configuration

                                sharedPref.saveIsTracked(trackingConfig.result.is_tracked)

                                sharedPref.saveWorkingHoursOnly(trackingConfig.result.working_hours_only)

                                sharedPref.saveTrackingIntervalMinutes(trackingConfig.result.tracking_interval_minutes)

                                sharedPref.saveMinDistanceMeters(trackingConfig.result.min_distance_meters)

                                sharedPref.saveShowNotification(trackingConfig.result.show_notification)

                             // SUCCESS
                            } catch (e: Exception) {

                                e.printStackTrace()

                            } finally { }
                        }
                    }
                )


                MyDivider(
                    horizontalPadding = 20,
                    color = colors.surfaceColor
                )

                SettingsItem(
                    stringResource(R.string.logout),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        showDialog = true
                    }
                )

                if (showDialog) {
                    MyDialog(
                        onConfirm = {
                            showDialog = false
                            viewModel.logout()
                            val companyId = sharedPref.getCompanyId() ?: ""
                            val apiKey = sharedPref.getApiKey() ?: ""
                            sharedPref.setProtectionSkipped(false)
                            navController.navigate("SignInScreen/$companyId/$apiKey")
                        },
                        onDismiss = { showDialog = false },
                        title = stringResource(R.string.logout),
                        subtitle = stringResource(R.string.are_you_sure_you_want_to_log_out),
                        confirmButtonText = stringResource(R.string.ok),
                        dismissButtonText = stringResource(R.string.cancel),
                    )
                }
            }
        }
    }
}