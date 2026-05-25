package net.inspirehub.hr.time_off.components

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
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.time_off.data.LeaveType
import androidx.core.graphics.toColorInt
import net.inspirehub.hr.appColors

@Composable
fun LeaveTypesLazyRow(
    leaveTypes: List<LeaveType>
){
    val colors = appColors()

    LazyRow(
        contentPadding = PaddingValues(horizontal = 15.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(35.dp)
    ) {
        items(leaveTypes) { leaveType ->
            val colorHex = leaveType.color?.ifEmpty { "#FFFFFF" } ?: "#FFFFFF"
            val color = try {
                Color(colorHex.toColorInt())
            } catch (e: Exception) {
                colors.tertiaryColor
            }

            val translatedName = leaveType.name

            Row(
                verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .wrapContentWidth()
            ){
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color, CircleShape)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = translatedName,
                    color = colors.onBackgroundColor,
                )
            }
        }
    }
}



