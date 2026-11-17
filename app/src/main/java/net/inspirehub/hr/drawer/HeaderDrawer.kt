package net.inspirehub.hr.drawer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors

@Composable
fun HeaderDrawer(
    onClose: () -> Unit
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val employeeFullName = sharedPref.getEmployeeName() ?: "User"
    val employeeFirstName = employeeFullName.split(" ").firstOrNull() ?: employeeFullName

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.tertiaryColor)
            .statusBarsPadding()
            .padding(top = 8.dp, bottom = 24.dp, end = 8.dp, start = 24.dp)
    ) {
        Column {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                CloseIcon(
                    tint = colors.onSecondaryColor,
                    onClick = onClose
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.inspire_hub_logo_drawer),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                )

                Spacer(Modifier.width(20.dp))

                VerticalDivider(
                    modifier = Modifier.height(50.dp) ,
                    thickness = 2.dp ,
                    color = colors.onSecondaryColor )

                Spacer(Modifier.width(20.dp))

                Column {
                    Text(
                        text = stringResource(R.string.app_name),
                        color = colors.onSecondaryColor,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${stringResource(R.string.welcome)} $employeeFirstName",
                        color = colors.onSecondaryColor,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}