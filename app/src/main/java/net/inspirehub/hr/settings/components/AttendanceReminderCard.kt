package net.inspirehub.hr.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.settings.data.LocalAttendanceReminderManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import net.inspirehub.hr.check_in_out.data.hasBackgroundLocationPermission
import net.inspirehub.hr.InfoIcon
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.Image
import android.util.Log
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import net.inspirehub.hr.MyDivider

@Composable
fun AttendanceReminderCard() {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    var isCheckInReminderEnabled by remember { mutableStateOf(sharedPref.isCheckInReminderEnabled()) }
    var isCheckOutReminderEnabled by remember { mutableStateOf(sharedPref.isCheckOutReminderEnabled()) }
    var showCheckInDialog by remember { mutableStateOf(false) }
    var showCheckOutDialog by remember { mutableStateOf(false) }
    var showPermissionDialog by remember { mutableStateOf(false) }
    var pendingReminderType by remember { mutableStateOf<String?>(null) }

    //    Function: start selected reminder
    fun startPendingReminder() {
        when (pendingReminderType) {
            "CHECK_IN" -> {
                Log.d("ATTENDANCE_REMINDER", "Starting CHECK-IN reminder")

                isCheckInReminderEnabled = true
                LocalAttendanceReminderManager.startCheckIn(context)
            }

            "CHECK_OUT" -> {
                Log.d("ATTENDANCE_REMINDER", "Starting CHECK-OUT reminder")

                isCheckOutReminderEnabled = true
                LocalAttendanceReminderManager.startCheckOut(context)
            }
        }

        pendingReminderType = null
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->

//            if (event == Lifecycle.Event.ON_RESUME) {
//
//                Log.d("LOCATION_PERMISSION", "ON_RESUME")
//
//
////              Only continue if a reminder was waiting for permission.
//                if (pendingReminderType != null) {
//
//                    val backgroundGranted = hasBackgroundLocationPermission(context)
//
//                    Log.d("LOCATION_PERMISSION", "Background granted = $backgroundGranted")
//
//                    if (backgroundGranted) {
//
//                        Log.d("LOCATION_PERMISSION", "Background permission confirmed")
//                        showPermissionDialog = false
//                        startPendingReminder()
//
//                    }
//                }
//            }
            if (event == Lifecycle.Event.ON_RESUME) {

                Log.d("LOCATION_PERMISSION", "ON_RESUME")

                val backgroundGranted = hasBackgroundLocationPermission(context)

                Log.d(
                    "LOCATION_PERMISSION",
                    "Background permission = $backgroundGranted"
                )

                // Permission was revoked from device settings
                if (!backgroundGranted) {

                    if (isCheckInReminderEnabled) {
                        Log.d(
                            "ATTENDANCE_REMINDER",
                            "Check-in reminder disabled because background permission was revoked"
                        )

                        isCheckInReminderEnabled = false
                        LocalAttendanceReminderManager.stopCheckIn(context)
                    }

                    if (isCheckOutReminderEnabled) {
                        Log.d(
                            "ATTENDANCE_REMINDER",
                            "Check-out reminder disabled because background permission was revoked"
                        )

                        isCheckOutReminderEnabled = false
                        LocalAttendanceReminderManager.stopCheckOut(context)
                    }
                }

                // Permission was granted after opening settings
                if (pendingReminderType != null && backgroundGranted) {

                    Log.d(
                        "LOCATION_PERMISSION",
                        "Background permission confirmed"
                    )

                    showPermissionDialog = false
                    startPendingReminder()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

//  Background permission launcher
    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        Log.d(
            "LOCATION_PERMISSION", "Background permission result = $isGranted"
        )

        if (isGranted) {

            Log.d("LOCATION_PERMISSION", "Background location GRANTED")
            showPermissionDialog = false
            startPendingReminder()

        } else {

            Log.d("LOCATION_PERMISSION", "Background location DENIED")

//          Don't enable the switch if permission wasn't granted.
            when (pendingReminderType) {
                "CHECK_IN" -> { isCheckInReminderEnabled = false }
                "CHECK_OUT" -> { isCheckOutReminderEnabled = false }
            }
            pendingReminderType = null
        }
    }

//    Foreground location permission launcher
    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            val locationGranted = fineGranted || coarseGranted

            Log.d("LOCATION_PERMISSION", "Foreground result: $permissions")

            if (!locationGranted) {

                Log.d("LOCATION_PERMISSION", "Foreground location DENIED")

                when (pendingReminderType) {
                    "CHECK_IN" -> { isCheckInReminderEnabled = false }
                    "CHECK_OUT" -> { isCheckOutReminderEnabled = false }
                }
                pendingReminderType = null

                return@rememberLauncherForActivityResult
            }

            Log.d("LOCATION_PERMISSION", "Foreground location GRANTED")

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

                Log.d("LOCATION_PERMISSION", "Android 10 → requesting background")

                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                Log.d("LOCATION_PERMISSION", "Android 11+ → Settings required")

                showPermissionDialog = true
            }
        }




    Column {
        Text(
            text = stringResource(R.string.reminders),
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
                    label = stringResource(R.string.check_in_reminder),
                    icon = Icons.Default.HowToReg,
                    onClick = {
                        if (!isCheckInReminderEnabled) {
//                         User wants to ENABLE check-in
                            pendingReminderType = "CHECK_IN"
                            showCheckInDialog = true
                        } else {
//                            User wants to DISABLE check-in
                            isCheckInReminderEnabled = false
                            LocalAttendanceReminderManager.stopCheckIn(context)
                        }
                    },
                    trailingIcon = {
                        Switch(
                            checked = isCheckInReminderEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    pendingReminderType = "CHECK_IN"
                                    showCheckInDialog = true
                                } else {
                                    isCheckInReminderEnabled = false
                                    LocalAttendanceReminderManager.stopCheckIn(context)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.onSecondaryColor,
                                checkedTrackColor = colors.tertiaryColor,
                                uncheckedThumbColor = colors.onSecondaryColor,
                                uncheckedTrackColor = colors.onBackgroundColor,
                                uncheckedBorderColor = colors.transparent,
                                checkedBorderColor = colors.transparent
                            )
                        )
                    }
                )

                MyDivider(
                    horizontalPadding = 20,
                    color = colors.surfaceColor
                )

                SettingsItem(
                    label = stringResource(R.string.check_out_reminder),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        if (!isCheckOutReminderEnabled) {
//                            User wants to ENABLE check-out
                            pendingReminderType = "CHECK_OUT"
                            showCheckOutDialog = true
                        } else {
//                            User wants to DISABLE check-out
                            isCheckOutReminderEnabled = false
                            LocalAttendanceReminderManager.stopCheckOut(context)
                        }
                    },
                    trailingIcon = {
                        Switch(
                            checked = isCheckOutReminderEnabled,
                            onCheckedChange = { enabled ->
                                if (enabled) {
                                    pendingReminderType = "CHECK_OUT"
                                    showCheckOutDialog = true
                                } else {
                                    isCheckOutReminderEnabled = false
                                    LocalAttendanceReminderManager.stopCheckOut(context)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.onSecondaryColor,
                                checkedTrackColor = colors.tertiaryColor,
                                uncheckedThumbColor = colors.onSecondaryColor,
                                uncheckedTrackColor = colors.onBackgroundColor,
                                uncheckedBorderColor = colors.transparent,
                                checkedBorderColor = colors.transparent
                            )
                        )
                    }
                )
            }
        }
    }

    if (showCheckInDialog) {
        MyDialog(
            onConfirm = {
                showCheckInDialog = false
                pendingReminderType = "CHECK_IN"

//                Background permission already exists
                if (hasBackgroundLocationPermission(context)) {
                    startPendingReminder()
                } else {
//                 Need permission
                    showPermissionDialog = true
                }
            },
            onDismiss = {
                showCheckInDialog = false
                isCheckInReminderEnabled = false
                pendingReminderType = null
            },
            title = stringResource(R.string.check_in_reminder),
            subtitle = "",
            subtitleContent = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.attendance_reminder_intro))
                            append("\n\n")

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.tertiaryColor
                                )
                            ) {
                                append(stringResource( R.string.check_in_reminder_title))
                            }
                            append(" ")
                            append(stringResource(R.string.check_in_reminder_description))

                            append("\n")
                        },
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        color = colors.onBackgroundColor
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.tertiaryColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 10.dp
                                ),
                            verticalAlignment = Alignment.Top
                        ) {
                            InfoIcon()

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(
                                    R.string.attendance_reminder_no_api
                                ),
                                color = colors.onBackgroundColor,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButtonText = stringResource(R.string.ok),

            dismissButtonText = stringResource(R.string.cancel)
        )
    }



    if (showCheckOutDialog) {
        MyDialog(
            onConfirm = {
                showCheckOutDialog = false
                pendingReminderType = "CHECK_OUT"

//                Background permission already exists
                if (hasBackgroundLocationPermission(context)) {
                    startPendingReminder()
                } else {
//                 Need permission
                    showPermissionDialog = true
                }
            },
            onDismiss = {
                showCheckOutDialog = false
                isCheckOutReminderEnabled = false
                pendingReminderType = null
            },
            title = stringResource(R.string.check_out_reminder),
            subtitle = "",
            subtitleContent = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = buildAnnotatedString {
                            append(stringResource(R.string.attendance_reminder_intro))
                            append("\n\n")

                            withStyle(
                                style = SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    color = colors.tertiaryColor
                                )
                            ) {
                                append(stringResource( R.string.check_out_reminder_title))
                            }
                            append(" ")
                            append(stringResource(R.string.check_out_reminder_description))

                            append("\n")
                        },
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        color = colors.onBackgroundColor
                    )
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.tertiaryColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 10.dp
                                ),
                            verticalAlignment = Alignment.Top
                        ) {
                            InfoIcon()

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(
                                    R.string.attendance_reminder_no_api
                                ),
                                color = colors.onBackgroundColor,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            },
            confirmButtonText = stringResource(R.string.ok),

            dismissButtonText = stringResource(R.string.cancel)
        )
    }

    if (showPermissionDialog) {

        MyDialog(
            title = stringResource(R.string.background_location_permission),
            subtitle = " ",
            subtitleContent = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.background_location_message),
                        color = colors.onBackgroundColor,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Image(
                        painter = painterResource(id = R.drawable.background_location_settings),
                        contentDescription = stringResource(
                            R.string.background_location_permission
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                BorderStroke(
                                    1.dp,
                                    colors.inverseOnSurface
                                ),
                                RoundedCornerShape(12.dp)
                            ),
                        contentScale = ContentScale.FillWidth
                    )
                }
            },

            confirmButtonText = stringResource(R.string.open_settings),

            dismissButtonText = stringResource(R.string.cancel),

            onConfirm = {
                Log.d("LOCATION_PERMISSION", "========== BACKGROUND LOCATION OK ==========")
                Log.d("LOCATION_PERMISSION", "SDK = ${Build.VERSION.SDK_INT}")

                val fineGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                val coarseGranted =
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED

                Log.d("LOCATION_PERMISSION", "Fine = $fineGranted")
                Log.d("LOCATION_PERMISSION", "Coarse = $coarseGranted")

                if (!fineGranted && !coarseGranted) {

                    Log.d("LOCATION_PERMISSION", "Foreground location missing")
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )

                    return@MyDialog
                }

                // Foreground location exists
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

                    Log.d("LOCATION_PERMISSION", "Android 10 → requesting background permission")

                    showPermissionDialog = false

                    backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    Log.d("LOCATION_PERMISSION", "Android 11+ → requesting background location")

                    showPermissionDialog = false

                    backgroundPermissionLauncher.launch(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                }
            },

            onDismiss = {
                showPermissionDialog = false
                when (pendingReminderType) {
                    "CHECK_IN" -> { isCheckInReminderEnabled = false }
                    "CHECK_OUT" -> { isCheckOutReminderEnabled = false }
                }
                pendingReminderType = null
                }
        )
    }
}