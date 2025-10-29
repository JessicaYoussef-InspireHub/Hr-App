package com.example.myapplicationnewtest.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myapplicationnewtest.time_off.data.LeaveType
import androidx.core.graphics.toColorInt
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.appColors

@Composable
fun LeaveTypesLazyRow(
    leaveTypes: List<LeaveType>
){
    val colors = appColors()
    val context = LocalContext.current
    val sharedPrefManager = SharedPrefManager(context)
    val language = sharedPrefManager.getLanguage()

    fun translateLeaveTypeName(name: String, language: String): String {
        if (language != "ar") return name

        return when (name.trim().lowercase()) {
            "sick time off" -> "إجازة مرضية"
            "unpaid" -> "إجازة بدون راتب"
            "permission" -> "إذن"
            "annual leave" -> "إجازة سنوية"
            else -> name
        }
    }

    LazyRow(
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(35.dp)
    ) {
        items(leaveTypes) { leaveType ->
            val colorHex = leaveType.color?.ifEmpty { "#FFFFFF" } ?: "#FFFFFF"
            val color = try {
                Color(colorHex.toColorInt())
            } catch (e: Exception) {
                Color.White
            }

            val translatedName = translateLeaveTypeName(leaveType.name, language)


            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.width(140.dp).padding(vertical = 4.dp)
            ){
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = translatedName,
                    color = colors.onBackgroundColor
                )
            }
        }
    }
}



