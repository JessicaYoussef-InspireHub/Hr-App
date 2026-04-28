package net.inspirehub.hr.time_off.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.LeaveType
import net.inspirehub.hr.utils.convertToArabicDigits
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyActualTimeOff(
    leaveTypes: List<LeaveType> ,
) {

    val currentLocale = Locale.getDefault()
    val visibleLeaveTypes = leaveTypes.filter { it.requires_allocation == "yes" }
    val colors = appColors()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            itemsIndexed(visibleLeaveTypes) { index, leave ->
                 val balance = if (currentLocale.language == "ar") {

                     convertToArabicDigits((leave.remaining_balance ?: 0f).toString())
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
                            label1 = leave.name,
                            label2 = balance,
                            label3 = if (currentLocale.language == "ar") {
                                if (leave.request_unit == "day" || leave.request_unit == "half_day") "من الأيام المتاحة" else "من الساعات المتاحة"
                            } else {
                                if (leave.request_unit == "day" || leave.request_unit == "half_day") "DAYS AVAILABLE" else "HOURS AVAILABLE"
                            },
                            showIcon = leave.name == "Annual Leaves"
                        )
                    }
                if ( index < visibleLeaveTypes.lastIndex) {
                    VerticalDivider(
                        modifier = Modifier
                            .height(120.dp)
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