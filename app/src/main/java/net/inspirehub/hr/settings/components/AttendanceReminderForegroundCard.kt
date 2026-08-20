//package net.inspirehub.hr.settings.components
//
//import android.Manifest
//import android.content.pm.PackageManager
//import android.os.Build
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.border
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.NotificationsActive
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.Switch
//import androidx.compose.material3.SwitchDefaults
//import androidx.compose.material3.Text
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.withStyle
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.ContextCompat
//import net.inspirehub.hr.InfoIcon
//import net.inspirehub.hr.MyDialog
//import net.inspirehub.hr.R
//import net.inspirehub.hr.SharedPrefManager
//import net.inspirehub.hr.appColors
//import net.inspirehub.hr.check_in_out.data.AttendanceReminderForegroundManager
//import android.app.Activity
//import android.content.Intent
//import android.provider.Settings
//import androidx.core.app.ActivityCompat
//import androidx.core.net.toUri
//
//@Composable
//fun AttendanceReminderForegroundCard() {
//
//    val colors = appColors()
//    val context = LocalContext.current
//
//    var showLocationSettingsDialog by remember { mutableStateOf(false) }
//    val sharedPref = remember { SharedPrefManager(context) }
//
//    var isReminderEnabled by remember {
//        mutableStateOf(
//            sharedPref.isAttendanceReminderEnabled()
//        )
//    }
//
//    var showReminderDialog by remember {
//        mutableStateOf(false)
//    }
//
//
//
//
//    fun hasForegroundLocationPermission(): Boolean {
//        val fineGranted =
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//        val coarseGranted =
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//
//        return fineGranted || coarseGranted
//    }
//
//    LaunchedEffect(Unit) {
//
//        if (isReminderEnabled && !hasForegroundLocationPermission()) {
//
//            isReminderEnabled = false
//
//            sharedPref.setAttendanceReminderEnabled(false)
//
//            AttendanceReminderForegroundManager.stop(context)
//        }
//    }
//
//    /*
//     * Foreground location permission
//     */
//    val locationPermissionLauncher =
//        rememberLauncherForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions ->
//
//            Log.d(
//                "ATTENDANCE_REMINDER",
//                "Location permission result = $permissions"
//            )
//
//            val fineGranted =
//                permissions[
//                    Manifest.permission.ACCESS_FINE_LOCATION
//                ] == true
//
//            val coarseGranted =
//                permissions[
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ] == true
//
//            val locationGranted =
//                fineGranted || coarseGranted
//
//            if (locationGranted) {
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "Foreground location GRANTED"
//                )
//
//                isReminderEnabled = true
//
//                sharedPref.setAttendanceReminderEnabled(true)
//
//                AttendanceReminderForegroundManager.start(
//                    context
//                )
//
//            } else {
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "Foreground location DENIED"
//                )
//
//                isReminderEnabled = false
//
//                sharedPref.setAttendanceReminderEnabled(false)
//
//                AttendanceReminderForegroundManager.stop(
//                    context
//                )
//            }
//        }
//
//    /*
//     * Notification permission - Android 13+
//     */
//    val notificationPermissionLauncher =
//        rememberLauncherForActivityResult(
//            ActivityResultContracts.RequestPermission()
//        ) { granted ->
//
//            if (granted) {
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "Notification permission granted"
//                )
//
//                requestLocationPermission(
//                    locationPermissionLauncher
//                )
//
//            } else {
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "Notification permission denied"
//                )
//            }
//        }
//
//    Column {
//
//        Text(
//            text = stringResource(
//                R.string.reminders
//            ),
//            color = colors.tertiaryColor,
//            fontSize = 20.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(
//            modifier = Modifier.height(8.dp)
//        )
//
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .border(
//                    BorderStroke(
//                        2.dp,
//                        colors.inverseOnSurface
//                    ),
//                    RoundedCornerShape(8.dp)
//                ),
//            colors = CardDefaults.cardColors(
//                containerColor = colors.onSecondaryColor
//            )
//        ) {
//
//            SettingsItem(
//                label = stringResource(
//                    R.string.reminders
//                ),
//                icon = Icons.Default.NotificationsActive,
//
//                onClick = {
//                    Log.d(
//                        "ATTENDANCE_REMINDER",
//                        "Switch clicked - current state = $isReminderEnabled"
//                    )
//                    if (!isReminderEnabled) {
//                        Log.d(
//                            "ATTENDANCE_REMINDER",
//                            "Switch is OFF -> opening explanation dialog"
//                        )
//                        showReminderDialog = true
//
//                    } else {
//                        Log.d(
//                            "ATTENDANCE_REMINDER",
//                            "Switch is ON -> disabling reminder"
//                        )
//                        isReminderEnabled = false
//
//                        AttendanceReminderForegroundManager.stop(
//                            context
//                        )
//                    }
//                },
//
//                trailingIcon = {
//
//                    Switch(
//                        checked = isReminderEnabled,
//
//                        onCheckedChange = { enabled ->
//                            Log.d(
//                                "ATTENDANCE_REMINDER",
//                                "Switch onCheckedChange = $enabled"
//                            )
//                            if (enabled) {
//
//                                Log.d(
//                                    "ATTENDANCE_REMINDER",
//                                    "User wants to ENABLE reminder -> opening explanation dialog"
//                                )
//
//                                showReminderDialog = true
//
//                            } else {
//                                Log.d(
//                                    "ATTENDANCE_REMINDER",
//                                    "User wants to DISABLE reminder"
//                                )
//                                isReminderEnabled = false
//
//                                AttendanceReminderForegroundManager.stop(
//                                    context
//                                )
//                            }
//                        },
//
//                        colors = SwitchDefaults.colors(
//                            checkedThumbColor = colors.onSecondaryColor,
//
//                            checkedTrackColor =
//                                colors.tertiaryColor,
//
//                            uncheckedThumbColor =
//                                colors.onSecondaryColor,
//
//                            uncheckedTrackColor =
//                                colors.onBackgroundColor,
//
//                            uncheckedBorderColor =
//                                colors.transparent,
//
//                            checkedBorderColor =
//                                colors.transparent
//                        )
//                    )
//                }
//            )
//        }
//    }
//
//    LaunchedEffect(showReminderDialog) {
//
//        Log.d(
//            "ATTENDANCE_REMINDER",
//            "showReminderDialog = $showReminderDialog"
//        )
//    }
//
//    /*
//     * Main Attendance Reminder explanation
//     */
//    if (showReminderDialog) {
//
//        MyDialog(
//
//            onConfirm = {
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "Explanation dialog OK clicked"
//                )
//
//                showReminderDialog = false
//
//                // -----------------------------------------
//                // 1. Check Notification Permission
//                // -----------------------------------------
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//
//                    val notificationGranted =
//                        ContextCompat.checkSelfPermission(
//                            context,
//                            Manifest.permission.POST_NOTIFICATIONS
//                        ) == PackageManager.PERMISSION_GRANTED
//
//                    Log.d(
//                        "ATTENDANCE_REMINDER",
//                        "Notification permission granted = $notificationGranted"
//                    )
//
//                    if (!notificationGranted) {
//
//                        Log.d(
//                            "ATTENDANCE_REMINDER",
//                            "Requesting notification permission..."
//                        )
//
//                        notificationPermissionLauncher.launch(
//                            Manifest.permission.POST_NOTIFICATIONS
//                        )
//
//                        return@MyDialog
//                    }
//                }
//
//                // -----------------------------------------
//                // 2. Check Location Permission
//                // -----------------------------------------
//
//                val fineGranted =
//                    ContextCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) == PackageManager.PERMISSION_GRANTED
//
//                val coarseGranted =
//                    ContextCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) == PackageManager.PERMISSION_GRANTED
//
//                val locationGranted =
//                    fineGranted || coarseGranted
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "Location permission: fine=$fineGranted, coarse=$coarseGranted"
//                )
//
//                // -----------------------------------------
//                // 3. Already granted
//                // -----------------------------------------
//
//                if (locationGranted) {
//
//                    Log.d(
//                        "ATTENDANCE_REMINDER",
//                        "Location permission already granted"
//                    )
//
//                    isReminderEnabled = true
//
//                    sharedPref.setAttendanceReminderEnabled(true)
//
//                    Log.d(
//                        "ATTENDANCE_REMINDER",
//                        "Starting Attendance Reminder Foreground Service"
//                    )
//
//                    AttendanceReminderForegroundManager.start(context)
//
//                    return@MyDialog
//                }
//
//                // -----------------------------------------
//                // 4. Check Android's rationale
//                // -----------------------------------------
//
//                val activity = context as? Activity
//
//                if (activity == null) {
//
//                    Log.e(
//                        "ATTENDANCE_REMINDER",
//                        "Context is not an Activity"
//                    )
//
//                    return@MyDialog
//                }
//
//                val shouldShowFineRationale =
//                    ActivityCompat.shouldShowRequestPermissionRationale(
//                        activity,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    )
//
//                val shouldShowCoarseRationale =
//                    ActivityCompat.shouldShowRequestPermissionRationale(
//                        activity,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    )
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "shouldShowFineRationale=$shouldShowFineRationale"
//                )
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "shouldShowCoarseRationale=$shouldShowCoarseRationale"
//                )
//
//                // -----------------------------------------
//                // 5. Permission was denied before
//                // -----------------------------------------
//
//                if (
//                    sharedPref.wasLocationPermissionRequested() &&
//                    !shouldShowFineRationale &&
//                    !shouldShowCoarseRationale
//                ) {
//
//                    Log.d(
//                        "ATTENDANCE_REMINDER",
//                        "Location permission was previously denied permanently"
//                    )
//
//                    Log.d(
//                        "ATTENDANCE_REMINDER",
//                        "Opening app settings dialog"
//                    )
//
//                    showLocationSettingsDialog = true
//
//                    return@MyDialog
//                }
//
//                // -----------------------------------------
//                // 6. First request OR Android allows rationale
//                // -----------------------------------------
//
//                Log.d(
//                    "ATTENDANCE_REMINDER",
//                    "Requesting foreground location permission"
//                )
//
//                sharedPref.setLocationPermissionRequested(true)
//
//                locationPermissionLauncher.launch(
//                    arrayOf(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    )
//                )
//            },
//
//            onDismiss = {
//
//                showReminderDialog = false
//                isReminderEnabled = false
//            },
//
//            title = stringResource(
//                R.string.reminders
//            ),
//
//            subtitle = "",
//
//            subtitleContent = {
//
//                Column(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//
//                    Text(
//                        text = buildAnnotatedString {
//
//                            append(
//                                stringResource(
//                                    R.string.attendance_reminder_intro
//                                )
//                            )
//
//                            append("\n\n")
//
//                            withStyle(
//                                SpanStyle(
//                                    fontWeight = FontWeight.Bold,
//                                    color = colors.tertiaryColor
//                                )
//                            ) {
//
//                                append(
//                                    stringResource(
//                                        R.string.check_out_reminder_title
//                                    )
//                                )
//                            }
//
//                            append(" ")
//
//                            append(
//                                stringResource(
//                                    R.string.check_out_reminder_description
//                                )
//                            )
//
//                            append("\n\n")
//
//                            withStyle(
//                                SpanStyle(
//                                    fontWeight = FontWeight.Bold,
//                                    color = colors.tertiaryColor
//                                )
//                            ) {
//
//                                append(
//                                    stringResource(
//                                        R.string.check_in_reminder_title
//                                    )
//                                )
//                            }
//
//                            append(" ")
//
//                            append(
//                                stringResource(
//                                    R.string.check_in_reminder_description
//                                )
//                            )
//
//                            append("\n")
//                        },
//
//                        fontSize = 16.sp,
//                        lineHeight = 23.sp,
//                        color = colors.onBackgroundColor
//                    )
//
//                    Surface(
//                        modifier = Modifier.fillMaxWidth(),
//                        shape = RoundedCornerShape(12.dp),
//                        color = colors.tertiaryColor.copy(
//                            alpha = 0.12f
//                        )
//                    ) {
//
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .padding(
//                                    horizontal = 12.dp,
//                                    vertical = 10.dp
//                                ),
//                            verticalAlignment = Alignment.Top
//                        ) {
//
//                            InfoIcon()
//
//                            Spacer(
//                                modifier = Modifier.width(8.dp)
//                            )
//
//                            Text(
//                                text = stringResource(
//                                    R.string.attendance_reminder_no_api
//                                ),
//                                color = colors.onBackgroundColor,
//                                fontSize = 14.sp,
//                                lineHeight = 20.sp,
//                                modifier = Modifier.weight(1f)
//                            )
//                        }
//                    }
//                }
//            },
//
//            confirmButtonText = stringResource(
//                R.string.ok
//            ),
//
//            dismissButtonText = stringResource(
//                R.string.cancel
//            )
//        )
//    }
//
//
//
//    if (showLocationSettingsDialog) {
//
//        MyDialog(
//
//            onConfirm = {
//
//                showLocationSettingsDialog = false
//
//                val intent = Intent(
//                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                ).apply {
//                    data = "package:${context.packageName}".toUri()
//                }
//
//                context.startActivity(intent)
//            },
//
//            onDismiss = {
//                showLocationSettingsDialog = false
//                isReminderEnabled = false
//                sharedPref.setAttendanceReminderEnabled(false)
//            },
//
//            title = stringResource(R.string.reminders),
//
//            subtitle = "",
//
//            subtitleContent = {
//                Text(
//                    text = "Location permission is required for Attendance Reminder. Please enable Location permission from App Settings.",
//                    color = colors.onBackgroundColor,
//                    fontSize = 16.sp,
//                    lineHeight = 23.sp
//                )
//            },
//
//            confirmButtonText = stringResource(R.string.ok),
//            dismissButtonText = stringResource(R.string.cancel)
//        )
//    }
//}
//
//private fun requestLocationPermission(
//    launcher:
//    androidx.activity.result.ActivityResultLauncher<Array<String>>
//) {
//
//    launcher.launch(
//        arrayOf(
//            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        )
//    )
//}