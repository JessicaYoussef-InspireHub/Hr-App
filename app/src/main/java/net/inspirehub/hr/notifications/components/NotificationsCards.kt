package net.inspirehub.hr.notifications.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun NotificationsCards(
    exitTime: String?
){
    data class NotificationItem(
        val date: String,
        val status: String,
        val message: String,
        val time: String
    )

    val colors = appColors()
    val serverDateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val localDateTimeFormat = SimpleDateFormat("d-M-yyyy h:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("d-M-yyyy", Locale.getDefault())
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    val lastExitDate = exitTime?.let {
        try {
            serverDateTimeFormat.parse(it)
        } catch (e: Exception) {
            Log.d("ServerTime", "❌ Error parsing server exit time: $e")
            null
        }
    }


    val notifications = listOf(
        NotificationItem("29-10-2018", "Accepted", "Your Permission planned on 28-10-2025 11:00 has been accepted", "1:00 pm"),
        NotificationItem("02-11-2025", "Accepted", "Your Permission planned on 28-10-2025 11:00 has been accepted", "5:00 pm"),
        NotificationItem("01-11-2025", "Accepted", "Your Permission planned on 28-10-2025 11:00 has been accepted", "1:00 pm"),
        NotificationItem("16-08-2025", "Accepted", "Your Annual leave planned on 28-10-2025 11:00 has been accepted", "11:00 am"),
        NotificationItem("03-05-2025", "Refused", "Your Permission planned on 28-10-2025 11:00 has been refused", "5:00 pm"),
        NotificationItem("03-05-2025", "Accepted", "Your Annual leave planned on 28-10-2025 11:00 has been accepted", "8:00 pm"),
        NotificationItem("16-08-2025", "Accepted", "Your Annual leave planned on 28-10-2025 11:00 has been accepted", "5:00 pm"),
        NotificationItem("16-08-2025", "Refused", "Your Annual leave planned on 28-10-2025 11:00 has been accepted", "12:00 pm"),
        NotificationItem("16-08-2025", "Accepted", "Your Annual leave planned on 28-10-2025 11:00 has been accepted", "4:00 pm")
    )


    val sortedNotifications = notifications.sortedByDescending {
        dateFormat.parse(it.date)
    }

    val groupedByDate = sortedNotifications.groupBy { it.date }

    fun getDisplayDate(dateString: String): String {
        val notificationDate = dateFormat.parse(dateString)
        val today = Calendar.getInstance()
        val notificationCal = Calendar.getInstance().apply { time = notificationDate!! }

        val diffInMillis = today.timeInMillis - notificationCal.timeInMillis
        val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()

        return when (diffInDays) {
            0 -> "Today"
            1 -> "Yesterday"
            else -> dateString
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.onSecondaryColor
        )
    ){
        Column {
            groupedByDate.toList().forEachIndexed { index, (date, items) ->

                val displayDate = getDisplayDate(date)

                Text(displayDate ,
                    color = colors.onBackgroundColor ,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                val sortedByTime = items.sortedByDescending { timeFormat.parse(it.time) }

                sortedByTime.forEachIndexed { itemIndex, item ->

                    val itemDateTime = localDateTimeFormat.parse("${item.date} ${item.time}")

                    val isNew = lastExitDate == null || (itemDateTime != null && itemDateTime.after(lastExitDate))


                    NotificationsItem(
                        item.status,
                        item.message,
                        item.time,
                        showNew = isNew
                    )
                    Spacer(Modifier.height(8.dp))
                    if (index != groupedByDate.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth(),
                            color = colors.surfaceColor
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}