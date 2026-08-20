
package net.inspirehub.hr.check_in_out.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import net.inspirehub.hr.SharedPrefManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import net.inspirehub.hr.settings.data.LocalAttendanceReminderReceiver

class CheckInOutViewModel(application: Application) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val _workedHours = MutableStateFlow<Double?>(null)
    private val _currentLat = MutableStateFlow(0.0)
    private val _lastCheckIn = MutableStateFlow<String?>(null)
    private val _lastCheckOut = MutableStateFlow<String?>(null)
    private val _currentLng = MutableStateFlow(0.0)
    private val _message = MutableStateFlow("")
    private val _isWithinDistance = MutableStateFlow<Boolean?>(null)
    private val _attendanceStatus = MutableStateFlow("Loading...")
    private val cache = AttendanceCache(context)
    private val _showTimeChangedDialog = MutableStateFlow(false)
    val lastCheckOut: StateFlow<String?> = _lastCheckOut
    val lastCheckIn: StateFlow<String?> = _lastCheckIn
    val workedHours: StateFlow<Double?> = _workedHours
    val currentLat: StateFlow<Double> = _currentLat
    val currentLng: StateFlow<Double> = _currentLng
    val isWithinDistance: StateFlow<Boolean?> = _isWithinDistance
    val message: StateFlow<String> = _message
    val showTimeChangedDialog: StateFlow<Boolean> = _showTimeChangedDialog
    val attendanceStatus: StateFlow<String> = _attendanceStatus
    private val _availableCompanies = MutableStateFlow<List<String>>(emptyList())
    val availableCompanies: StateFlow<List<String>> = _availableCompanies

    private val _currentCompanyId = MutableStateFlow<Int?>(null)
    val currentCompanyId: StateFlow<Int?> = _currentCompanyId

    private val _isAllowedLocation = MutableStateFlow(true)
    val isAllowedLocation: StateFlow<Boolean> = _isAllowedLocation

    private val _isFakeLocation = MutableStateFlow(false)
    val isFakeLocation: StateFlow<Boolean> = _isFakeLocation

    private var hasPrintedDistanceLog = false
    private var locationCallback: LocationCallback? = null

    private val _locationAccuracy = MutableStateFlow<Float?>(null)
    val locationAccuracy: StateFlow<Float?> = _locationAccuracy

    @SuppressLint("MissingPermission")
    fun startLocationUpdates(
        companies: List<CompanyLocation>,
        allowedLocationIds: List<Int>
    ) {

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            3000L
        )
            .setMinUpdateDistanceMeters(3f)
            .build()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {
                Log.d("SERVICE_TEST_view_model", "Location callback fired")
                val location = result.lastLocation ?: return

                checkLocationAndDistanceAllCompanies(
                    location,
                    companies,
                    allowedLocationIds
                )
            }
        }
        Log.d("SERVICE_TEST", "Requesting location updates")
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback!!,
            null
        )
    }

    fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }

    fun startPollingAttendance(token: String) {
        viewModelScope.launch {
            val result = fetchAttendanceStatus(context, token)
            if (result != null) {
                _attendanceStatus.value = result.attendance_status ?: "checked_out"
                _lastCheckIn.value = result.checkInTime ?: result.lastCheckIn
                _lastCheckOut.value = result.lastCheckOut ?: result.lastCheckOut
                _workedHours.value = result.worked_hours
                cache.saveStatus(
                    _attendanceStatus.value,
                    _lastCheckIn.value,
                    _lastCheckOut.value
                )
            }
        }
    }

    private fun isLocationFake(location: Location): Boolean {

        val mockDetected =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                location.isMock
            } else {
                location.isFromMockProvider
            }

        // accuracy غير طبيعي (fake apps غالباً)
        val badAccuracy = location.accuracy > 50f

        return mockDetected || badAccuracy
    }

    private val dao =
        AppDatabase.getDatabase(application).offlineLogDao()

    private val _buttonText = MutableStateFlow("Check هتIn")
    val buttonText: StateFlow<String> = _buttonText

    fun loadLastOfflineStatus() {
        viewModelScope.launch {
            val lastLog = offlineDao.getLastLog()

            if (lastLog != null) {
                _attendanceStatus.value = when (lastLog.action) {
                    "check_in" -> "checked_in"
                    "check_out" -> "checked_out"
                    else -> "checked_out"
                }
            }
        }
    }


    init {
        viewModelScope.launch {
            val online =
                !NetworkUtils.isNetworkAvailable(context).not() && NetworkUtils.hasRealInternet()
            if (online) {
                // 🔹 Online → جلب من السيرفر
                val token = SharedPrefManager(context).getToken() ?: ""
                val result = fetchAttendanceStatus(
                    context = application.applicationContext,
                    token
                )
                if (result != null) {
                    _attendanceStatus.value = result.attendance_status ?: "checked_out"
                    _lastCheckIn.value = result.checkInTime ?: result.lastCheckIn
                    _lastCheckOut.value = result.lastCheckOut ?: result.lastCheckOut

                    // 🔹 احفظ النسخة الأخيرة في الكاش
                    cache.saveStatus(
                        _attendanceStatus.value,
                        _lastCheckIn.value,
                        _lastCheckOut.value
                    )
                } else {
                    // ❌ في حالة فشل السيرفر → fallback على الكاش
                    val (status, checkIn, checkOut) = cache.getStatus()
                    setAttendanceStatus(status)
                    _lastCheckIn.value = checkIn
                    _lastCheckOut.value = checkOut
                }
            } else {
                // 🔹 Offline → جلب من الكاش
                val (status, checkIn, checkOut) = cache.getStatus()
                setAttendanceStatus(status)
                _lastCheckIn.value = checkIn
                _lastCheckOut.value = checkOut
            }
        }
    }

    fun syncOfflineData(token: String) {
        viewModelScope.launch {
            if (NetworkUtils.isNetworkAvailable(context) && NetworkUtils.hasRealInternet()) {
                enqueueOfflineWorker(token)
            }
        }
    }

    fun enqueueOfflineWorker(token: String) {
        val data = workDataOf("token" to token)

        val request = OneTimeWorkRequestBuilder<OfflineAttendanceWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag("offline_attendance_tag")
            .build()

        WorkManager.getInstance(context).enqueue(request)
        Log.d("OfflineWorker", "⏳ OfflineWorker enqueued")
    }

    fun setAttendanceStatus(status: String) {
        _attendanceStatus.value = status

        SharedPrefManager(getApplication()).saveAttendanceStatus(status)

        Log.d(
            "TEST ATTENDANCE_STATUS",
            "Attendance status saved = $status"
        )
    }


    private val database = AppDatabase.getDatabase(context)
    private val offlineDao = database.offlineLogDao()

    private suspend fun saveOfflineLog(
        action: String,
        lat: Double,
        lng: Double,
        actionTime: String
    ) {
        val log = OfflineLog(
            action = action,
            lat = lat,
            lng = lng,
            action_time = actionTime,
            action_tz = "UTC"
        )
        offlineDao.insertLog(log)
        Log.d("OfflineLog", "💾 Saved offline log: $log")
    }


    fun dismissTimeChangedDialog() {
        _showTimeChangedDialog.value = false
    }


    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    fun checkLocationAndDistanceAllCompanies(
        location: Location,
        companies: List<CompanyLocation>,
        allowedLocationIds: List<Int>
    ) {

        if (isLocationFake(location)) {

            Log.e("Security", "🚨 Fake location detected!")

            _isFakeLocation.value = true
            _isWithinDistance.value = false
            _isAllowedLocation.value = false
            _currentCompanyId.value = null

            return
        }
        _isFakeLocation.value = false


        _currentLat.value = location.latitude
        _currentLng.value = location.longitude
        _locationAccuracy.value = location.accuracy
//            Log.d("Location", "📍 Current location: ${location.latitude}, ${location.longitude}")

        var matchedCompany: CompanyLocation? = null

        companies.forEach { company ->
            val results = FloatArray(1)
            Location.distanceBetween(
                location.latitude,
                location.longitude,
                company.lat,
                company.lng,
                results
            )
            val distance = results[0]

            if (!hasPrintedDistanceLog) {
                Log.d(
                    "DistanceCheck",
                    "Company: ${company.name} | Lat: ${company.lat}, Lng: ${company.lng} | " +
                            "Distance: $distance meters | AllowedDistance: ${company.allowedDistance} meters"
                )
            }
            val limit = company.allowedDistance + location.accuracy

            if (distance <= limit) {
//1               if (distance <=  company.allowedDistance) {
                matchedCompany = company
                if (!hasPrintedDistanceLog) {
                    Log.d("DistanceCheck", "${company.name} is within allowed distance ✅")
                }
            }
        }

        if (!hasPrintedDistanceLog) {
            hasPrintedDistanceLog = true
        }

        if (matchedCompany != null) {
            _isWithinDistance.value = true
            _currentCompanyId.value = matchedCompany!!.id

            // 🔥 تحقق من صلاحية الموقع
            _isAllowedLocation.value = allowedLocationIds.contains(matchedCompany!!.id)
            if (!hasPrintedDistanceLog) {
                Log.d(
                    "DistanceCheck",
                    "Employee is within allowed distance for company: ${matchedCompany!!.name} | " +
                            "ID: ${matchedCompany!!.id} | Allowed: ${_isAllowedLocation.value}"
                )
            }

        } else {
            _isWithinDistance.value = false
            _currentCompanyId.value = null
            _isAllowedLocation.value = true
            if (!hasPrintedDistanceLog) {
                Log.d("DistanceCheck", "User is not within any allowed company distance")
            }
        }
    }


    suspend fun isOffline(): Boolean {
        val noNetwork = !NetworkUtils.isNetworkAvailable(context)
        val noRealInternet = !NetworkUtils.hasRealInternet()
        return noNetwork || noRealInternet
    }

    fun getTimeDifferenceWithServer(token: String, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("TimeCheck", "🚀 Start calculating the time difference with the server...")
                val serverTimeString = fetchServerTime(
                    context = context, token = token
                )
                Log.d("TimeCheck", "🕒 Time coming from server (raw): $serverTimeString")
                if (serverTimeString != null) {

                    // ✅ Modify the date format according to the time coming from the server                    val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    val serverFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                    serverFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val serverDate = serverFormat.parse(serverTimeString)
                    val deviceDate = Date()

                    Log.d("TimeCheck", "📅 Server time (after conversion): $serverDate")
                    Log.d("TimeCheck", "📱 Current device time: $deviceDate")

                    if (serverDate != null) {
                        val diffMillis = serverDate.time - deviceDate.time
                        val diffMinutes = diffMillis / (1000 * 60)
                        Log.d(
                            "TimeCheck",
                            "✅ Time difference between device and server: $diffMinutes"
                        )
                        onResult(diffMinutes)
                    } else {
                        Log.e("TimeCheck", "❌ Failed to convert server time to Date")
                        onResult(-1)
                    }
                } else {
                    Log.e("TimeCheck", "❌ fetchServerTime(token) returned null")
                    onResult(-1)
                }
            } catch (e: Exception) {
                Log.e(
                    "TimeCheck",
                    "❌ An exception occurred while calculating the time difference: ${e.message}",
                    e
                )
                onResult(-1)
            }
        }
    }


    fun sendAttendance(token: String, action: String, onComplete: (String?) -> Unit = {}) {
        val sharedPrefManager = SharedPrefManager(context)

        if (_isFakeLocation.value) {
            _message.value = "Fake location detected"
            onComplete(null)
            return
        }
        val cache = AttendanceCache(context)

        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        viewModelScope.launch {
            val serverTime = fetchServerTime(token, context)

            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")

            val finalActionTime = serverTime?.trim()
                ?.replace("T", " ")
                ?.replace("Z", "")
                ?.substringBefore("+")
                ?: utcFormat.format(Date())
            Log.d("CheckInOut", "📤 Sending attendance with time: $finalActionTime")

            Log.d("Attendance", "📅 Final Action Time to send: $finalActionTime")

            val isOnline =
                NetworkUtils.isNetworkAvailable(context) && NetworkUtils.hasRealInternet()


            if (isOnline) {

                // 🔸 Online → Send directly
                val result = sendAttendanceAction(
                    context,
                    token,
                    action,
                    _currentLat.value.toString(),
                    _currentLng.value.toString(),
                    finalActionTime
                )
                println("🔹 Check-in response: $result")

                if (result != null) {
                    _message.value = result.message ?: "Something went wrong. Please try again."

                    if (result.status.equals("error", ignoreCase = true)) {
//                        _message.value = result.message
                        onComplete(null)
                        return@launch
                    }


                    result.attendance_status?.let {
                        _attendanceStatus.value = it

                        SharedPrefManager(context)
                            .saveAttendanceStatus(it)
                    }

                    result.checkInTime?.let {
                        _lastCheckIn.value = it
                    } ?: result.lastCheckIn?.let {
                        _lastCheckIn.value = it
                    }

                    result.checkOutTime?.let {
                        _lastCheckOut.value = it
                    } ?: result.lastCheckOut?.let {
                        _lastCheckOut.value = it
                    }

                    _workedHours.value = result.worked_hours

                    println("Before assign _lastCheckIn: ${_lastCheckIn.value}")
                    println("Server last_check_in: ${result.lastCheckIn} and ${result.checkInTime}")

                    _lastCheckIn.value = result.checkInTime ?: result.lastCheckIn

                    println("After assign _lastCheckIn: ${_lastCheckIn.value}")

                    cache.saveStatus(
                        _attendanceStatus.value,
                        _lastCheckIn.value,
                        _lastCheckOut.value
                    )

                    if (action == "check_out") {
                        cancelCheckOutReminder(context)
                        sharedPrefManager.saveLastAttendanceReminderType(null)
                        LocalAttendanceReminderReceiver.cancelReminderNotification(context)

                        LocationTrackingManager.updateTracking(
                            context = context,
                            attendanceStatus = "checked_out"
                        )
                    }

                    if (action == "check_in") {
                        sharedPrefManager.saveLastAttendanceReminderType(null)
                        LocalAttendanceReminderReceiver.cancelReminderNotification(context)

                        result.todayScheduledHours?.let { hours ->
                            scheduleCheckOutReminder(context, hours)
                        }

                        LocationTrackingManager.updateTracking(
                            context = context,
                            attendanceStatus = "checked_in"
                        )
                    }

                    onComplete(result.attendance_status)
                } else {
                    // ❌ Online but server error → cancel everything
                    _message.value = "Something went wrong. Please try again."
                    onComplete(null)

//                    enqueueWorkManager(token, action, finalActionTime)
//                    onComplete("queued")
                }
            } else {
                // 🔸 Offline (but the time is right)
                saveOfflineLog(action, _currentLat.value, _currentLng.value, finalActionTime)
                enqueueWorkManager(token, action, finalActionTime)
                onComplete("queued")
            }
        }
    }

    // ✨ I separated the WorkManager part into a special function so that the code would be cleaner.
    private fun enqueueWorkManager(token: String, action: String, actionTime: String) {
        val sharedPref = SharedPrefManager(context)
        val diffMinutes = sharedPref.getTimeDifference()

        val data = workDataOf(
            "token" to token,
            "action" to action,
            "lat" to _currentLat.value.toString(),
            "lng" to _currentLng.value.toString(),
            "action_time" to actionTime,
            "diff_minutes" to diffMinutes.toString()
        )

        val request = OneTimeWorkRequestBuilder<AttendanceWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag("attendance_tag")
            .build()

        WorkManager.getInstance(context).enqueue(request)

        Log.d("Attendance", "⏳ WorkManager job enqueued with data: $data")
        _message.value = "⏳ Attendance queued. Will send when network is back."
    }

    fun getAttendanceStatus(token: String) {
        viewModelScope.launch {
            val result = fetchAttendanceStatus(
                context, token
            )
            if (result != null) {
                _attendanceStatus.value = result.attendance_status ?: "checked_out"
                _lastCheckIn.value = result.checkInTime ?: result.lastCheckIn
                _lastCheckOut.value = result.lastCheckOut ?: result.lastCheckOut
                _workedHours.value = result.worked_hours
//                calculateWorkedHours()
            }
        }
    }
}