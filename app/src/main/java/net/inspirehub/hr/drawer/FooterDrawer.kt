package net.inspirehub.hr.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.BuildConfig
import net.inspirehub.hr.MyDivider
import net.inspirehub.hr.OutlinedInfoIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun FooterDrawer(
    onLogoutClick: () -> Unit
) {
    val colors = appColors()

    MyDivider(
        thickness = 1,
        color = colors.onBackgroundColor.copy(alpha = 0.6f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedInfoIcon()

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "${stringResource(R.string.version)} ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                color = colors.onBackgroundColor.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }


        Row (
            modifier = Modifier.clickable {  onLogoutClick() },
            verticalAlignment = Alignment.CenterVertically,
        ){
            VerticalDivider(
                thickness = 2.dp,
                modifier = Modifier.height(25.dp),
                color = colors.onBackgroundColor.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = stringResource(R.string.logout) ,
                tint = colors.error,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = stringResource(R.string.logout),
                color = colors.error,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}