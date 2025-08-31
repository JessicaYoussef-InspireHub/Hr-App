package com.example.myapplicationnewtest.settings.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (checked)
            MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.onSecondaryContainer,
        label = "bgColor"
    )

    Box (
        modifier = Modifier
            .width(80.dp)
            .height(40.dp)
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable { onCheckedChange(!checked) }
    ){
        Text(
            text = if (checked) "ON" else "OFF",
            color = if (checked) Color.White else Color.Black.copy(alpha = 0.6f),
            fontSize = 14.sp,
            modifier = Modifier
                .align(if (checked) Alignment.CenterStart else Alignment.CenterEnd)
                .padding(horizontal = 12.dp)
        )
        Box(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.CenterStart)
                .offset( if (checked) 40.dp else 4.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}


