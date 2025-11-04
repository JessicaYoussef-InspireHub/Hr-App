package net.inspirehub.hr.time_off.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun MyActualTimeOffText(
    label1: String,
    label2: String,
    label3: String,
    showIcon: Boolean = false
) {
    val colors = appColors()

    Column(
        modifier = Modifier.padding( vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            label1 ,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = colors.tertiaryColor
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Icon(
                    imageVector = Icons.Filled.BeachAccess,
                    contentDescription = "Vacation Icon",
                    modifier = Modifier
                        .padding(end = 6.dp),
                    tint = colors.tertiaryColor
                )
            }
            Text(
                label2,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colors.tertiaryColor
            )

        }
        Text(
            label3 ,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = colors.tertiaryColor
        )
    }
}