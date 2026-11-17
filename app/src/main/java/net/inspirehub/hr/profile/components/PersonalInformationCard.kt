package net.inspirehub.hr.profile.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.MyDivider
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R
import net.inspirehub.hr.profile.data.loadProfileFromPrefs
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PersonalInformationCard(
    title: String
) {
    val colors = appColors()
    val context = LocalContext.current
    val profile = remember { loadProfileFromPrefs(SharedPrefManager(context)) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.inverseSurface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colors.onBackgroundColor
            )
            SectionCardItem(
                icon = Icons.Default.MailOutline,
                label = stringResource(R.string.email_address),
                value = profile.email
            )

            MyDivider(
                color = colors.onBackgroundColor.copy(alpha = 0.08f),
                thickness = 1
            )

            SectionCardItem(
                icon = Icons.Default.Cake,
                label = stringResource(R.string.birthday),
                value = profile.birthday
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatBirthday(raw: String, locale: Locale): String {
    if (raw.equals("false", ignoreCase = true)) return "-"
    return try {
        java.time.LocalDate.parse(raw.take(10)).format(
            java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
        )
    } catch (_: Exception) {
        raw
    }
}