package com.example.myapplicationnewtest.notifications.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.appColors

@Composable
fun NotificationsItem(
    title: String ,
    body: String ,
    time: String,
    showNew: Boolean
) {
    val colors = appColors()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (title == "Accepted") {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = title,
                    modifier = Modifier
                        .size(40.dp),
                    tint = colors.tertiaryColor
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = title,
                    modifier = Modifier
                        .size(40.dp),
                    tint = colors.error
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = colors.onBackgroundColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = time,
                            color = colors.onBackgroundColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Light
                        )
                        if (showNew) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Circle,
                                contentDescription = "New",
                                modifier = Modifier.size(10.dp),
                                tint = colors.tertiaryColor
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = body,
                    color = colors.onBackgroundColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}