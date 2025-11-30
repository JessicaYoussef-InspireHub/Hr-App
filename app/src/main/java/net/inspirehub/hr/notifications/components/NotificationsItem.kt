package net.inspirehub.hr.notifications.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.notifications.data.NotificationEntity
import java.text.SimpleDateFormat
import java.util.Locale

private fun formatTimestamp(timestamp: Long): Pair<String, String> {
    return try {
        val date = java.util.Date(timestamp)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val dateStr = dateFormat.format(date)
        val timeStr = timeFormat.format(date)

        Pair(dateStr, timeStr)

    } catch (e: Exception) {
        Pair("${e}Unknown date", "")
    }
}

@Composable
fun NotificationItem(notification: NotificationEntity) {
    val (_, timeStr) = formatTimestamp(notification.timestamp)
    val colors = appColors()
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("notif_prefs", Context.MODE_PRIVATE)
    val lastOpenTime = prefs.getLong("last_open_time", 0L)
    val isNew =  notification.timestamp > lastOpenTime

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceContainerHigh
        ),
    ){
//        Column(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//        )
//        {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.Bottom
//            ) {
//                Row(
//                    modifier = Modifier
//                        .padding(vertical = 8.dp),
//                    horizontalArrangement = Arrangement.Start,
//                    verticalAlignment = Alignment.Bottom
//                )
//                {
//                    if (notification.title == "Leave Request Approved") {
//                        Icon(
//                            imageVector = Icons.Default.CheckCircle,
//                            contentDescription = notification.title,
//                            modifier = Modifier.size(30.dp),
//                            tint = colors.tertiaryColor
//                        )
//                    } else {
//                        Icon(
//                            imageVector = Icons.Default.Cancel,
//                            contentDescription = notification.title,
//                            modifier = Modifier.size(30.dp),
//                            tint = colors.error
//                        )
//                    }
//                    Spacer(modifier = Modifier.width(8.dp))
//
//                    Text(
//                        text = notification.title,
//                        fontSize = 16.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = colors.onBackgroundColor
//                    )
//                }
//
//                Row(
//                    modifier = Modifier
//                        .padding(vertical = 8.dp),
//                    horizontalArrangement = Arrangement.Start,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = timeStr,
//                        fontSize = 14.sp,
//                        color = colors.onBackgroundColor.copy(alpha = 0.6f)
//                    )
//                    Spacer(modifier = Modifier.width(4.dp))
//                    if (isNew) {
//                    Icon(
//                        imageVector = Icons.Default.Circle,
//                        contentDescription = "New",
//                        modifier = Modifier.size(12.dp),
//                        tint = colors.tertiaryColor
//                    )}
//                }
//            }
//
//
//            Text(
//                text = notification.message,
//                fontSize = 16.sp,
//                color = colors.onBackgroundColor.copy(alpha = 0.8f),
//                modifier = Modifier.padding(top = 4.dp)
//            )
//        }

        Row (
            modifier = Modifier.fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
        )
        {
            if (notification.title == "Leave Request Approved") {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = notification.title,
                    modifier = Modifier.size(30.dp),
                    tint = colors.tertiaryColor
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = notification.title,
                    modifier = Modifier.size(30.dp),
                    tint = colors.error
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = notification.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackgroundColor
                    )
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = timeStr,
                            fontSize = 14.sp,
                            color = colors.onBackgroundColor.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        if (isNew) {
                            Icon(
                            imageVector = Icons.Default.Circle,
                            contentDescription = "New",
                            modifier = Modifier.size(12.dp),
                            tint = colors.tertiaryColor
                        )}
                    }
                }
                Text(
                    text = notification.message,
                    fontSize = 16.sp,
                    color = colors.onBackgroundColor.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 0.dp)
                )
            }
        }


    }
}


//@Composable
//fun NotificationsItem(
//    title: String ,
//    body: String ,
//    time: String,
//    showNew: Boolean
//) {
//    val colors = appColors()
//
//    Column {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            horizontalArrangement = Arrangement.Start,
//            verticalAlignment = Alignment.Top
//        ) {
//            if (title == "Accepted") {
//                Icon(
//                    imageVector = Icons.Default.CheckCircle,
//                    contentDescription = title,
//                    modifier = Modifier
//                        .size(40.dp),
//                    tint = colors.tertiaryColor
//                )
//            } else {
//                Icon(
//                    imageVector = Icons.Default.Cancel,
//                    contentDescription = title,
//                    modifier = Modifier
//                        .size(40.dp),
//                    tint = colors.error
//                )
//            }
//            Spacer(modifier = Modifier.width(8.dp))
//            Column {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(
//                        text = title,
//                        color = colors.onBackgroundColor,
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(
//                            text = time,
//                            color = colors.onBackgroundColor,
//                            fontSize = 15.sp,
//                            fontWeight = FontWeight.Light
//                        )
//                        if (showNew) {
//                            Spacer(modifier = Modifier.width(4.dp))
//                            Icon(
//                                imageVector = Icons.Default.Circle,
//                                contentDescription = "New",
//                                modifier = Modifier.size(10.dp),
//                                tint = colors.tertiaryColor
//                            )
//                        }
//                    }
//                }
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = body,
//                    color = colors.onBackgroundColor,
//                    fontSize = 15.sp,
//                    fontWeight = FontWeight.Medium
//                )
//            }
//        }
//    }
//}