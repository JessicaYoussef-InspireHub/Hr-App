package com.example.myapplicationnewtest.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.appColors

@Composable
fun SettingsLanguage(
    label: String,
    icon: Painter,
    onClick: () -> Unit
) {
    val colors = appColors()

    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 20.dp, horizontal = 35.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = icon,
            contentDescription = label,
            Modifier
                .width(20.dp)
                .height(20.dp)
        )
        Text(
            label,
            color = colors.onBackgroundColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        )
    }
}