package net.inspirehub.hr.check_in_out.presentation

import android.Manifest
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.SharedPreferences
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.check_in_out.data.CheckInOutViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.system.exitProcess
import net.inspirehub.hr.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.work.WorkManager
import net.inspirehub.hr.appColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.inspirehub.hr.FullLoading
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Date
import net.inspirehub.hr.BuildConfig
import net.inspirehub.hr.check_in_out.data.checkLocationUpdatesRaw
import net.inspirehub.hr.sign_in.data.SignInApiService
import net.inspirehub.hr.utils.convertToArabicDigits
import com.google.firebase.messaging.FirebaseMessaging
import net.inspirehub.hr.FullButton
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.MySnackBar
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import net.inspirehub.hr.check_in_out.data.LocationTrackingManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.core.net.toUri
import androidx.lifecycle.compose.LocalLifecycleOwner
import net.inspirehub.hr.InfoIcon
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.check_in_out.data.hasBackgroundLocationPermission
import net.inspirehub.hr.check_in_out.data.hasForegroundLocationPermission
import net.inspirehub.hr.settings.data.AttendanceReminderPowerSettings

var timeChangeReceiver: BroadcastReceiver? = null

private fun checkInternetConnection(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork ?: return false
    val capabilities = cm.getNetworkCapabilities(network) ?: return false
    val hasNetwork = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    if (!hasNetwork) return false

    return try {
        Socket().use { socket ->
            socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
            true
        }
    } catch (e: Exception) {
        false
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission", "SuspiciousIndentation", "LocalContextGetResourceValueCall",
    "UseKtx"
)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckInOutScreen(
    navController: NavController,
    viewModel: CheckInOutViewModel = viewModel()

) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val token = sharedPref.getToken() ?: ""
    val currentLat by viewModel.currentLat.collectAsState()
    val currentLng by viewModel.currentLng.collectAsState()
    val isWithinDistance by viewModel.isWithinDistance.collectAsState()
    var isDialogLoading by remember { mutableStateOf(false) }
    val lastCheckIn by viewModel.lastCheckIn.collectAsState()
    val lastCheckOut by viewModel.lastCheckOut.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    var offlineMessage by remember { mutableStateOf("") }
    var showInternetRequiredDialog by remember { mutableStateOf(false) }
    var isOffline by remember { mutableStateOf(false) }
    var isButtonLoading by remember { mutableStateOf(true) }
    val workedHours by viewModel.workedHours.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val attendanceStatus by viewModel.attendanceStatus.collectAsState()
    var isInitialLoading by remember { mutableStateOf(false) }
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    var isGpsEnabled by remember { mutableStateOf(false) }
    var showGpsDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current
    var showNotAllowedDialog by remember { mutableStateOf(false) }
    val isAllowedLocation by viewModel.isAllowedLocation.collectAsState()
    val isFakeLocation by viewModel.isFakeLocation.collectAsState()
    var showErrorMessageDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isErrorDialogLoading by remember { mutableStateOf(false) }
    var showFakeLocationDialog by remember { mutableStateOf(isFakeLocation) }
    val employeeFullName = sharedPref.getEmployeeName() ?: "User"
    val employeeFirstName = employeeFullName.split(" ").firstOrNull() ?: employeeFullName
    val scope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    val locationAccuracy by viewModel.locationAccuracy.collectAsState()
    var showBatteryDialog by remember { mutableStateOf(false) }
    var showBackgroundLocationDialog by remember { mutableStateOf(false) }
    var awaitingBatteryResult by remember { mutableStateOf(false) }
    var isBatteryUnrestricted by remember { mutableStateOf(AttendanceReminderPowerSettings.isBatteryUnrestricted(context))}
    var showBatteryWarning by remember { mutableStateOf(false) }


    fun hasAllLocationPermissions(context: Context): Boolean {

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

        if (!fineGranted && !coarseGranted) {
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val backgroundGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (!backgroundGranted) {
                return false
            }
        }

        return true
    }

    fun startLocationTrackingIfPossible() {

        val permissionsGranted = hasAllLocationPermissions(context)

        if (!permissionsGranted) {

            Log.d("CHECK IN_LOCATION_TRACKING", "❌ Cannot start tracking → location permissions missing")

            return
        }

        val isTracked = sharedPref.getIsTracked()

        Log.d("CHECK IN_LOCATION_TRACKING", "Permissions OK | isTracked=$isTracked")

        if (isTracked) {

            Log.d("CHECK IN_LOCATION_TRACKING", "🚀 Starting / updating location tracking")

            LocationTrackingManager.updateTracking(context)

        } else {

            Log.d("CHECK IN_LOCATION_TRACKING", "⏸ Tracking not started because isTracked=false")
        }
    }

    fun continueAfterLocationGranted() {

        val companies = sharedPref.getCompaniesLatLng()
        val allowedIds = sharedPref.getAllowedLocationsIds()

        viewModel.startLocationUpdates(
            companies = companies,
            allowedLocationIds = allowedIds
        )

        val hasLocationPermission = hasAllLocationPermissions(context)

        Log.d("CHECK IN_PERMISSION_FLOW", "continueAfterLocationGranted() → location=$hasLocationPermission")

        if (!hasLocationPermission) {

            Log.d("CHECK IN_PERMISSION_FLOW", "❌ Location permissions are not complete")

            return
        }

        isBatteryUnrestricted =
            AttendanceReminderPowerSettings.isBatteryUnrestricted(context)

        Log.d("CHECK IN_BATTERY", "Battery unrestricted = $isBatteryUnrestricted")

        if (isBatteryUnrestricted) {

            Log.d("CHECK IN_BATTERY", "✅ Battery already unrestricted → starting tracking")

            startLocationTrackingIfPossible()

        } else {

            Log.d("CHECK IN_BATTERY", "⚠️ Battery restricted → showing battery dialog")

            showBatteryDialog = true
        }
    }

    val backgroundPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            Log.d("CHECK IN_LOCATION_PERMISSION", "Background permission result = $isGranted")

            if (isGranted) {

                Log.d("CHECK IN_LOCATION_PERMISSION", "✅ Background location GRANTED")

                showBackgroundLocationDialog = false

                continueAfterLocationGranted()

            } else {

                Log.d("CHECK IN_LOCATION_PERMISSION", "❌ Background location DENIED")

                showBackgroundLocationDialog = false
            }
        }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            val locationGranted = fineGranted || coarseGranted

            Log.d("CHECK IN_LOCATION_PERMISSION", "Foreground result = $permissions")

            if (!locationGranted) {

                Log.d("CHECK IN_LOCATION_PERMISSION", "❌ Foreground location DENIED")

                showBackgroundLocationDialog = false

                return@rememberLauncherForActivityResult
            }

            Log.d("CHECK IN_LOCATION_PERMISSION", "✅ Foreground location GRANTED")

            /*
             * Android 10:
             * Background location can be requested directly.
             */
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

                Log.d("CHECK IN_LOCATION_PERMISSION", "Android 10 → requesting background location")

                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

                /*
                 * Android 11+:
                 * Background location must be enabled from Settings.
                 */
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                Log.d("CHECK IN_LOCATION_PERMISSION", "Android 11+ → showing background location dialog")

                showBackgroundLocationDialog = sharedPref.getIsTracked()

            } else {

                continueAfterLocationGranted()
            }
        }

    val batteryExemptionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {

            awaitingBatteryResult = false

            isBatteryUnrestricted = AttendanceReminderPowerSettings.isBatteryUnrestricted(context)

            Log.d("CHECK IN_BATTERY", "Battery exemption result → unrestricted = $isBatteryUnrestricted")

            if (isBatteryUnrestricted) {

                Log.d("CHECK IN_BATTERY", "✅ Battery optimization disabled")
                showBatteryWarning = false
            } else {

                Log.d("CHECK IN_BATTERY", "⚠️ Battery optimization still enabled")
                showBatteryWarning = true
            }

            /*
             * In both cases we continue.
             * The user is not blocked from Check In / Check Out.
             */
            startLocationTrackingIfPossible()
        }

    fun launchBatteryExemption() {

        awaitingBatteryResult = true

        runCatching {

            batteryExemptionLauncher.launch(
                AttendanceReminderPowerSettings.exemptionIntent(context)
            )

        }.onFailure {

            Log.w("CHECK IN_BATTERY", "Direct battery exemption unavailable → opening battery settings", it)

            runCatching {

                batteryExemptionLauncher.launch(
                    AttendanceReminderPowerSettings.batterySettingsIntent()
                )

            }.onFailure { noSettings ->

                awaitingBatteryResult = false

                Log.e("CHECK IN_BATTERY", "❌ No battery settings screen available", noSettings)

                /*
                 * Even if settings cannot be opened,
                 * continue with tracking.
                 */
                startLocationTrackingIfPossible()
            }
        }
    }

    val backgroundLocationPermissionState =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            rememberPermissionState(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        } else {
            null
        }

    LaunchedEffect(Unit) {

        val fineGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val backgroundGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }

        Log.d("CHECK IN_LOCATION_PERMISSION", "========== INITIAL PERMISSION CHECK ==========")

        Log.d("CHECK IN_LOCATION_PERMISSION", "Fine = $fineGranted")

        Log.d("CHECK IN_LOCATION_PERMISSION", "Coarse = $coarseGranted")

        Log.d("CHECK IN_LOCATION_PERMISSION", "Background = $backgroundGranted")

        /*
         * 1. Foreground missing
         */
        if (!fineGranted && !coarseGranted) {

            Log.d("CHECK IN_LOCATION_PERMISSION", "❌ Foreground missing → requesting")

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )

            return@LaunchedEffect
        }

        /*
         * 2. Foreground exists but Background missing
         */
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            !backgroundGranted
        ) {

            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

                Log.d("CHECK IN_LOCATION_PERMISSION", "Android 10 → requesting background")

                backgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)

            } else {

                Log.d("CHECK IN_LOCATION_PERMISSION", "Android 11+ → showing background dialog")

                showBackgroundLocationDialog = sharedPref.getIsTracked()
            }

            return@LaunchedEffect
        }

        /*
         * 3. Everything granted
         */
        Log.d("CHECK IN_LOCATION_PERMISSION", "✅ All location permissions granted")

        continueAfterLocationGranted()
    }

    /*
     * is_tracked can flip while this screen is already on display: an FCM config
     * update writes it from a background coroutine. The checks above all run on
     * entry or on resume, so without this the employee the backend has just started
     * tracking is never asked for background location - the request waits until the
     * next time they leave the screen and come back.
     */
    DisposableEffect(Unit) {

        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, key ->

                if (key != SharedPrefManager.KEY_IS_TRACKED) {
                    return@OnSharedPreferenceChangeListener
                }

                // Written on whichever thread saved the config, so the state below
                // has to be touched from the main thread.
                scope.launch {

                    val isTracked = sharedPref.getIsTracked()

                    Log.d(
                        "CHECK IN_LOCATION_PERMISSION",
                        "is_tracked changed under the screen -> $isTracked"
                    )

                    if (!isTracked) {

                        showBackgroundLocationDialog = false

                        return@launch
                    }

                    if (!hasForegroundLocationPermission(context)) {

                        Log.d(
                            "CHECK IN_LOCATION_PERMISSION",
                            "Foreground location missing -> the resume check will ask"
                        )

                        return@launch
                    }

                    if (!hasBackgroundLocationPermission(context)) {

                        showBackgroundLocationDialog = true

                        return@launch
                    }

                    startLocationTrackingIfPossible()
                }
            }

        sharedPref.registerChangeListener(listener)

        onDispose {
            sharedPref.unregisterChangeListener(listener)
        }
    }

    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_RESUME) {

                Log.d("CHECK IN_PERMISSION_FLOW", "========== ON_RESUME ==========")

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

                val backgroundGranted =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }

                isBatteryUnrestricted = AttendanceReminderPowerSettings.isBatteryUnrestricted(context)

                Log.d("CHECK IN_PERMISSION_FLOW", "Fine=$fineGranted | Coarse=$coarseGranted | Background=$backgroundGranted")

                Log.d("CHECK IN_BATTERY", "Unrestricted=$isBatteryUnrestricted")

                /*
                 * Foreground location missing
                 */
                if (!fineGranted && !coarseGranted) {

                    Log.d("CHECK IN_PERMISSION_FLOW", "❌ Foreground permission still missing")

                    return@LifecycleEventObserver
                }

                /*
                 * Background location missing
                 */
                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    !backgroundGranted
                ) {

                    Log.d("CHECK IN_PERMISSION_FLOW", "⚠️ Background permission still missing")

                    showBackgroundLocationDialog = sharedPref.getIsTracked()

                    return@LifecycleEventObserver
                }

                /*
                 * All location permissions granted
                 */
                Log.d("CHECK IN_PERMISSION_FLOW", "✅ All location permissions granted")

                showBackgroundLocationDialog = false

                /*
                 * Battery settings were opened.
                 *
                 * Do not open the battery dialog again while the
                 * system settings activity is still returning.
                 */
                if (awaitingBatteryResult) {

                    Log.d("CHECK IN_BATTERY", "Waiting for battery launcher result")

                    return@LifecycleEventObserver
                }

                /*
                 * If battery is already unrestricted,
                 * start/update tracking.
                 */
                if (isBatteryUnrestricted) {

                    Log.d("CHECK IN_BATTERY", "✅ Battery unrestricted → continue tracking")

                    startLocationTrackingIfPossible()

                } else {

                    /*
                     * Battery restricted.
                     *
                     * Show dialog only if we are not already showing it.
                     */
                    if (!showBatteryDialog) {

                        Log.d("CHECK IN_BATTERY", "⚠️ Battery restricted → showing battery dialog")

                        showBatteryDialog = true
                    }
                }

                /*
                 * GPS status
                 */
                val gpsStatus = locationManager.isProviderEnabled(
                        LocationManager.GPS_PROVIDER
                    )

                isGpsEnabled = gpsStatus

                if (!gpsStatus) {

                    Log.d("GPS_STATUS", "❌ GPS is OFF")

                    showGpsDialog = true

                } else {

                    Log.d("GPS_STATUS", "✅ GPS is ON")

                    showGpsDialog = false
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }


    LaunchedEffect(Unit) {
        Log.d("TOKEN", "Stored Token: $token")
    }

    DisposableEffect(isWithinDistance) {
        Log.d("disable", "changed -> $isWithinDistance")
        onDispose { }
    }

    @Composable
    fun Modifier.noClickable(): Modifier = this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) {
        // no click effect
    }


    fun formatAttendanceDateTime(dateTimeString: String): Pair<String, String> {
        return try {
            val inputFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

            // Server time is UTC
            val utcDateTime = LocalDateTime.parse(
                dateTimeString,
                inputFormatter
            )

            // Convert UTC to device local time
            val localDateTime = ZonedDateTime
                .of(utcDateTime, ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.systemDefault())
                .toLocalDateTime()

            val localDate = localDateTime.toLocalDate()
            val localTime = localDateTime.toLocalTime()

            val currentLocale = Locale.getDefault()
            val isArabic = currentLocale.language == "ar"

            val timeFormatter = DateTimeFormatter.ofPattern(
                "h:mm a",
                currentLocale
            )

            val timeText = localTime.format(timeFormatter)

            val today = LocalDate.now()

            val dateText = when {
                localDate == today -> {
                    if (isArabic) {
                        "اليوم"
                    } else {
                        "Today"
                    }
                }

                localDate == today.minusDays(1) -> {
                    if (isArabic) {
                        "أمس"
                    } else {
                        "Yesterday"
                    }
                }

                else -> {
                    val dateFormatter = DateTimeFormatter.ofPattern(
                        "d MMMM yyyy",
                        if (isArabic) Locale("ar") else Locale.ENGLISH
                    )

                    localDate.format(dateFormatter)
                }
            }

            val finalDate = if (isArabic) {
                convertToArabicDigits(dateText)
            } else {
                dateText
            }

            val finalTime = if (isArabic) {
                convertToArabicDigits(timeText)
            } else {
                timeText
            }

            Pair(finalDate, finalTime)

        } catch (e: Exception) {
            Pair("--/--/----", "--:--")
        }
    }


    val colors = appColors()

    val checkInDateTime = lastCheckIn?.let {
        formatAttendanceDateTime(it)
    } ?: Pair("--/--/----", "--:--")

    val checkOutDateTime = lastCheckOut?.let {
        formatAttendanceDateTime(it)
    } ?: Pair("--/--/----", "--:--")



    LaunchedEffect(Unit) {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { fcmToken ->
            Log.d("TEST_FCM_TOKEN", "🔥 CURRENT FCM TOKEN = $fcmToken")

            scope.launch {
                SignInApiService.sendDeviceToken(
                    employeeToken = token,
                    mobileToken = fcmToken
                )
            }
        }  .addOnFailureListener { e ->

            Log.e(
                "TEST_FCM_TOKEN",
                "❌ Failed to get FCM token",
                e
            )
        }

        val response = checkLocationUpdatesRaw(context, token)
        println("📍 FINAL RESPONSE Update location: $response")

        if (response == null) return@LaunchedEffect

        try {
            val json = org.json.JSONObject(response)
            val result = json.getJSONObject("result")

            val changed = result.optBoolean("changed", false)

            println("Update location: 📦 BEFORE UPDATE:")

            println("Update location: Allowed IDs (old): ${sharedPref.getAllowedLocationsIds()}")
            println("Update location: Companies (old): ${sharedPref.getCompaniesLatLng()}")


            if (!changed) {
                println("Update location:📍 No changes in locations")
                return@LaunchedEffect
            }

            println("Update location:✅ Locations changed → updating...")

            // ✅ 1. allowed_locations_ids
            val idsJson = result.optJSONArray("allowed_locations_ids")
            val idsList = mutableListOf<Int>()

            if (idsJson != null) {
                for (i in 0 until idsJson.length()) {
                    idsList.add(idsJson.getInt(i))
                }
            }

            sharedPref.saveAllowedLocationsIds(idsList)

            // ✅ 2. company_locations
            val companiesJson = result.getJSONArray("company_locations")

            val companies = mutableListOf<net.inspirehub.hr.sign_in.data.Company>()

            for (i in 0 until companiesJson.length()) {
                val item = companiesJson.getJSONObject(i)
                val name = item.getString("name")

                val address = item.getJSONObject("address")

                val company = net.inspirehub.hr.sign_in.data.Company(
                    name = name,
                    address = net.inspirehub.hr.sign_in.data.Address(
                        id = address.getInt("id"),
                        street = address.optString("street", ""),
                        city = address.optString("city", ""),
                        zip = address.optString("zip", ""),
                        country = address.optString("country", ""),
                        latitude = address.getDouble("latitude"),
                        longitude = address.getDouble("longitude"),
                        allowed_distance = address.getDouble("allowed_distance")
                    )
                )

                companies.add(company)
            }

            sharedPref.saveCompaniesLatLng(companies)

            println("Update location: 🆕 AFTER UPDATE:")

            println("Update location: Allowed IDs (new): ${sharedPref.getAllowedLocationsIds()}")
            println("Update location: Companies (new): ${sharedPref.getCompaniesLatLng()}")

            println("Update location: ✅ Locations saved successfully")

        } catch (e: Exception) {
            println("Update location: 🔴 Error parsing update locations: ${e.message}")
        }
    }


    LaunchedEffect(Unit) {
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        Log.d("GPS_STATUS", "📍 GPS Enabled: $isGpsEnabled")
        Log.d("token", token)

        if (!isGpsEnabled) {
            println("❌ GPS is turned OFF")
            showGpsDialog = true
        } else {
            println("✅ GPS is ON")
        }
    }

// ✅ Every time the user returns to the application (from settings or any other screen)
    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_RESUME) {

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

                val backgroundGranted =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    } else {
                        true
                    }

                Log.d(
                    "TEST LOCATION_TRACKING",
                    "🔄 ON_RESUME | fine=$fineGranted | coarse=$coarseGranted | background=$backgroundGranted"
                )

                // Foreground location missing
                if (!fineGranted && !coarseGranted) {

                    Log.d(
                        "TEST LOCATION_TRACKING",
                        "❌ Foreground location still missing"
                    )

                    return@LifecycleEventObserver
                }

                // Foreground موجود لكن Background مش موجود
                if (
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    !backgroundGranted
                ) {

                    Log.d(
                        "TEST LOCATION_TRACKING",
                        "⚠️ Background location still missing"
                    )

                    showBackgroundLocationDialog = sharedPref.getIsTracked()

                    return@LifecycleEventObserver
                }

                // كل permissions موجودة
                Log.d(
                    "TEST LOCATION_TRACKING",
                    "✅ All permissions granted"
                )

                showBackgroundLocationDialog = false

                val isTracked = sharedPref.getIsTracked()

                Log.d(
                    "TEST LOCATION_TRACKING",
                    "🚀 ON_RESUME → isTracked=$isTracked"
                )

                if (isTracked) {
                    LocationTrackingManager.updateTracking(context)
                }

                // GPS
                val gpsStatus =
                    locationManager.isProviderEnabled(
                        LocationManager.GPS_PROVIDER
                    )

                isGpsEnabled = gpsStatus

                if (!gpsStatus) {
                    showGpsDialog = true
                } else {
                    showGpsDialog = false
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }




    LaunchedEffect(Unit) {
        val connected = withContext(Dispatchers.IO) { checkInternetConnection(context) }
        isOffline = !connected

        if (connected) {
            isInitialLoading = true
            viewModel.syncOfflineData(token)
        }

        while (true) {
            val stillConnected = withContext(Dispatchers.IO) { checkInternetConnection(context) }
            isOffline = !stillConnected
            delay(3000)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.startPollingAttendance(token)
    }


    LaunchedEffect(attendanceStatus, lastCheckIn, workedHours) {
        // First time logging in and no data coming from the server
        if (attendanceStatus == null && lastCheckIn == null && workedHours == null) {
            isInitialLoading = false
            isButtonLoading = false
            return@LaunchedEffect
        }

        // When the server returns AttendStatus only → Dough to unblock the loading
        if (attendanceStatus != null) {
            delay(300)
            isInitialLoading = false
            isButtonLoading = false
        }
    }


    DisposableEffect(Unit) {

        onDispose {
            viewModel.stopLocationUpdates()
        }
    }

    val notificationPermission =
        rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

    LaunchedEffect(Unit) {
        if (!notificationPermission.status.isGranted) {
            notificationPermission.launchPermissionRequest()
        }
    }
    BackHandler(enabled = true) {
        exitProcess(0)
    }

    LaunchedEffect(offlineMessage) {
        if (offlineMessage.isNotEmpty()) {
            snackBarHostState.showSnackbar(
                message = offlineMessage,
                duration = SnackbarDuration.Long
            )
            offlineMessage = ""
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState,
                ) { data ->

                    MySnackBar(
                        snackBarData = data,
                        useOffset = false
                    )
                }
            },
            bottomBar = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Last Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                        modifier = Modifier.padding(start = 12.dp),
                        color = colors.onBackgroundColor.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
//                    Text(
//                        text = locationAccuracy?.let {
//                            "new Location accuracy: ${"%.1f".format(Locale.US, it)} m"
//                        } ?: "Location accuracy: --",
//                        fontSize = 12.sp,
//                        color = colors.onBackgroundColor.copy(alpha = 0.6f),
//                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp),
//                    )
                    BottomBar(navController = navController)
                }
            }
        )
        { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colors.onSecondaryColor)
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues())
                    .padding(WindowInsets.statusBars.asPaddingValues())
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    when (attendanceStatus) {
                        "checked_in" -> "${stringResource(R.string.welcome)} $employeeFirstName\n${
                            stringResource(
                                R.string.you_are_checked_in
                            )
                        }"

                        "checked_out" -> "${stringResource(R.string.welcome)} $employeeFirstName\n${
                            stringResource(
                                R.string.you_are_checked_out
                            )
                        }"

                        else -> "${stringResource(R.string.welcome)} $employeeFirstName\n${
                            stringResource(
                                R.string.loading
                            )
                        }"
                    },
                    color = colors.tertiaryColor,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge,
                )

                Image(
                    painter = painterResource(id = R.drawable.check_in_out),
                    contentDescription = attendanceStatus
                )

                if (isOffline) {

                    Text(
                        text = stringResource(
                            R.string.you_are_currently_offline_your_action_will_be_saved_and_sent_once_the_internet_is_available
                        ),
                        color = colors.error,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                    )

                } else {

                    when (attendanceStatus) {

                        "checked_in" -> {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                // Main message
                                Text(
                                    text = stringResource(
                                        R.string.you_have_successfully_checked_in_at
                                    ),
                                    color = colors.onBackgroundColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )

                                // Date
                                Text(
                                    text = checkInDateTime.first,
                                    color = colors.onBackgroundColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )

                                // Time
                                Text(
                                    text = checkInDateTime.second,
                                    color = colors.onBackgroundColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )

                                // Footer message
                                Text(
                                    text = stringResource(
                                        R.string.have_a_blessed_day
                                    ),
                                    color = colors.onBackgroundColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        "checked_out" -> {

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {

                                // Main message
                                Text(
                                    text = stringResource(
                                        R.string.you_have_successfully_checked_out_on
                                    ),
                                    color = colors.onBackgroundColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )

                                // Date
                                Text(
                                    text = checkOutDateTime.first,
                                    color = colors.onBackgroundColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )

                                // Time
                                Text(
                                    text = checkOutDateTime.second,
                                    color = colors.onBackgroundColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )

                                // Footer message
                                Text(
                                    text = stringResource(
                                        R.string.great_job
                                    ),
                                    color = colors.onBackgroundColor,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        else -> {

                            Text(
                                text = stringResource(R.string.loading),
                                color = colors.onBackgroundColor,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
//                Log.d("disable", "isWithinDistance from state: $isWithinDistance")

                if (isWithinDistance == false) {
                    Text(
                        text = stringResource(R.string.outside_company_range),
                        color = colors.error,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }

                if (showBatteryWarning && !isBatteryUnrestricted) {

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 8.dp
                            )
                            .clickable {
                                launchBatteryExemption()
                            },
                        shape = RoundedCornerShape(16.dp),
                        color = colors.transparent,
                        border = BorderStroke(
                            1.dp,
                            colors.error.copy(alpha = 0.25f)
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 12.dp
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(12.dp),
                                color = colors.error.copy(alpha = 0.12f)
                            ) {

                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {

                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = colors.error,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(
                                modifier = Modifier.width(12.dp)
                            )

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = stringResource(
                                        R.string.battery_optimization_warning_title
                                    ),
                                    color = colors.error,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(
                                    modifier = Modifier.height(3.dp)
                                )

                                Text(
                                    text = stringResource(
                                        R.string.battery_optimization_warning_message
                                    ),
                                    color = colors.onBackgroundColor.copy(alpha = 0.75f),
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = stringResource(
                                    R.string.battery_optimization_allow
                                ),
                                color = colors.tertiaryColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }


                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    FullButton(
                        label = when (attendanceStatus) {
                            "checked_in" -> stringResource(R.string.check_out)
                            "checked_out" -> stringResource(R.string.check_in)
                            else -> "..."
                        },
                        enabled = isWithinDistance == true,
                        isLoading = isButtonLoading || isWithinDistance == null,
                        containerColor =
                            if (attendanceStatus == "checked_in")
                                colors.tertiaryColor
                            else
                                colors.onSecondaryColor,
                        contentColor =
                            if (attendanceStatus == "checked_in")
                                colors.onSecondaryColor
                            else
                                colors.tertiaryColor,
                        border =
                            if (isWithinDistance == true)
                                BorderStroke(2.dp, colors.tertiaryColor)
                            else null,
                        onClick = {
                            if (!isButtonLoading) {
                                coroutineScope.launch {

                                    val now = Date()

                                    if (isOffline) {
                                        val lastActionTime = sharedPref.getLastOfflineActionTime()
                                            ?: Date(0) // Or get the latest offline transaction
                                        val diffMinutes =
                                            ((now.time - lastActionTime.time) / 60000).toInt() // Difference in minutes

                                        if (diffMinutes < 1) {

                                            // If the difference is less than a minute → Prevent pressure
                                            offlineMessage =
                                                context.getString(R.string.wait_one_minute)
                                            return@launch
                                        }
                                    }

                                    // ️Perform an immediate re-check of the site.
                                    val companies = sharedPref.getCompaniesLatLng()
                                    val allowedIds = sharedPref.getAllowedLocationsIds()
                                    if (isWithinDistance != true) {
                                        showNotAllowedDialog = true
                                        isButtonLoading = false
                                        return@launch
                                    }


                                    val locationManager =
                                        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                                    val gpsEnabled =
                                        locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

                                    if (!gpsEnabled) {
                                        Log.d("GPS_STATUS", "❌ GPS is OFF when button clicked")
                                        showGpsDialog = true
                                        return@launch
                                    }

                                    isButtonLoading = true
                                    isErrorDialogLoading = true

                                    val sharedPref = SharedPrefManager(context)
                                    val token = sharedPref.getToken()
                                    val offline = viewModel.isOffline()
                                    val wasOfflineDuringChange =
                                        sharedPref.wasOfflineDuringTimeChange()
                                    val diffMinutes = sharedPref.getTimeDifference()
                                    Log.d(
                                        "CheckInOut",
                                        "🌐 Online status: ${if (offline) "Offline" else "Online"}"
                                    )
                                    Log.d("CheckInOut", "🕒 Saved time difference: $diffMinutes")
                                    Log.d(
                                        "CheckInOut",
                                        "⚡ Change time while offline: $wasOfflineDuringChange"
                                    )

                                    val nextAction =
                                        if (attendanceStatus == "checked_in") "check_out" else "check_in"

                                    val nextStatus =
                                        if (nextAction == "check_in") "checked_in" else "checked_out"

                                    Log.d("CheckInOut", "📌 Next Action: $nextAction")
                                    Log.d("CheckInOut", "🌐 Offline: $isOffline")
                                    Log.d("CheckInOut", "🕒 Device Time: ${Date()}")



                                    Log.d("CheckInOutDebug", "🔹 Button clicked")
                                    Log.d(
                                        "CheckInOutDebug",
                                        "Current attendanceStatus: $attendanceStatus"
                                    )
                                    Log.d("CheckInOutDebug", "Is Offline: $isOffline")

                                    Log.d("CheckInOutDebug", "Next action determined: $nextAction")


                                    // 🔹 Case 1: User changed the time while offline → Forbidden to execute
                                    if (isOffline && wasOfflineDuringChange) {
                                        showInternetRequiredDialog = true
                                        isButtonLoading = false
                                        return@launch
                                    }

                                    // 🔹 Case 2: Offline → queue the punch and stop.
                                    //
                                    // This branch MUST return. Everything below it is the online
                                    // path, and getTimeDifferenceWithServer invokes its callback
                                    // even when the server is unreachable — falling through from
                                    // here queued the punch a second time and stored a bogus clock
                                    // skew from a request that never completed.
                                    if (isOffline) {
                                        if (nextAction == "check_in") {
                                            // sendAttendance owns the queue write: offline it saves
                                            // one offline_logs row (UTC-formatted) and schedules the
                                            // drain. Writing the row here as well produced duplicates
                                            // in a second timestamp format.
                                            viewModel.sendAttendance(token!!, nextAction) {
                                                isButtonLoading = false
                                                isErrorDialogLoading = false
                                            }

                                            // Live attendance update in the UI
                                            viewModel.setAttendanceStatus(nextStatus)
                                            sharedPref.saveLastOfflineActionTime(Date())

                                            Log.d(
                                                "CheckInOutDebug",
                                                "AttendanceStatus after setAttendanceStatus(): $attendanceStatus"
                                            )

                                            // Message to the user
                                            offlineMessage =
                                                context.getString(R.string.offline_saved_message)
                                        } else {
                                            // check_out is confirmed first; the queue write happens
                                            // in the dialog's onConfirm.
                                            showErrorDialog = true
                                            isButtonLoading = false
                                            isErrorDialogLoading = false
                                        }

                                        return@launch
                                    }


                                    // 🔹 Case 3: User is online → Calculate the time difference with the server
                                    viewModel.getTimeDifferenceWithServer(token!!) { diff ->
                                        Log.d(
                                            "CheckInOut",
                                            "🕒 Time difference with server (minutes): $diff"
                                        )

                                        // null = the server was not reached. Connectivity can drop
                                        // between the isOffline check and this call, so treat it as
                                        // offline: queue the punch instead of persisting a clock
                                        // skew and clearing the time-tamper flag on a failed call.
                                        if (diff == null) {
                                            Log.w(
                                                "CheckInOut",
                                                "⚠️ Server unreachable during time check → queueing offline"
                                            )
                                            if (nextAction == "check_out") {
                                                showErrorDialog = true
                                                isButtonLoading = false
                                                isErrorDialogLoading = false
                                            } else {
                                                viewModel.sendAttendance(token, nextAction) {
                                                    isButtonLoading = false
                                                    isErrorDialogLoading = false
                                                }
                                                viewModel.setAttendanceStatus(nextStatus)
                                                sharedPref.saveLastOfflineActionTime(Date())
                                                offlineMessage =
                                                    context.getString(R.string.offline_saved_message)
                                            }
                                            return@getTimeDifferenceWithServer
                                        }

                                        sharedPref.saveTimeDifference(diff)
                                        sharedPref.setWasOfflineDuringTimeChange(false)
                                        Log.d(
                                            "CheckInOut",
                                            "🕒 New time difference with server: $diff min"
                                        )
                                        val deviceDate = Date()
                                        val finalActionTime = Date(deviceDate.time + diff * 60_000)
                                        Log.d(
                                            "CheckInOut",
                                            "🕒 Final Action Time (to send): $finalActionTime"
                                        )


                                        // 🔹 After checking the time, we start implementing the procedure.
                                        if (nextAction == "check_out") {
                                            isDialogLoading = true
                                            viewModel.sendAttendance(token, "status") { newStatus ->
                                                isDialogLoading = false
                                                isButtonLoading = false
                                                isErrorDialogLoading = false
                                                if (newStatus != null) {
                                                    println("✅ Check Out sent successfully: $newStatus")
                                                    showErrorDialog = true
                                                } else {
                                                    errorMessage = viewModel.message.value.ifEmpty {
                                                        context.getString(R.string.error)
                                                    }
                                                    showErrorMessageDialog = true
                                                }
                                            }
                                        } else {
                                            viewModel.sendAttendance(
                                                token,
                                                nextAction
                                            ) { newStatus ->
                                                isButtonLoading = false
                                                isErrorDialogLoading = false
                                                Log.d(
                                                    "ReminderDebug",
                                                    "✅ Reminder scheduled after ONLINE check-in"
                                                )

                                                if (newStatus != null) {
                                                    // ✅ Success
                                                    if (!isAllowedLocation) {
                                                        showNotAllowedDialog = true
                                                    }
                                                } else {
                                                    errorMessage = viewModel.message.value
                                                    showErrorMessageDialog = true
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                        }
                    )



                    if (isErrorDialogLoading) {
                        FullLoading()
                    }
                }
            }
        }

        if (showFakeLocationDialog) {
            MyDialog(
                title = stringResource(R.string.invalid_request),
                subtitle = stringResource(R.string.a_fake_location_was_detected_please_turn_off_fake_gps_to_continue),
                confirmButtonText = stringResource(R.string.ok),
                onConfirm = { showFakeLocationDialog = false },
                onDismiss = { showFakeLocationDialog = false },
            )
        }

        if (showNotAllowedDialog) {
            MyDialog(
                title = stringResource(R.string.attention),
                subtitle = stringResource(R.string.you_are_currently_at_a_different_branch_from_your_registered_one_hr_will_be_informed),
                confirmButtonText = stringResource(R.string.ok),
                onConfirm = {
                    showNotAllowedDialog = false

                },
                onDismiss = { showNotAllowedDialog = false },
            )
        }

        if (isInitialLoading && !isOffline) {
            FullLoading()
        }


        if (showInternetRequiredDialog) {

            MyDialog(
                title = stringResource(R.string.check_not_allowed),
                subtitle = stringResource(R.string.you_have_changed_the_time_while_offline_you_cannot_perform_a_check_operation_until_you_are_back_online),
                confirmButtonText = stringResource(R.string.ok),
                onConfirm = { showInternetRequiredDialog = false },
                onDismiss = { showInternetRequiredDialog = false },
            )
        }
    }

    if (showGpsDialog) {
        MyDialog(
            title = stringResource(R.string.enable_location),
            subtitle = stringResource(R.string.please_enable_gps_so_the_app_can_accurately_detect_your_location),
            confirmButtonText = stringResource(R.string.enable_now),
            dismissButtonText = stringResource(R.string.cancel),
            onConfirm = {
                showGpsDialog = false
                val intent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(intent)
            },
            onDismiss = { showGpsDialog = false },
        )
    }



    if (showErrorMessageDialog) {

        MyDialog(
            title = stringResource(R.string.invalid_request),
            subtitle = errorMessage,
            confirmButtonText = stringResource(R.string.ok),
            onConfirm = { showErrorMessageDialog = false },
            onDismiss = { showErrorMessageDialog = false },
        )
    }

    if (showBackgroundLocationDialog && sharedPref.getIsTracked()) {

        MyDialog(
            title = stringResource(
                R.string.background_location_permission
            ),

            subtitle = " ",

            subtitleContent = {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(
                            R.string.background_location_message
                        ),
                        color = colors.onBackgroundColor,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Image(
                        painter = painterResource(
                            id = R.drawable.background_location_settings
                        ),
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

            confirmButtonText = stringResource(
                R.string.open_settings
            ),

            dismissButtonText = stringResource(
                R.string.cancel
            ),

            onConfirm = {

                Log.d("CHECK IN_LOCATION_PERMISSION", "========== BACKGROUND LOCATION OK ==========")

                Log.d("CHECK IN_LOCATION_PERMISSION", "SDK = ${Build.VERSION.SDK_INT}")

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

                Log.d("CHECK IN_LOCATION_PERMISSION", "Fine = $fineGranted")

                Log.d("CHECK IN_LOCATION_PERMISSION", "Coarse = $coarseGranted")

                /*
                 * Foreground permission missing
                 */
                if (!fineGranted && !coarseGranted) {

                    Log.d("CHECK IN_LOCATION_PERMISSION", "❌ Foreground location missing")

                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )

                    return@MyDialog
                }

                /*
                 * Android 10
                 */
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {

                    Log.d("CHECK IN_LOCATION_PERMISSION", "Android 10 → requesting background permission")

                    showBackgroundLocationDialog = false

                    backgroundPermissionLauncher.launch(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                }

                /*
                 * Android 11+
                 */
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                    Log.d("CHECK IN_LOCATION_PERMISSION", "Android 11+ → opening application settings")

                    showBackgroundLocationDialog = false

                    backgroundPermissionLauncher.launch(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                }
            },

            onDismiss = {

                Log.d("CHECK IN_LOCATION_PERMISSION", "❌ Background location dialog dismissed")

                showBackgroundLocationDialog = false
            }
        )
    }


    if (showBatteryDialog) {

        MyDialog(
            title = stringResource(
                R.string.battery_optimization_title
            ),

            subtitle = " ",

            subtitleContent = {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(
                            R.string.battery_optimization_message
                        ),
                        color = colors.onBackgroundColor,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = colors.tertiaryColor.copy(
                            alpha = 0.12f
                        )
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

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = stringResource(
                                    R.string.battery_optimization_note
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

            confirmButtonText = stringResource(
                R.string.open_settings
            ),

            dismissButtonText = stringResource(
                R.string.cancel
            ),

            onConfirm = {

                Log.d("CHECK IN_BATTERY", "User selected Open Settings")

                showBatteryDialog = false

                launchBatteryExemption()
            },

            onDismiss = {

                Log.d("CHECK IN_BATTERY", "User refused battery exemption")

                showBatteryDialog = false

                // Keep the warning visible because battery optimization is still enabled.
                isBatteryUnrestricted = AttendanceReminderPowerSettings.isBatteryUnrestricted(context)

                if (!isBatteryUnrestricted) { showBatteryWarning = true }
                /*
                 * Do NOT block the Check In / Check Out screen.
                 *
                 * Continue tracking even though the battery optimization
                 * is still enabled.
                 */
                startLocationTrackingIfPossible()
            }
        )
    }




    if (showErrorDialog) {
        MyDialog(
            title = stringResource(R.string.attention),
            subtitle = if (!isOffline) {
                val hoursInt = (workedHours ?: 0.0).toInt()
                LocalContext.current.resources.getQuantityString(
                    R.plurals.check_out_confirmation,
                    hoursInt,
                    workedHours ?: 0.0
                )
            } else {
                // Offline wording: tell the user the punch is queued, not sent.
                stringResource(R.string.are_you_sure_you_want_to_check_out_now_the_operation_will_be_saved_and_sent_when_the_internet_is_available)
            },
            isLoading = isDialogLoading,
            confirmButtonText = stringResource(R.string.ok),
            dismissButtonText = stringResource(R.string.cancel),
            onConfirm = {
                sharedPref.clearCheckOutScheduledTime()
                WorkManager.getInstance(context).cancelAllWorkByTag("check_out_reminder_work")
                isDialogLoading = true

                // Offline, sendAttendance queues the punch and reports "queued". The online
                // result handling below must not also run, or the offline confirmation races
                // the success/error branches of the same call.
                if (isOffline) {
                    viewModel.sendAttendance(token, "check_out") {
                        isDialogLoading = false
                        isButtonLoading = false
                        isErrorDialogLoading = false
                    }
                    showErrorDialog = false
                    showErrorMessageDialog = false
                    viewModel.setAttendanceStatus("checked_out")
                    sharedPref.saveLastOfflineActionTime(Date())
                    offlineMessage = context.getString(R.string.offline_saved_message)
                } else {
                    viewModel.sendAttendance(token, "check_out") { newStatus ->
                        isDialogLoading = false
                        if (newStatus != null) {
                            // ✅ Success
                            println("✅ Forced Check Out with status: $newStatus")
                            showErrorDialog = false

                            if (!isAllowedLocation) {
                                showNotAllowedDialog = true
                            }
                        } else {
                            // ❌  Error
                            errorMessage = viewModel.message.value.ifEmpty {
                                context.getString(R.string.error)
                            }
                            showErrorDialog = false
                            showErrorMessageDialog = true
                        }
                    }
                }
            },
            onDismiss = { showErrorDialog = false }
        )
    }
}