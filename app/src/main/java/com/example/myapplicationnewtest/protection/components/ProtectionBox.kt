package com.example.myapplicationnewtest.protection.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.appColors

@Composable
fun ProtectionBox(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
){
    val colors = appColors()

    Box (
        modifier = Modifier.fillMaxWidth()
            .border(2.dp, colors.tertiaryColor , shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ){
        Row (
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ){
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .padding(end = 6.dp).size(40.dp),
                tint = colors.tertiaryColor,

            )
            Text(
                label,
                color = colors.tertiaryColor,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp)
        }
    }
}