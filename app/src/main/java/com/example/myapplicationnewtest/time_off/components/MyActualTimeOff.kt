package com.example.myapplicationnewtest.time_off.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplicationnewtest.appColors
import com.example.myapplicationnewtest.time_off.data.LeaveType
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyActualTimeOff(
    leaveTypes: List<LeaveType> ,
) {

    fun translateLeaveName(name: String, locale: Locale): String {
        return if (locale.language == "ar" ) {
            when (name) {
                "Annual Leave" -> "اجازة سنوية"
                "Sick Time Off" -> "أجازة مرضية"
                "Unpaid" -> "بدون راتب"
                "Permission" -> "إذن"
                else -> name
            }
        } else {
            name
        }
    }

    fun String.replaceDigitsWithArabic(): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return this.map { char ->
            if (char.isDigit()) arabicDigits[char.digitToInt()] else char
        }.joinToString("")
    }

    val currentLocale = Locale.getDefault()
    val visibleLeaveTypes = leaveTypes.filter { it.requires_allocation == "yes" }
    val colors = appColors()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .background(colors.onSecondaryColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center

        ) {

            visibleLeaveTypes.forEachIndexed { index, leave ->
                    val balance = if (currentLocale.language == "ar") {
                        (leave.remaining_balance ?: 0f).toString().replaceDigitsWithArabic()
                    } else {
                        (leave.remaining_balance ?: 0f).toString()
                    }

                    Box(
                        modifier = Modifier
                            .width(200.dp)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        MyActualTimeOffText(
                            label1 = translateLeaveName(leave.name, currentLocale),
                            label2 = balance,
                            label3 = if (currentLocale.language == "ar") {
                                if (leave.request_unit == "day" || leave.request_unit == "half_day") "من الأيام المتاحة" else "من الساعات المتاحة"
                            } else {
                                if (leave.request_unit == "day" || leave.request_unit == "half_day") "DAYS AVAILABLE" else "HOURS AVAILABLE"
                            },
                            showIcon = leave.name == "Annual Leave" || translateLeaveName(leave.name, currentLocale) == "اجازة سنوية"
                        )
                    }
//                }
                if ( index < visibleLeaveTypes.lastIndex) {
                    VerticalDivider(
                        modifier = Modifier
                            .height(150.dp)
                            .width(1.dp)
                            .background(colors.inverseOnSurface)
                    )
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.inverseOnSurface)
        )
    }
}