package net.inspirehub.hr.bottom_app_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun BottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val colors = appColors()
    val itemColor = colors.onBackgroundColor
    val selectedColor = colors.tertiaryColor

    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .background(colors.onSecondaryColor)
        ) {

            // Divider behind the profile icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = 5.dp)
                    .background(colors.inverseOnSurface)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .height(68.dp)
                    .padding(horizontal = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // Home
                BottomBarItem(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = stringResource(R.string.home)
                        )
                    },
                    label = stringResource(R.string.home),
                    selected = currentRoute == "CheckInOutScreen"
                ) {
                    navigateTo(navController, "CheckInOutScreen")
                }

                // Time Off
                BottomBarItem(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Default.BeachAccess,
                            contentDescription = stringResource(R.string.time_off)
                        )
                    },
                    label = stringResource(R.string.time_off),
                    selected = currentRoute == "TimeOffScreen"
                ) {
                    navigateTo(navController, "TimeOffScreen")
                }

                // Center Profile
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .offset(y = (-30).dp)
                            .size(58.dp)
                            .background(
                                color = if (currentRoute == "ProfileScreen")
                                    selectedColor
                                else
                                    itemColor,
                                shape = CircleShape
                            )
                            .border(
                                width = 4.dp,
                                color = colors.onSecondaryColor,
                                shape = CircleShape
                            )
                            .clickable {
                                navigateTo(navController, "ProfileScreen")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.profile),
                            tint = colors.onSecondaryColor,
                            modifier = Modifier.size(29.dp)
                        )
                    }
                }

                // Notifications
                BottomBarItem(
                    modifier = Modifier.weight(1f),
                    icon = {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.notification)
                        )
                    },
                    label = stringResource(R.string.notification),
                    selected = currentRoute == "NotificationsScreen"
                ) {
                    navigateTo(navController, "NotificationsScreen")
                }

                // Attend
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    BottomBarItem(
                        modifier = Modifier,
                        icon = {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = stringResource(R.string.attend)
                            )
                        },
                        label = stringResource(R.string.attend),
                        selected = currentRoute == "AttendanceScreen"
                    ) {
                        navigateTo(navController, "AttendanceScreen")
                    }
                }
            }
        }
    }
}

private fun navigateTo(
    navController: NavController,
    route: String
) {
    navController.navigate(route) {
        launchSingleTop = true
    }
}