package com.example.myapplicationnewtest.check_in_out.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.check_in_out.components.CheckInOutButton
import com.example.myapplicationnewtest.check_in_out.components.CheckOutDialog
import com.example.myapplicationnewtest.check_in_out.data.CheckInOutViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.system.exitProcess
import com.example.myapplicationnewtest.R


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckInOutScreen(
    navController: NavController,
    viewModel: CheckInOutViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

) {
    val context = LocalContext.current
    val prefManager = remember { SharedPrefManager(context) }

    val token = prefManager.getToken() ?: ""
    val latitude = prefManager.getLatitude()
    val longitude = prefManager.getLongitude()
    val allowedDistance = prefManager.getAllowedDistance()
    val currentLat by viewModel.currentLat.collectAsState()
    val currentLng by viewModel.currentLng.collectAsState()
    val isWithinDistance by viewModel.isWithinDistance.collectAsState()
    val message by viewModel.message.collectAsState()
    var isDialogLoading by remember { mutableStateOf(false) }

    val lastCheckIn by viewModel.lastCheckIn.collectAsState()
    val lastCheckOut by viewModel.lastCheckOut.collectAsState()
    val rawDate = lastCheckOut?.substringBefore(" ") ?: ""
    val parts = rawDate.split("-") // [2025, 08, 18]

    fun String.replaceDigitsWithArabic(): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return this.map { char ->
            if (char.isDigit()) arabicDigits[char.digitToInt()] else char
        }.joinToString("")
    }

    val checkInTime = lastCheckIn?.substringAfter(" ")?.let { timeString ->
        try {
            val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"))
            val currentLocale = Locale.getDefault()
            val formattedTime = time.format(DateTimeFormatter.ofPattern("h:mm a", currentLocale))

            if (currentLocale.language == "ar") {
                formattedTime.replaceDigitsWithArabic()
            } else {
                formattedTime
            }
        } catch (e: Exception) {
            "--:--"
        }
    } ?: "--:--"


    val checkOutTime = lastCheckOut?.substringAfter(" ")?.let { timeString ->
        try {
            val time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm:ss"))
            val currentLocale = Locale.getDefault()
            val formattedTime = time.format(DateTimeFormatter.ofPattern("h:mm a", currentLocale))

            if (currentLocale.language == "ar") {
                formattedTime.replaceDigitsWithArabic()
            } else {
                formattedTime
            }
        } catch (e: Exception) {
            "--:--"
        }
    } ?: "--:--"




    val workedHours by viewModel.workedHours.collectAsState()
    val totalMinutes = ((workedHours ?: 0.0) * 60).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    var showErrorDialog by remember { mutableStateOf(false) }

    val checkOutLabel = remember(parts) {
        if (parts.size == 3) {
            val year = parts.getOrNull(0)?.toIntOrNull()
            val month = parts.getOrNull(1)?.toIntOrNull()
            val day = parts.getOrNull(2)?.toIntOrNull()

            if (year != null && month != null && day != null) {
                val checkOutDateLocal = LocalDate.of(year, month, day)
                val today = LocalDate.now()
                val daysDiff = ChronoUnit.DAYS.between(checkOutDateLocal, today)

                when (daysDiff) {
                    0L -> context.getString(R.string.today)
                    1L -> context.getString(R.string.yesterday)
                    else -> {
                        val currentLocale = if (Locale.getDefault().language == "ar") Locale("ar") else Locale.ENGLISH
                        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", currentLocale)
                        val formattedDate = checkOutDateLocal.format(formatter)

                        if (currentLocale.language == "ar") {
                            formattedDate.replaceDigitsWithArabic()
                        } else {
                            formattedDate
                        }
                    }
                }
            } else {
                "--/--/----"
            }
        } else {
            "--/--/----"
        }
    }


    val locationPermissionState =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val attendanceStatus by viewModel.attendanceStatus.collectAsState()

    LaunchedEffect(true) {
        viewModel.getAttendanceStatus(token)
    }

//    LaunchedEffect(locationPermissionState.status.isGranted) {
//        if (locationPermissionState.status.isGranted) {
//            viewModel.checkLocationAndDistance(latitude, longitude, allowedDistance)
//        } else {
//            locationPermissionState.launchPermissionRequest()
//        }
//    }
    LaunchedEffect(locationPermissionState.status.isGranted) {
        Log.d("disable", "LaunchedEffect triggered | Permission granted: ${locationPermissionState.status.isGranted}")
        if (locationPermissionState.status.isGranted) {
            Log.d("disable", "Calling checkLocationAndDistance()...")
            viewModel.checkLocationAndDistance(latitude, longitude, allowedDistance)
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    BackHandler(enabled = true) {
        exitProcess(0)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (attendanceStatus == "checked_in")
                stringResource(R.string.you_are_checked_in)
            else stringResource(R.string.you_are_checked_out),
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
        )

        Text(
            if (attendanceStatus == "checked_in") {

                stringResource(R.string.checked_in_message , checkInTime)
            } else {
                stringResource(R.string.checked_out_message, checkOutLabel, checkOutTime, hours, minutes)
            },
            color = MaterialTheme.colorScheme.tertiary,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
        Log.d("disable", "isWithinDistance from state: $isWithinDistance")

        CheckInOutButton(
            attendanceStatus = attendanceStatus,
            isWithinDistance = isWithinDistance,
            onClick = {
                Log.d("disable", "Button enabled state: $isWithinDistance | attendanceStatus: $attendanceStatus")

                val nextAction =
                    if (attendanceStatus == "checked_in") "check_out" else "check_in"

                if (nextAction == "check_out") {
                    isDialogLoading = true

                    viewModel.sendAttendance(token, "status") { newStatus ->
                        isDialogLoading = false
                        if (newStatus != null) {
                            println("h✅ New status from API: $newStatus")
                        }
                    }

                    showErrorDialog = true
                } else {
                    viewModel.sendAttendance(token, nextAction) { newStatus ->
                        if (newStatus != null) {
                            println("✅ New status from API: $newStatus")
                        }
                    }
                }
            }
        )

        Button(
            onClick = {
                navController.navigate("SettingsScreen")
            }) {
            Text("Go To Settings Screen")
        }
    }


    if (showErrorDialog) {
        CheckOutDialog(
            hours = hours,
            minutes = minutes,
            isLoading = isDialogLoading,
            onConfirm = {
                showErrorDialog = false
                viewModel.sendAttendance(token, "check_out") { newStatus ->
                    if (newStatus != null) {
                        println("✅ Forced Check Out with status: $newStatus")
                    }
                }
            },
            onCancel = { showErrorDialog = false }
        )
    }


    //        Spacer(modifier = Modifier.height(50.dp))
    //        if (message.isNotEmpty()) {
    //            Text(text = message)
    //        }
    //        lastCheckIn?.let {
    //            Text("Last Check In: $it")
    //        }
    //        lastCheckOut?.let {
    //            Text("Last Check Out: $it")
    //        }
    //        Text("Status: $attendanceStatus")
    //        Text(token)
    //        Text(latitude.toString())
    //        Text(longitude.toString())
    //        Text(allowedDistance.toString())
    //        Text("Your Current Latitude: $currentLat")
    //        Text("Your Current Longitude: $currentLng")

            Button(
                onClick = {

                    navController.navigate("TimeOffScreen")
                }) {
                Text("Go To TimeOffScreen Screen")
            }
}




