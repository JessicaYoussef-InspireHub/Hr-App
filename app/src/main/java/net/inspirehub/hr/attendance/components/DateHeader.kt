package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.DateRangeIcon
import net.inspirehub.hr.DescriptionIcon
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.utils.formatLocalizedDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateHeader(
    date: String,
    hasPermission: Boolean,
    size: Int = 18
) {
    val colors = appColors()
    val context = LocalContext.current
    val currentLanguage = SharedPrefManager(context).getLanguage()



    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            DateRangeIcon()

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = formatLocalizedDate(date, currentLanguage),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = colors.onBackgroundColor
            )
        }

        if (hasPermission) {
            DescriptionIcon(
                size = size.dp
            )
        }
    }
}