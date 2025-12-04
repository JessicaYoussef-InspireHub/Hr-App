package net.inspirehub.hr

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavController,
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val colors = appColors()

    Column {
        HorizontalDivider(
            thickness = 1.dp,
            color = colors.inverseOnSurface
        )
        NavigationBar(
            containerColor = colors.onSecondaryColor,
            contentColor = colors.onBackgroundColor,
        ){
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
                    selectedIconColor =  colors.tertiaryColor,
                    selectedTextColor =  colors.tertiaryColor,
                    unselectedIconColor =  colors.onBackgroundColor,
                    unselectedTextColor =  colors.onBackgroundColor,
                    indicatorColor =  colors.transparent
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
                icon = { Icon(Icons.Default.BeachAccess, contentDescription = "Time Off") },
                label = { Text(stringResource(R.string.time_off)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =  colors.tertiaryColor,
                    selectedTextColor =  colors.tertiaryColor,
                    unselectedIconColor =  colors.onBackgroundColor,
                    unselectedTextColor =  colors.onBackgroundColor,
                    indicatorColor =  colors.transparent
                )
            )
//
//            NavigationBarItem(
//                selected = currentRoute == "LunchScreen",
//                onClick = {
//                    if (currentRoute != "LunchScreen") {
//                        navController.navigate("LunchScreen") {
//                            launchSingleTop = true
//                        }
//                    }
//                },
//                icon = { Icon(Icons.Default.LunchDining, contentDescription = "Lunch") },
//                label = { Text(stringResource(R.string.lunch)) },
//                colors = NavigationBarItemDefaults.colors(
//                    selectedIconColor = colors.tertiaryColor,
//                    selectedTextColor = colors.tertiaryColor,
//                    unselectedIconColor = colors.onBackgroundColor,
//                    unselectedTextColor = colors.onBackgroundColor,
//                    indicatorColor = colors.transparent
//                )
//            )

            NavigationBarItem(
                selected = currentRoute == "SettingsScreen",
                onClick = {
                    if (currentRoute != "SettingsScreen") {
                        navController.navigate("SettingsScreen") {
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text(stringResource(R.string.settings)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor =  colors.tertiaryColor,
                    selectedTextColor =  colors.tertiaryColor,
                    unselectedIconColor =  colors.onBackgroundColor,
                    unselectedTextColor =  colors.onBackgroundColor,
                    indicatorColor =  colors.transparent
                )
            )
        }
    }
}