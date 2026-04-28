package net.inspirehub.hr.time_off.components

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.SmallLoading
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.LeaveType
import net.inspirehub.hr.time_off.data.getLeaveDuration
import net.inspirehub.hr.utils.convertToArabicDigits
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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
    val currentLanguage = sharedPrefManager.getLanguage() // "ar" or "en"


    Column {
        if (isLoading) {
            SmallLoading()
        } else {
            Row(
                modifier = Modifier.clickable {
                    expanded = true

                }
            ) {
                Text(
                    text = selectedLeaveType?.let {
                        val translatedName = it.name
                        val remaining = it.remaining_balance?.toString() ?: ""
                        val original = it.original_balance?.toString() ?: ""

                        if (it.remaining_balance != null)
                            "$translatedName (${
                                if (currentLanguage == "ar") convertToArabicDigits(
                                    remaining
                                ) else remaining
                            } ${stringResource(R.string.remaining_out_of)} ${
                                if (currentLanguage == "ar") convertToArabicDigits(
                                    original
                                ) else original
                            })"
                        else
                            translatedName
                    } ?: stringResource(R.string.select_leave_type),
                    color = colors.onBackgroundColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)

                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown arrow",
                    tint = colors.onBackgroundColor
                )
            }
        }

        if (!isLoading) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(
                        color = colors.onSecondaryColor,
                    )
            ) {
                leaveTypes
                    .filter { it.remaining_balance == null || it.remaining_balance > 0 }
                    .forEach { item ->
                        val translatedName = item.name
                        val remaining = item.remaining_balance ?: 0
                        val original = item.original_balance ?: 0

                        DropdownMenuItem(
                            text = {
                                Text(
                                    if (item.remaining_balance != null)
                                        "$translatedName (${
                                            if (currentLanguage == "ar") convertToArabicDigits(
                                                remaining.toString()
                                            ) else remaining
                                        } ${stringResource(R.string.remaining_out_of)} ${
                                            if (currentLanguage == "ar") convertToArabicDigits(
                                                original.toString()
                                            ) else original
                                        })"
                                    else
                                        translatedName,
                                    color = colors.onBackgroundColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                onLeaveTypeSelected(item)
                                expanded = false

                                isLoading = true
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
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
}