package com.example.myapplicationnewtest

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.surface,
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
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = MaterialTheme.colorScheme.surface,
                    unselectedTextColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = androidx.compose.ui.graphics.Color.Transparent
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
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = MaterialTheme.colorScheme.surface,
                    unselectedTextColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )

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
                    selectedIconColor = MaterialTheme.colorScheme.tertiary,
                    selectedTextColor = MaterialTheme.colorScheme.tertiary,
                    unselectedIconColor = MaterialTheme.colorScheme.surface,
                    unselectedTextColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = androidx.compose.ui.graphics.Color.Transparent
                )
            )
        }
    }
}