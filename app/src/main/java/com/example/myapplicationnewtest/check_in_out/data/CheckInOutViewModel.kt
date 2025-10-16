package com.example.myapplicationnewtest.check_in_out.data

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.location.LocationServices
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
    val workedHours: StateFlow<Double?> = _workedHours

    private val _lastCheckIn = MutableStateFlow<String?>(null)
    val lastCheckIn: StateFlow<String?> = _lastCheckIn

    private val _lastCheckOut = MutableStateFlow<String?>(null)
    val lastCheckOut: StateFlow<String?> = _lastCheckOut

    private val _currentLat = MutableStateFlow(0.0)
    val currentLat: StateFlow<Double> = _currentLat

    private val _currentLng = MutableStateFlow(0.0)
    val currentLng: StateFlow<Double> = _currentLng

    private val _isWithinDistance = MutableStateFlow<Boolean?>(null)
    val isWithinDistance: StateFlow<Boolean?> = _isWithinDistance

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    private val _attendanceStatus = MutableStateFlow("Loading...")

    private val cache = AttendanceCache(context)

    private val _showTimeChangedDialog = MutableStateFlow(false)
    val showTimeChangedDialog: StateFlow<Boolean> = _showTimeChangedDialog


    init {
        //// 🔹 Load local values immediately upon opening the app

        val (status, checkIn, checkOut) = cache.getStatus()
        _attendanceStatus.value = status
        _lastCheckIn.value = checkIn
        _lastCheckOut.value = checkOut
    }

    fun dismissTimeChangedDialog() {
        _showTimeChangedDialog.value = false
    }

    suspend fun isOffline(): Boolean {
        val noNetwork = !NetworkUtils.isNetworkAvailable(context)
        val noRealInternet = !NetworkUtils.hasRealInternet()
        return noNetwork || noRealInternet
    }






    @SuppressLint("MissingPermission")
    fun checkLocationAndDistance(targetLat: Double, targetLng: Double, allowedDistance: Double) {
        Log.d(
            "disable",
            "checkLocationAndDistance called with targetLat=$targetLat, targetLng=$targetLng, allowedDistance=$allowedDistance"
        )

        fusedLocationClient.getCurrentLocation(
            com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location: Location? ->
            if (location != null) {
                // ✅ Accurate location from getCurrentLocation
                Log.d(
                    "Location",
                    "✅ Accurate Lat: ${location.latitude}, Lng: ${location.longitude}"
                )
                Log.d("disable", "✅ Current Location: ${location.latitude}, ${location.longitude}")
                Log.d(
                    "disable",
                    "✅ Target Location: $targetLat, $targetLng | Allowed Distance: $allowedDistance"
                )


//                _currentLat.value = location.latitude
//                _currentLng.value = location.longitude


                // 👇الوطنية getCurrentLocation
//                _currentLat.value = 27.192085
//                _currentLng.value = 31.186931

//                // اول الشارع
                _currentLat.value = 27.190936
                _currentLng.value = 31.187951


                val results = FloatArray(1)
                Location.distanceBetween(
                    _currentLat.value, _currentLng.value, // my current location

                    targetLat, targetLng, // my company location (Step)
                    results
                )
                val distance = results[0]
                Log.d("Distance", "🚩 Distance to company: $distance meters")
                Log.d(
                    "disable",
                    "allowedDistance: $allowedDistance | isWithinDistance: ${distance <= allowedDistance}"
                )
                Log.d("disable", "🚩 Calculated Distance: $distance meters")

                _isWithinDistance.value = distance <= allowedDistance
                Log.d("disable", "✅ isWithinDistance updated to: ${_isWithinDistance.value}")

            } else {
                Log.e("Location", "❌ Location is null")
                Log.e("disable", "❌ Location is null")

            }
        }
    }

    fun sendAttendance(token: String, action: String, onComplete: (String?) -> Unit = {}) {
        val cache = AttendanceCache(context)

        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        utcFormat.timeZone = TimeZone.getTimeZone("UTC")

        viewModelScope.launch {
            val serverTime = fetchServerTime(token)

            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            utcFormat.timeZone = TimeZone.getTimeZone("UTC")

            val finalActionTime = serverTime?.trim()
                ?.replace("T", " ")
                ?.replace("Z", "")
                ?.substringBefore("+")
                ?: utcFormat.format(Date())

            Log.d("Attendance", "📅 Final Action Time to send: $finalActionTime")

            val isOnline = NetworkUtils.isNetworkAvailable(context) && NetworkUtils.hasRealInternet()
            val isTimeAuto = isDeviceTimeAndTimeZoneAutomatic(context)

            // ✳️ الشرط الجديد: لو أوفلاين و الوقت مش أوتوماتيكي → أوقف كل حاجة
            if (!isOnline && !isTimeAuto) {
                _showTimeChangedDialog.value = true
                Log.d("Attendance", "⚠️ الجهاز أوفلاين و الوقت متغير يدويًا - تم إيقاف الإجراء")
                onComplete("time_changed")
                return@launch
            }

            // 🔹 Update screen immediately (بعد التأكد من الشروط)
            if (action == "check_in") {
                _attendanceStatus.value = "checked_in"
                _lastCheckIn.value = finalActionTime
                cache.saveStatus("checked_in", finalActionTime, _lastCheckOut.value)
            } else if (action == "check_out") {
                _attendanceStatus.value = "checked_out"
                _lastCheckOut.value = finalActionTime
                cache.saveStatus("checked_out", _lastCheckIn.value, finalActionTime)
            }

            if (isOnline) {
                // 🔸 Online → Send directly
                val result = sendAttendanceAction(
                    token,
                    action,
                    _currentLat.value.toString(),
                    _currentLng.value.toString(),
                    finalActionTime
                )

                if (result != null) {
                    _message.value = result.message
                    _attendanceStatus.value = result.attendance_status ?: _attendanceStatus.value
                    _lastCheckIn.value = result.last_check_in ?: _lastCheckIn.value
                    _lastCheckOut.value = result.last_check_out ?: _lastCheckOut.value
                    _workedHours.value = result.worked_hours

                    cache.saveStatus(
                        _attendanceStatus.value,
                        _lastCheckIn.value,
                        _lastCheckOut.value
                    )

                    onComplete(result.attendance_status)
                } else {
                    enqueueWorkManager(token, action, finalActionTime)
                    onComplete("queued")
                }
            } else {
                // 🔸 Offline (بس الوقت سليم)
                enqueueWorkManager(token, action, finalActionTime)
                onComplete("queued")
            }
        }
    }


//    fun sendAttendance(token: String, action: String, onComplete: (String?) -> Unit = {}) {
//        val cache = AttendanceCache(context)
//
//        val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
//        utcFormat.timeZone = TimeZone.getTimeZone("UTC")
//
//        viewModelScope.launch {
//            val serverTime = fetchServerTime(token)
//
//            val utcFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
//            utcFormat.timeZone = TimeZone.getTimeZone("UTC")
//
//            val finalActionTime = serverTime?.trim()
//                ?.replace("T", " ")
//                ?.replace("Z", "")
//                ?.substringBefore("+") // ⛔️ يشيل أي timezone إضافي زي +00:00
//                ?: utcFormat.format(Date())
//
//            Log.d("Attendance", "📅 Final Action Time to send: $finalActionTime")
//            println("📅 Final Action Time to send (print): $finalActionTime")
//
//
//            //🔹 Update screen immediately
//            if (action == "check_in") {
//                _attendanceStatus.value = "checked_in"
//                _lastCheckIn.value = finalActionTime
//                cache.saveStatus("checked_in", finalActionTime, _lastCheckOut.value)
//            } else if (action == "check_out") {
//                _attendanceStatus.value = "checked_out"
//                _lastCheckOut.value = finalActionTime
//                cache.saveStatus("checked_out", _lastCheckIn.value, finalActionTime)
//            }
//
//            if (NetworkUtils.isNetworkAvailable(context) && NetworkUtils.hasRealInternet()) {
//                // Direct sending
//                val result = sendAttendanceAction(
//                    token,
//                    action,
//                    _currentLat.value.toString(),
//                    _currentLng.value.toString(),
//                    finalActionTime
//                )
//
//
//                if (result != null) {
//                    // 🔹Update official values from the server
//
//                    _message.value = result.message
//                    _attendanceStatus.value = result.attendance_status ?: _attendanceStatus.value
//                    _lastCheckIn.value = result.last_check_in ?: _lastCheckIn.value
//                    _lastCheckOut.value = result.last_check_out ?: _lastCheckOut.value
//                    _workedHours.value = result.worked_hours
//
//                    // Update Cache with official values
//                    cache.saveStatus(
//                        _attendanceStatus.value,
//                        _lastCheckIn.value,
//                        _lastCheckOut.value
//                    )
//
//                    onComplete(result.attendance_status)
//                } else {
//                    //Send failed, we keep local values
//                    enqueueWorkManager(token, action, finalActionTime)
//                    onComplete("queued")
//                }
//            } else {
//                if (!isDeviceTimeAndTimeZoneAutomatic(context)) {
//                    // المستخدم غيّر الوقت يدويًا → أظهر الـDialog فقط ولا تعمل أي أكشن
//                    _showTimeChangedDialog.value = true
//                    Log.d("Attendance", "⚠️ الوقت أو التايم زون متغير يدويًا - تم إيقاف الإجراء")
//                    onComplete("time_changed")
//                    return@launch // ⛔️ أوقف كل حاجة هنا
//                }
//                // Offline → WorkManager
//                enqueueWorkManager(token, action , finalActionTime)
//                onComplete("queued")
//            }
//        }
//    }

    // ✨ I separated the WorkManager part into a special function so that the code would be cleaner.
    private fun enqueueWorkManager(token: String, action: String , actionTime: String) {

        val data = workDataOf(
            "token" to token,
            "action" to action,
            "lat" to _currentLat.value.toString(),
            "lng" to _currentLng.value.toString(),
            "action_time" to actionTime
        )

        val request = OneTimeWorkRequestBuilder<AttendanceWorker>()
            .setInputData(data)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueue(request)

        Log.d("Attendance", "⏳ WorkManager job enqueued with data: $data")
        _message.value = "⏳ Attendance queued. Will send when network is back."
    }


    val attendanceStatus: StateFlow<String> = _attendanceStatus

    fun getAttendanceStatus(token: String) {
        viewModelScope.launch {
            val result = fetchAttendanceStatus(token)
            if (result != null) {
                _attendanceStatus.value = result.attendance_status ?: "unknown"
                _lastCheckIn.value = result.last_check_in
                _lastCheckOut.value = result.last_check_out
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



