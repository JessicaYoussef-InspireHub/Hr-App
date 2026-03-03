package   net.inspirehub.hr.check_in_out.presentation

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import net.inspirehub.hr.check_in_out.components.CheckInOutButton
import net.inspirehub.hr.check_in_out.components.CheckOutDialog
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.work.WorkManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.check_in_out.components.GpsDialog
import net.inspirehub.hr.check_in_out.components.InternetRequiredDialog
import net.inspirehub.hr.check_in_out.components.OfflineCheckOutDialog
import net.inspirehub.hr.check_in_out.components.OfflineSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.check_in_out.components.CheckInOutErrorDialog
import net.inspirehub.hr.check_in_out.components.NotAllowedLocationDialog
import net.inspirehub.hr.check_in_out.data.AppDatabase
import net.inspirehub.hr.check_in_out.data.OfflineLog
import net.inspirehub.hr.check_in_out.data.scheduleCheckOutReminder
import java.net.InetSocketAddress
import java.net.Socket
import java.util.Date
import java.util.TimeZone


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
@SuppressLint("MissingPermission", "SuspiciousIndentation")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckInOutScreen(
    navController: NavController,
    viewModel: CheckInOutViewModel = viewModel()

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
    var isDialogLoading by remember { mutableStateOf(false) }
    val lastCheckIn by viewModel.lastCheckIn.collectAsState()
    val lastCheckOut by viewModel.lastCheckOut.collectAsState()
    val rawDate = lastCheckOut?.substringBefore(" ") ?: ""
    val parts = rawDate.split("-") // [2025, 08, 18]
    var showOfflineCheckOutDialog by remember { mutableStateOf(false) }
//    val showDialog by viewModel.showTimeChangedDialog.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var offlineMessage by remember { mutableStateOf("") }
    var showInternetRequiredDialog by remember { mutableStateOf(false) }
    var isOffline by remember { mutableStateOf(false) }
    var isButtonLoading by remember { mutableStateOf(true) }
    val workedHours by viewModel.workedHours.collectAsState()
    val totalMinutes = ((workedHours ?: 0.0) * 60).toInt()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
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

    fun String.replaceDigitsWithArabic(): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return this.map { char ->
            if (char.isDigit()) arabicDigits[char.digitToInt()] else char
        }.joinToString("")
    }


    @Composable
    fun Modifier.noClickable(): Modifier = this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null
    ) {
        // no click effect
    }


    println("jessicayoussef $attendanceStatus")
    println("jessicayoussef $lastCheckIn")
    println("jessicayoussef $lastCheckOut")

    fun formatUtcToLocal(dateTimeString: String): String {
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val utcDateTime = LocalDateTime.parse(dateTimeString, formatter)
            val utcZoned = ZonedDateTime.of(utcDateTime, ZoneOffset.UTC)
            val localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault())
            val localTime = localZoned.toLocalTime()

            val currentLocale = Locale.getDefault()
            val formattedTime =
                localTime.format(DateTimeFormatter.ofPattern("h:mm a", currentLocale))

            if (currentLocale.language == "ar") {
                val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
                formattedTime.map { c ->
                    if (c.isDigit()) arabicDigits[c.digitToInt()] else c
                }.joinToString("")
            } else {
                formattedTime
            }
        } catch (e: Exception) {
            "--:--"
        }
    }

    val colors = appColors()

    val checkInTime = lastCheckIn?.let { formatUtcToLocal(it) } ?: "--:--"
    val checkOutTime = lastCheckOut?.let { formatUtcToLocal(it) } ?: "--:--"

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
                        val currentLocale =
                            if (Locale.getDefault().language == "ar") Locale("ar") else Locale.ENGLISH
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
                val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                isGpsEnabled = gpsStatus
                Log.d("GPS_STATUS", "🔁 GPS status after resume: $gpsStatus")

                if (!gpsStatus) {
                    println("❌ GPS is still OFF")
                    showGpsDialog = true
                } else {
                    println("✅ GPS is ON now")
                    showGpsDialog = false

                    // ✅ Re-verify distance and location after GPS is turned on
                    Log.d("GPS_STATUS", "🔄 Re-checking location and distance...")
//                    viewModel.checkLocationAndDistance(latitude, longitude, allowedDistance)

                    val companies = prefManager.getCompaniesLatLng()
                    val allowedIds = prefManager.getAllowedLocationsIds()

                    if (!isOffline) {
                        viewModel.syncOfflineData(token)
                    }

                    viewModel.checkLocationAndDistanceAllCompanies(
                        companies = companies,
                        allowedLocationIds = allowedIds
                    )

                    // ✅ Loading will be temporarily enabled after returning
                    isInitialLoading = true
                    coroutineScope.launch {
                        delay(1000)
                        isInitialLoading = false
                    }
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


    LaunchedEffect(locationPermissionState.status.isGranted) {
        Log.d(
            "disable",
            "LaunchedEffect triggered | Permission granted: ${locationPermissionState.status.isGranted}"
        )
        if (locationPermissionState.status.isGranted) {
            Log.d("disable", "Calling checkLocationAndDistance()...")
//            viewModel.checkLocationAndDistance(latitude, longitude, allowedDistance)

            val companies = prefManager.getCompaniesLatLng()
            val allowedIds = prefManager.getAllowedLocationsIds()
            viewModel.checkLocationAndDistanceAllCompanies(
                companies = companies,
                allowedLocationIds = allowedIds
            )
            delay(60_000)

        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        val companies = prefManager.getCompaniesLatLng()
        val allowedIds = prefManager.getAllowedLocationsIds()

        viewModel.startLocationChecking(
            companies = companies,
            allowedLocationIds = allowedIds
        )
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            bottomBar = {
                BottomBar(navController = navController)
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
                        "checked_in" -> stringResource(R.string.you_are_checked_in)
                        "checked_out" -> {
                            stringResource(R.string.you_are_checked_out)
                        }

                        else -> "..."
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
                        text = stringResource(R.string.you_are_currently_offline_your_action_will_be_saved_and_sent_once_the_internet_is_available),
                        color = colors.error,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                    )
                } else {
                    Text(
                        if (attendanceStatus == "checked_in") {
                            println("jessica youssef $checkInTime")
                            stringResource(
                                R.string.checked_in_message,
                                checkInTime
                            )
                        } else if (attendanceStatus == "checked_out") {
                            stringResource(
                                R.string.checked_out_message,
                                checkOutLabel,
                                checkOutTime
                            )
                        } else "Loading",
                        color = colors.onBackgroundColor,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Log.d("disable", "isWithinDistance from state: $isWithinDistance")

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


                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {

                    CheckInOutButton(
                        attendanceStatus = attendanceStatus,
                        isWithinDistance = (isWithinDistance == true),
                        isLoading = isButtonLoading || isWithinDistance == null,
                        onClick = {
                            if (!isButtonLoading) {
                                coroutineScope.launch {

                                    val now = Date()

                                    if (isOffline) {
                                        val lastActionTime = prefManager.getLastOfflineActionTime() ?: Date(0) // أو احصلي على آخر عملية offline
                                        val diffMinutes = ((now.time - lastActionTime.time) / 60000).toInt() // فرق بالدقائق

                                        if (diffMinutes < 1) {
                                            // لو الفرق أقل من دقيقة → امنع الضغط
                                            offlineMessage = if (Locale.getDefault().language == "ar") {
                                                "عليك الانتظار دقيقة واحدة قبل إعادة العملية!"
                                            } else {
                                                "Please wait 1 minute before performing the action again!"
                                            }
                                            return@launch
                                        }
                                    }

                                    // ️Perform an immediate re-check of the site.
                                    val companies = prefManager.getCompaniesLatLng()
                                    val allowedIds = prefManager.getAllowedLocationsIds()

                                    viewModel.checkLocationAndDistanceAllCompanies(
                                        companies = companies,
                                        allowedLocationIds = allowedIds
                                    )

                                    if(isWithinDistance != true) {
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

                                    val sharedPrefManager = SharedPrefManager(context)
                                    val token = sharedPrefManager.getToken()
                                    val offline = viewModel.isOffline()
                                    val wasOfflineDuringChange =
                                        sharedPrefManager.wasOfflineDuringTimeChange()
                                    val diffMinutes = sharedPrefManager.getTimeDifference()
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
                                    Log.d("CheckInOutDebug", "Current attendanceStatus: $attendanceStatus")
                                    Log.d("CheckInOutDebug", "Is Offline: $isOffline")

                                    Log.d("CheckInOutDebug", "Next action determined: $nextAction")



                                    // 🔹 Case 1: User changed the time while offline → Forbidden to execute
                                    if (isOffline && wasOfflineDuringChange) {
                                        showInternetRequiredDialog = true
                                        isButtonLoading = false
                                        return@launch
                                    }

                                    // 🔹 إذا كانت العملية check_out، أظهر الـ OfflineCheckOutDialog
                              if (isOffline) {

                                        val db = AppDatabase.getDatabase(context)
                                        val log = OfflineLog(
                                            action = nextAction,
                                            lat = currentLat ?: 0.0,
                                            lng = currentLng ?: 0.0,
                                            action_time = Date().toString(),
                                            action_tz = TimeZone.getDefault().id
                                        )

                                  if (nextAction == "check_in") {
                                        coroutineScope.launch {
                                            // 🔹 تسجيل الـ offline log في الـ database
                                            withContext(Dispatchers.IO) {
                                                db.offlineLogDao().insertLog(log)

                                                // ✅ طباعة للتأكد
                                                val allLogs = db.offlineLogDao().getAllLogs()
                                                allLogs.forEach { println("💾 Offline Log: $it") }
                                            }}

                                        Log.d("CheckInOutDebug", "Updating attendanceStatus in ViewModel")


                                        // 🔹 تحديث حالة الحضور مباشرة في UI
                                        viewModel.setAttendanceStatus(nextStatus)
                                       prefManager.saveLastOfflineActionTime(Date())

                                       isButtonLoading = false
                                        Log.d("CheckInOutDebug", "AttendanceStatus after setAttendanceStatus(): $attendanceStatus")

                                        // 🔹 رسالة للمستخدم
                                        val currentLanguage = Locale.getDefault().language
                                        offlineMessage = if (currentLanguage == "ar") {
                                            "انت غير متصل بالانترنت! تم حفظ العملية، سيتم إرسالها عند توفر الإنترنت"
                                        } else {
                                            "You are offline! The operation has been saved and will be sent when the internet is available."
                                        }} else {


                                  if (nextAction == "check_out") {
                                      showErrorDialog = true
                                      isButtonLoading = false
                                      isErrorDialogLoading = false
//                                      return@launch
                                  }}

                                  // 🔹 محاولة إعادة إرسال الـ offline logs لو الإنترنت متاح بعدين
                                        if (!isOffline) {
                                            val token = prefManager.getToken()
                                            if (token != null) {
                                                viewModel.syncOfflineData(token)
                                            } else {
                                                Log.d("CheckInOut", "⚠️ Token is null, cannot sync offline data")
                                            }                                            }
                                        }





                                    // 🔹 Case 3: User is online → Calculate the time difference with the server
                                    viewModel.getTimeDifferenceWithServer(token!!) { diff ->
                                        Log.d(
                                            "CheckInOut",
                                            "🕒 Time difference with server (minutes): $diff"
                                        )

                                        sharedPrefManager.saveTimeDifference(diff)
                                        sharedPrefManager.setWasOfflineDuringTimeChange(false)
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
                                                Log.d("ReminderDebug", "✅ Reminder scheduled after ONLINE check-in")

                                                if (newStatus != null) {
                                                    // ✅ Success
                                                    if (!isAllowedLocation) {
                                                        showNotAllowedDialog = true
                                                    }
                                                }
                                                else {
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
            CheckInOutErrorDialog(
                message = stringResource(R.string.a_fake_location_was_detected_please_turn_off_fake_gps_to_continue),
                onDismiss = { showFakeLocationDialog = false }
            )
        }

        if (showOfflineCheckOutDialog) {
            OfflineCheckOutDialog(
                onDismiss = { showOfflineCheckOutDialog = false },
                onConfirm = {
                    showOfflineCheckOutDialog = false
                    coroutineScope.launch {
                        val db = AppDatabase.getDatabase(context)
                        val log = OfflineLog(
                            action = "check_out",
                            lat = currentLat,
                            lng = currentLng,
                            action_time = Date().toString(),
                            action_tz = TimeZone.getDefault().id
                        )
                        withContext(Dispatchers.IO) {
                            db.offlineLogDao().insertLog(log)
                        }
                        viewModel.setAttendanceStatus("checked_out")
                        prefManager.saveLastOfflineActionTime(Date())
                        isButtonLoading = false
                        isErrorDialogLoading = false
                    }
                }
            )
        }



        NotAllowedLocationDialog(
            showDialog = showNotAllowedDialog,
            onDismiss = {
                showNotAllowedDialog = false
                val companies = prefManager.getCompaniesLatLng()
                val allowedIds = prefManager.getAllowedLocationsIds()

                viewModel.checkLocationAndDistanceAllCompanies(
                    companies = companies,
                    allowedLocationIds = allowedIds
                )}
        )

        if (isInitialLoading && !isOffline) {
            FullLoading()
        }


        if (showInternetRequiredDialog) {
            InternetRequiredDialog(
                onDismiss = { showInternetRequiredDialog = false }
            )
        }

        if (offlineMessage.isNotEmpty()) {
            OfflineSnackBar(
                message = offlineMessage,
                onDismiss = { offlineMessage = "" }
            )
        }
    }


    GpsDialog(
        showDialog = showGpsDialog,
        onDismiss = { showGpsDialog = false },
        onConfirm = {
            showGpsDialog = false
            val intent =
                android.content.Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
        }
    )



    if (showErrorMessageDialog) {
        CheckInOutErrorDialog(
            message = errorMessage,
            onDismiss = { showErrorMessageDialog = false }
        )
    }




    if (showErrorDialog) {
        CheckOutDialog(
            isOffline = isOffline,
            hours = hours,
            minutes = minutes,
            isLoading = isDialogLoading,
            onConfirm = {
                WorkManager.getInstance(context).cancelAllWorkByTag("check_out_reminder_work")
                isDialogLoading = true
                viewModel.sendAttendance(token, "check_out") { newStatus ->
                    isDialogLoading = false
                    if (newStatus != null) {
                        // ✅ Success
                        println("✅ Forced Check Out with status: $newStatus")
                        showErrorDialog = false

                        if (!isAllowedLocation ) {
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
                if(isOffline){
                    showErrorDialog = false
                    showErrorMessageDialog = false
                    viewModel.setAttendanceStatus("checked_out")
                    prefManager.saveLastOfflineActionTime(Date())
                    val currentLanguage = Locale.getDefault().language
                    offlineMessage = if (currentLanguage == "ar") {
                        "انت غير متصل بالانترنت! تم حفظ العملية، سيتم إرسالها عند توفر الإنترنت"
                    } else {
                        "You are offline! The operation has been saved and will be sent when the internet is available."
                    }
                }
            },
            onCancel = { showErrorDialog = false }
        )

    }
}