package net.inspirehub.hr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavController,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val colors = appColors()
    var moreMenuExpanded by remember { mutableStateOf(false) }

    Column {
        HorizontalDivider(
            thickness = 1.dp, color = colors.inverseOnSurface
        )
        Box {
            NavigationBar(
                containerColor = colors.onSecondaryColor,
                contentColor = colors.onBackgroundColor,
            ) {
                NavigationBarItem(
                    selected = currentRoute == "CheckInOutScreen",
                    onClick = {
                        if (currentRoute != "CheckInOutScreen") {
                            navController.navigate("CheckInOutScreen") {
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(stringResource(R.string.home)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.tertiaryColor,
                        selectedTextColor = colors.tertiaryColor,
                        unselectedIconColor = colors.onBackgroundColor,
                        unselectedTextColor = colors.onBackgroundColor,
                        indicatorColor = colors.transparent
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "TimeOffScreen",
                    onClick = {
                        if (currentRoute != "TimeOffScreen") {
                            navController.navigate("TimeOffScreen") {
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = { Icon(Icons.Default.BeachAccess, contentDescription = stringResource(R.string.time_off)) },
                    label = { Text(stringResource(R.string.time_off)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.tertiaryColor,
                        selectedTextColor = colors.tertiaryColor,
                        unselectedIconColor = colors.onBackgroundColor,
                        unselectedTextColor = colors.onBackgroundColor,
                        indicatorColor = colors.transparent
                    )
                )

                NavigationBarItem(
                    selected = currentRoute == "NotificationsScreen",
                    onClick = {
                        if (currentRoute != "NotificationsScreen") {
                            navController.navigate("NotificationsScreen") {
                                launchSingleTop = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.notification)
                        )
                    },
                    label = { Text(stringResource(R.string.notification)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.tertiaryColor,
                        selectedTextColor = colors.tertiaryColor,
                        unselectedIconColor = colors.onBackgroundColor,
                        unselectedTextColor = colors.onBackgroundColor,
                        indicatorColor = colors.transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        moreMenuExpanded = true
                    },
                    icon = { Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.more)) },
                    label = { Text(stringResource(R.string.more)) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.tertiaryColor,
                        selectedTextColor = colors.tertiaryColor,
                        unselectedIconColor = colors.onBackgroundColor,
                        unselectedTextColor = colors.onBackgroundColor,
                        indicatorColor = colors.transparent
                    )
                )
            }


            DropdownMenu(
                expanded = moreMenuExpanded,
                onDismissRequest = { moreMenuExpanded = false },
                offset = DpOffset(x = (-1).dp, y = (-3).dp),
                modifier = Modifier.background(colors.surfaceContainerHigh)
            ) {
                DropdownMenuItem(
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                stringResource(R.string.settings),
                                tint = colors.onBackgroundColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.settings),
                                color = colors.onBackgroundColor
                            )
                        }
                    }, onClick = {
                        moreMenuExpanded = false
                        if (currentRoute != "SettingsScreen") {
                            navController.navigate("SettingsScreen") {
                                launchSingleTop = true
                            }
                        }
                    }
                )

                DropdownMenuItem(
                    text =
                        {
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Icon(Icons.Default.LunchDining,
                                    stringResource(R.string.lunch),
                                    tint = colors.onBackgroundColor )
                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    stringResource(R.string.lunch) ,
                                    color = colors.onBackgroundColor
                                )
                            }
                        },
                    onClick = {
                        moreMenuExpanded = false
                        if (currentRoute != "LunchScreen") {
                            navController.navigate("LunchScreen") {
                                launchSingleTop = true
                            }
                        }
                    }
                )

                DropdownMenuItem(
                    text =
                        {
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ){
                                Icon(Icons.Default.Paid,
                                    stringResource(R.string.expenses)  ,
                                    tint = colors.onBackgroundColor )
                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    stringResource(R.string.expenses) ,
                                    color = colors.onBackgroundColor
                                )
                            }
                        },
                    onClick = {
                        moreMenuExpanded = false
                        if (currentRoute != "ExpensesScreen") {
                            navController.navigate("ExpensesScreen") {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}