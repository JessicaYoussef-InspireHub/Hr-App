package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.convertToArabicDigits

@Composable
fun TotalPrice(
    itemsName: String,
    itemsPrice: Double
){
    val color = appColors()
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding( 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(itemsName ,
            color = color.onBackgroundColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            convertToArabicDigits(itemsPrice.toString()),
            color = color.onBackgroundColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold)
    }
}