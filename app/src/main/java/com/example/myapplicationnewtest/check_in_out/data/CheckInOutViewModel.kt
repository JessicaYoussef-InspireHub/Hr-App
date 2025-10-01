package com.example.myapplicationnewtest.check_in_out.data

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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

    @SuppressLint("MissingPermission")
    fun checkLocationAndDistance(targetLat: Double, targetLng: Double, allowedDistance: Double) {
        Log.d("disable", "checkLocationAndDistance called with targetLat=$targetLat, targetLng=$targetLng, allowedDistance=$allowedDistance")

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
                Log.d("disable", "✅ Target Location: $targetLat, $targetLng | Allowed Distance: $allowedDistance")


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
                Log.d("disable", "allowedDistance: $allowedDistance | isWithinDistance: ${distance <= allowedDistance}")
                Log.d("disable", "🚩 Calculated Distance: $distance meters")

                _isWithinDistance.value = distance <= allowedDistance
                Log.d("disable", "✅ isWithinDistance updated to: ${_isWithinDistance.value}")

            } else {
                Log.e("Location", "❌ Location is null")
                Log.e("disable", "❌ Location is null")

            }
        }
    }

    fun setMessage(msg: String) {
        _message.value = msg
    }


    fun sendAttendance(token: String, action: String, onComplete: (String?) -> Unit = {}) {
        viewModelScope.launch {
            val result = sendAttendanceAction(
                token,
                action,
                _currentLat.value.toString(),
                _currentLng.value.toString())
            if (result != null) {
                _message.value = result.message
                _attendanceStatus.value = result.attendance_status ?: "unknown"

                // 🔥 Update last check in/out too
                _lastCheckIn.value = result.last_check_in
                _lastCheckOut.value = result.last_check_out
                _workedHours.value = result.worked_hours


                getAttendanceStatus(token)

                onComplete(result.attendance_status)
            } else {
                _message.value = "❌ Failed to update attendance"
                onComplete(null)
            }
        }
    }


    private val _attendanceStatus = MutableStateFlow("Loading...")
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
                val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
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



