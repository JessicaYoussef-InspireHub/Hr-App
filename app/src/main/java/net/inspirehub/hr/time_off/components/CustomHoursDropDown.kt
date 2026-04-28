package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.convertToArabicDigits


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomHourDropDown(
    label: String,
    selectedPermissionHour: String?,
    onPermissionHourSelected: (String) -> Unit,
) {
    val colors = appColors()
    var expanded by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()


    fun translateAmPm(amPm: String, language: String): String {
        return if (language == "ar") {
            when (amPm) {
                "AM" -> "ص"
                "PM" -> "م"
                else -> amPm
            }
        } else {
            amPm
        }
    }


    fun generateTimeList(): List<String> {
        val times = mutableListOf<String>()
        val amPm = listOf("AM", "PM")

        for (half in 0 until 2) {
            val suffix = translateAmPm(amPm[half], currentLanguage)

            val hours = listOf(12) + (1..11)

            for (hour in hours) {
                val hourStr = if (currentLanguage == "ar") convertToArabicDigits(hour.toString()) else hour.toString()
                val zeroStr = if (currentLanguage == "ar") convertToArabicDigits("00") else "00"
                val halfStr = if (currentLanguage == "ar") convertToArabicDigits("30") else "30"

                times.add("$hourStr:$zeroStr $suffix")
                times.add("$hourStr:$halfStr $suffix")
            }
        }
        return times
    }

    val times = remember(currentLanguage) { generateTimeList() }

    Row{
        FirstText(label)
        Spacer(modifier = Modifier.width(5.dp))
        Column {
            Row (
                modifier = Modifier.clickable { expanded = true }
            ){
                Text(
                    text = selectedPermissionHour ?: " ",
                    color = colors.onBackgroundColor,
                    fontSize = 14.sp,
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "ArrowDropDown",
                    tint = colors.onBackgroundColor,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.onSecondaryColor)
            ){
                times.forEach { time ->
                    DropdownMenuItem(
                        text = {
                            Text(
                               time,
                                color = colors.onBackgroundColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = {
                            onPermissionHourSelected(time)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}