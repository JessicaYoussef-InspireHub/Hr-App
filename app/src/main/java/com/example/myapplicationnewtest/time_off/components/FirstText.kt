package com.example.myapplicationnewtest.time_off.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.appColors

@Composable
fun FirstText(
    label: String,
){
    val colors = appColors()

    Text(
        text = label,
        color = colors.onBackgroundColor,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}