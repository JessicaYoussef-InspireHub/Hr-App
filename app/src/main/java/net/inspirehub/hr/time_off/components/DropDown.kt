package net.inspirehub.hr.time_off.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.inspirehub.hr.ArrowDropDownIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.SmallLoading
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.LeaveType
import net.inspirehub.hr.time_off.data.getLeaveDuration
import net.inspirehub.hr.utils.convertToArabicDigits
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.CheckIcon

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DropDown(
    leaveTypes: List<LeaveType>,
    selectedLeaveType: LeaveType?,
    onLeaveTypeSelected: (LeaveType) -> Unit,
    token: String,
    selectedStartDate: LocalDate,
    selectedEndDate: LocalDate,
    leaveDays: (Double) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = appColors()
    var isLoading = leaveTypes.isEmpty()

    val defaultType = leaveTypes.find { it.name.equals("Annual Leave", ignoreCase = true) }
    if (selectedLeaveType == null && defaultType != null) {
        onLeaveTypeSelected(defaultType)
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()

    if (isLoading) {

        SmallLoading()

    } else {

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    expanded = true
                },
            border = BorderStroke(
                2.dp,
                colors.surfaceColor
            ),
            colors = CardDefaults.cardColors(
                containerColor = colors.transparent
            ),
            shape = RoundedCornerShape(12.dp)

        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = selectedLeaveType?.name
                            ?: stringResource(R.string.select_leave_type),
                        color = colors.onBackgroundColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    selectedLeaveType?.remaining_balance?.let {

                        Spacer(Modifier.height(2.dp))

                        val remaining =
                            if (currentLanguage == "ar")
                                convertToArabicDigits(it.toString())
                            else
                                it.toString()

                        val original =
                            if (currentLanguage == "ar")
                                convertToArabicDigits(
                                    selectedLeaveType.original_balance.toString()
                                )
                            else
                                selectedLeaveType.original_balance.toString()

                        Text(
                            text = "$remaining ${stringResource(R.string.remaining_out_of)} $original",
                            color = colors.tertiaryColor,
                            fontSize = 13.sp
                        )
                    }
                }

                ArrowDropDownIcon(
                    expanded = expanded
                )
            }
        }
    }

    if (!isLoading) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    color = colors.surfaceContainerHigh,
                    shape = RoundedCornerShape(12.dp)
                )
                .width(320.dp),
            border = BorderStroke(
                2.dp,
                colors.surfaceColor
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            leaveTypes
                .filter { it.remaining_balance == null || it.remaining_balance > 0 }
                .forEach { item ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (selectedLeaveType?.id == item.id)
                                    colors.tertiaryColor
                                else
                                    colors.transparent
                            ),
                        text = {

                            Column {
                                Text(
                                    text = item.name,
                                    color = if (selectedLeaveType?.id == item.id)
                                        colors.onSecondaryColor
                                    else
                                        colors.onBackgroundColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                item.remaining_balance?.let {

                                    Spacer(Modifier.height(2.dp))

                                    val remaining =
                                        if (currentLanguage == "ar")
                                            convertToArabicDigits(it.toString())
                                        else
                                            it.toString()

                                    val original =
                                        if (currentLanguage == "ar")
                                            convertToArabicDigits(item.original_balance.toString())
                                        else
                                            item.original_balance.toString()

                                    Text(
                                        text = "$remaining ${stringResource(R.string.remaining_out_of)} $original",
                                        fontSize = 13.sp,
                                        color = colors.onBackgroundColor.copy(.6f)
                                    )
                                }
                            }
                        },
                        trailingIcon = {
                            if (selectedLeaveType?.id == item.id) {
                                CheckIcon()
                            }
                        },
                        onClick = {
                            onLeaveTypeSelected(item)
                            expanded = false

                            isLoading = true
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val formatter =
                                        DateTimeFormatter.ofPattern("MM-dd-yyyy")
                                    val response = getLeaveDuration(
                                        context = context,
                                        employeeToken = token,
                                        requestDateFrom = selectedStartDate.format(formatter),
                                        requestDateTo = selectedEndDate.format(formatter),
                                        leaveTypeId = item.id,
                                    )
                                    withContext(Dispatchers.Main) {
                                        leaveDays(response.result.data?.days ?: 1.0)
                                        isLoading = false
                                    }
                                } catch (e: Exception) {
                                    Log.e("LEAVE_DURATION", e.message ?: "error")
                                    withContext(Dispatchers.Main) {
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    )
                }
        }
    }
}