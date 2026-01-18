package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun TotalPrice(
    itemsName: String,
    itemsPrice: Double
){
    val color = appColors()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(itemsName ,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = color.onBackgroundColor)

        Text(itemsPrice.toString() ,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = color.onBackgroundColor)
    }
}