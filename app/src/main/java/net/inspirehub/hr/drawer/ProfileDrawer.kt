package net.inspirehub.hr.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.GeneralIcon
import net.inspirehub.hr.KeyboardArrowRightIcon
import net.inspirehub.hr.MyDivider
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors

@Composable
fun ProfileDrawer(
    navController : NavController,
    onClose: () -> Unit,
) {

    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val employeeEmail = sharedPref.getLoginEmail() ?: ".."
    val employeeFullName = sharedPref.getEmployeeName() ?: "User"
    val employeeFirstName = employeeFullName.split(" ").firstOrNull() ?: employeeFullName

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.onSecondaryColor)
            .padding(end = 8.dp, start = 28.dp)
            .padding(vertical = 24.dp)
            .clickable {
                navController.navigate("ProfileScreen") {
                    launchSingleTop = true
                }
                onClose()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(colors.onSecondaryColor)
                    .border(
                        width = 3.dp,
                        color = colors.tertiaryColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                GeneralIcon(
                    imageVector = Icons.Default.Person,
                    contentDescription = stringResource(R.string.employee),
                    tint = colors.tertiaryColor,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(20.dp))


            Column {
                Text(
                    text = employeeFirstName,
                    color = colors.tertiaryColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = employeeEmail,
                    color = colors.onBackgroundColor,
                    fontSize = 12.sp
                )
            }
        }


        KeyboardArrowRightIcon( onClick = { } )
    }
    MyDivider(
        thickness = 1,
        horizontalPadding = 15,
        color = colors.onBackgroundColor.copy(alpha = 0.6f)
    )
}