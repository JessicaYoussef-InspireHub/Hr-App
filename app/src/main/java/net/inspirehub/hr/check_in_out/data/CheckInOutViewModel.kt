package net.inspirehub.hr.check_in_out.data

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


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
            val online = !NetworkUtils.isNetworkAvailable(context).not() && NetworkUtils.hasRealInternet()
            if (online) {
                // 🔹 Online → جلب من السيرفر
                val token = SharedPrefManager(context).getToken() ?: ""
                val result = fetchAttendanceStatus(
                    context = application.applicationContext ,
                    token)
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
                    _attendanceStatus.value = status
                    _lastCheckIn.value = checkIn
                    _lastCheckOut.value = checkOut
                }
            } else {
                // 🔹 Offline → جلب من الكاش
                val (status, checkIn, checkOut) = cache.getStatus()
                _attendanceStatus.value = status
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

    fun setAttendanceStatus(newStatus: String) {
        _attendanceStatus.value = newStatus
    }





    private val database = AppDatabase.getDatabase(context)
    private val offlineDao = database.offlineLogDao()

    private suspend fun saveOfflineLog(action: String, lat: Double, lng: Double, actionTime: String) {
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
    fun startLocationChecking(
        companies: List<CompanyLocation>,
        allowedLocationIds: List<Int>
    ) {
        viewModelScope.launch {
            while (true) {
                checkLocationAndDistanceAllCompanies(
                    companies = companies,
                    allowedLocationIds = allowedLocationIds
                )
                delay(5_000)
            }
        }
    }




    @RequiresPermission(
        allOf = [
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ]
    )
    fun checkLocationAndDistanceAllCompanies(
        companies: List<CompanyLocation>,
        allowedLocationIds: List<Int>
    ) {
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location == null) {
                Log.e("Location", "❌ Location is null")
                _isWithinDistance.value = false
                _isAllowedLocation.value = true
                _currentCompanyId.value = null
                return@addOnSuccessListener
            }

            _currentLat.value = location.latitude
            _currentLng.value = location.longitude
            Log.d("Location", "📍 Current location: ${location.latitude}, ${location.longitude}")

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

//                if (_isAllowedLocation.value) {
//                    Log.d("DistanceCheck", "✅ Company ID ${matchedCompany!!.id} is allowed")
//                } else {
//                    Log.d("DistanceCheck", "❌ Company ID ${matchedCompany!!.id} is NOT allowed")
//                }

                Log.d(
                    "DistanceCheck",
                    "Company: ${company.name} | Lat: ${company.lat}, Lng: ${company.lng} | " +
                            "Distance: $distance meters | AllowedDistance: ${company.allowedDistance} meters"
                )

                if (distance <=  company.allowedDistance) {
                    matchedCompany = company
                    Log.d("DistanceCheck", "${company.name} is within allowed distance ✅")
                }
            }

            if (matchedCompany != null) {
                _isWithinDistance.value = true
                _currentCompanyId.value = matchedCompany!!.id

                // 🔥 تحقق من صلاحية الموقع
                _isAllowedLocation.value = allowedLocationIds.contains(matchedCompany!!.id)

                Log.d(
                    "DistanceCheck",
                    "Employee is within allowed distance for company: ${matchedCompany!!.name} | " +
                            "ID: ${matchedCompany!!.id} | Allowed: ${_isAllowedLocation.value}"
                )

            } else {
                _isWithinDistance.value = false
                _currentCompanyId.value = null
                _isAllowedLocation.value = true
                Log.d("DistanceCheck", "User is not within any allowed company distance")
            }
        }
    }



//    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
//    fun checkLocationAndDistanceAllCompanies(
//        companies: Map<String, Pair<Double, Double>>,
//        allowedDistance: Double) {
//        fusedLocationClient.getCurrentLocation(
//            Priority.PRIORITY_HIGH_ACCURACY,
//            null
//        ).addOnSuccessListener { location: Location? ->
//            if (location != null) {
//                _currentLat.value = location.latitude
//                _currentLng.value = location.longitude
//
//                Log.d("Location", "📍 Current location: ${location.latitude}, ${location.longitude}")
//
//                var withinAnyCompany = false
//                var currentCompany: String? = null
//                var currentCompanyLat: Double? = null
//                var currentCompanyLng: Double? = null
//
//                companies.forEach { (companyName, latLng) ->
//                    val (companyLat, companyLng) = latLng
//                    val results = FloatArray(1)
//                    Location.distanceBetween(
//                        location.latitude, location.longitude,
//                        companyLat, companyLng,
//                        results
//                    )
//                    val distance = results[0]
//                    Log.d(
//                        "DistanceCheck",
//                        "Company: $companyName | Lat: $companyLat, Lng: $companyLng | Distance: $distance meters | AllowedDistance: $allowedDistance meters"
//                    )
//
//                    if (distance <= allowedDistance) {
//                        withinAnyCompany = true
//                        currentCompany = companyName
//                        currentCompanyLat = companyLat
//                        currentCompanyLng = companyLng
//                        Log.d("DistanceCheck", "$companyName is within allowed distance ✅")
//                    }
//                }
//
//                _isWithinDistance.value = withinAnyCompany
//
//                if (withinAnyCompany && currentCompany != null) {
//                    Log.d(
//                        "DistanceCheck",
//                        "Employee is within allowed distance for company: $currentCompany | Lat: $currentCompanyLat, Lng: $currentCompanyLng"
//                    )
//                }
//
//                Log.d("DistanceCheck", "User is within any allowed company distance: $withinAnyCompany")
//
//            } else {
//                Log.e("Location", "❌ Location is null")
//                _isWithinDistance.value = false
//            }
//        }
//    }




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
                        Log.d("TimeCheck", "✅ Time difference between device and server: $diffMinutes")
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
                Log.e("TimeCheck", "❌ An exception occurred while calculating the time difference: ${e.message}", e)
                onResult(-1)
            }
        }
    }


    @SuppressLint("MissingPermission")
//    fun checkLocationAndDistance(targetLat: Double, targetLng: Double, allowedDistance: Double) {
//        Log.d("disable", "checkLocationAndDistance called with targetLat=$targetLat, targetLng=$targetLng, allowedDistance=$allowedDistance")
//
//        fusedLocationClient.getCurrentLocation(
//            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
//            null
//        ).addOnSuccessListener { location: Location? ->
//            if (location != null) {
//                // ✅ Accurate location from getCurrentLocation
//                Log.d(
//                    "Location",
//                    "✅ Accurate Lat: ${location.latitude}, Lng: ${location.longitude}"
//                )
//                Log.d("disable", "✅ Current Location: ${location.latitude}, ${location.longitude}")
//                Log.d(
//                    "disable",
//                    "✅ Target Location: $targetLat, $targetLng | Allowed Distance: $allowedDistance"
//                )
//
//                _currentLat.value = location.latitude
//                _currentLng.value = location.longitude
//
//
//                // 👇الوطنية getCurrentLocation
////                _currentLat.value = 27.192085
////                _currentLng.value = 31.186931
//
////                // اول الشارع
////                _currentLat.value = 27.190936
////                _currentLng.value = 31.187951
//
//
//                val results = FloatArray(1)
//                Location.distanceBetween(
//                    _currentLat.value, _currentLng.value, // my current location
//                    targetLat, targetLng, // my company location (Step)
//                    results
//                )
//                val distance = results[0]
//                Log.d("Distance", "🚩 Distance to company: $distance meters")
//                Log.d(
//                    "disable",
//                    "allowedDistance: $allowedDistance | isWithinDistance: ${distance <= allowedDistance}"
//                )
//                Log.d("disable", "🚩 Calculated Distance: $distance meters")
//
//                _isWithinDistance.value = distance <= allowedDistance
//                Log.d("disable", "✅ isWithinDistance updated to: ${_isWithinDistance.value}")
//
//            } else {
//                Log.e("Location", "❌ Location is null")
//                Log.e("disable", "❌ Location is null")
//
//            }
//        }
//    }

    fun sendAttendance(token: String, action: String, onComplete: (String?) -> Unit = {}) {
        val cache = AttendanceCache(context)

        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        viewModelScope.launch {
            val serverTime = fetchServerTime( token , context)

            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")

            val finalActionTime = serverTime?.trim()
                ?.replace("T", " ")
                ?.replace("Z", "")
                ?.substringBefore("+")
                ?: utcFormat.format(Date())
            Log.d("CheckInOut", "📤 Sending attendance with time: $finalActionTime")

            Log.d("Attendance", "📅 Final Action Time to send: $finalActionTime")

            val isOnline = NetworkUtils.isNetworkAvailable(context) && NetworkUtils.hasRealInternet()


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

//                    _message.value = result.message

                    result.attendance_status?.let {
                        _attendanceStatus.value = it
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

//                    _attendanceStatus.value = result.attendance_status ?: _attendanceStatus.value
//                    _lastCheckIn.value = result.checkInTime ?: result.lastCheckIn
//                    _lastCheckOut.value = result.lastCheckOut ?: result.lastCheckOut
//
//                    _workedHours.value = result.worked_hours


                    println("Before assign _lastCheckIn: ${_lastCheckIn.value}")
                    println("Server last_check_in: ${result.lastCheckIn } and ${result.checkInTime}")

                    _lastCheckIn.value = result.checkInTime ?: result.lastCheckIn

                    println("After assign _lastCheckIn: ${_lastCheckIn.value}")

                    cache.saveStatus(
                        _attendanceStatus.value,
                        _lastCheckIn.value,
                        _lastCheckOut.value
                    )

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
    private fun enqueueWorkManager(token: String, action: String , actionTime: String) {
        val sharedPrefManager = SharedPrefManager(context)
        val diffMinutes = sharedPrefManager.getTimeDifference()

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
                context,token)
            if (result != null) {
                _attendanceStatus.value = result.attendance_status ?: "checked_out"
                _lastCheckIn.value = result.checkInTime ?: result.lastCheckIn
                _lastCheckOut.value = result.lastCheckOut ?: result.lastCheckOut
                _workedHours.value = result.worked_hours
                calculateWorkedHours()
            }
        }
    }

    fun calculateWorkedHours() {
        val checkIn = _lastCheckIn.value
        val checkOut = _lastCheckOut.value
        if (checkIn != null && checkOut != null) {
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                val checkInDate = formatter.parse(checkIn)
                val checkOutDate = formatter.parse(checkOut)
                if (checkInDate != null && checkOutDate != null) {
                    val diffMillis = checkOutDate.time - checkInDate.time
                    val diffHours = diffMillis / (1000.0 * 60.0 * 60.0)
                    _workedHours.value = diffHours
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}