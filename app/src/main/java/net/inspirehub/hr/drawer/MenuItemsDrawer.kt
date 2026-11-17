package net.inspirehub.hr.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R

@Composable
fun MenuItemsDrawer(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        DrawerItem(
            label = stringResource(R.string.home),
            icon = Icons.Default.Home,
            selected = currentRoute == "CheckInOutScreen",
            onClick = { onNavigate("CheckInOutScreen") }
        )

        DrawerItem(
            label = stringResource(R.string.time_off),
            icon = Icons.Default.BeachAccess,
            selected = currentRoute == "TimeOffScreen",
            onClick = { onNavigate("TimeOffScreen") }
        )

        DrawerItem(
            label = stringResource(R.string.AttendanceHistory),
            icon = Icons.Default.AccessTime,
            selected = currentRoute == "AttendanceScreen",
            onClick = { onNavigate("AttendanceScreen") }
        )

        DrawerItem(
            label = stringResource(R.string.deduction),
            icon = Icons.Default.HourglassBottom,
            selected = currentRoute == "DeductionScreen",
            onClick = { onNavigate("DeductionScreen") }
        )

        DrawerItem(
            label = stringResource(R.string.expenses),
            icon = Icons.Default.Paid,
            selected = currentRoute == "ExpensesScreen",
            onClick = { onNavigate("ExpensesScreen") }
        )

        DrawerItem(
            label = stringResource(R.string.lunch),
            icon = Icons.Default.LunchDining,
            selected = currentRoute == "LunchScreen",
            onClick = { onNavigate("LunchScreen") }
        )

        DrawerItem(
            label = stringResource(R.string.notification),
            icon = Icons.Default.Notifications,
            selected = currentRoute == "NotificationsScreen",
            onClick = { onNavigate("NotificationsScreen") }
        )

        DrawerItem(
            label = stringResource(R.string.settings),
            icon = Icons.Default.Settings,
            selected = currentRoute == "SettingsScreen",
            onClick = { onNavigate("SettingsScreen") }
        )
    }
}