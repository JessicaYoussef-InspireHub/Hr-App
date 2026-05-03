package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.convertToArabicDigits

@Composable
fun TimePickerBox(
    selectedHour: String?,
    selectedMinute: String?,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit,
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()
    val hours = (0..23).map { it.toString() }
    val minutes = (0..59).map { it.toString().padStart(2, '0') }

    fun formatNumberByLanguage(value: String): String {
        return if (currentLanguage == "ar") {
            convertToArabicDigits(value)
        } else {
            value
        }
    }

    fun formatHourTo12(hour: String): String {
        val h = hour.toIntOrNull() ?: return hour

        val hour12 = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }

        return "$hour12"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        // 🔹 HOURS BOX
        Box {
            var expandedHour by remember { mutableStateOf(false) }

            Text(
                text = formatNumberByLanguage(formatHourTo12(selectedHour ?: "9")),
                color = colors.onBackgroundColor,
                modifier = Modifier
                    .clickable { expandedHour = true }
                    .padding(start = 10.dp),
                fontSize = 14.sp
            )

            DropdownMenu(
                expanded = expandedHour,
                onDismissRequest = { expandedHour = false },
                modifier = Modifier.background(colors.onSecondaryColor)
            ) {
                hours.forEach { hour ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                formatNumberByLanguage(hour),
                                color = colors.onBackgroundColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = {
                            onHourChange(hour)
                            expandedHour = false
                        }
                    )
                }
            }
        }

        Text(
            " : ",
            color = colors.onBackgroundColor,
            fontSize = 18.sp
        )

        // 🔹 MINUTES BOX
        Box {
            var expandedMinute by remember { mutableStateOf(false) }

            Text(
                text = formatNumberByLanguage(selectedMinute ?: "00"),
                color = colors.onBackgroundColor,
                modifier = Modifier
                    .clickable { expandedMinute = true }
                    .padding(start = 10.dp),
                fontSize = 14.sp
            )

            DropdownMenu(
                expanded = expandedMinute,
                onDismissRequest = { expandedMinute = false },
                modifier = Modifier.background(colors.onSecondaryColor)
            ) {
                minutes.forEach { minute ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                formatNumberByLanguage(minute),
                                color = colors.onBackgroundColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = {
                            onMinuteChange(minute)
                            expandedMinute = false
                        }
                    )
                }
            }
        }
    }
}