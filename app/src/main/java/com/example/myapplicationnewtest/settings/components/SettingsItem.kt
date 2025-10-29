package com.example.myapplicationnewtest.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
fun SettingsItem(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    trailingIcon: (@Composable (() -> Unit))? = null
) {
    val colors = appColors()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 20.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = colors.onBackgroundColor,
                )
            Text(
                label,
                color = colors.onBackgroundColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        }
        if (trailingIcon != null) {
            trailingIcon()

        }
    }
}