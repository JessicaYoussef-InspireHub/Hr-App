package net.inspirehub.hr.bottom_app_bar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun BottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val colors = appColors()
    var moreMenuExpanded by remember { mutableStateOf(false) }

    val moreRoutes = listOf(
        "AttendanceScreen",
        "ExpensesScreen",
        "LunchScreen",
        "DeductionScreen",
        "SettingsScreen"
    )

    val isMoreSelected = currentRoute in moreRoutes

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

                // More
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
                                Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.more)
                            )
                        },
                        label = stringResource(R.string.more),
                        selected = isMoreSelected
                    ) {
                        moreMenuExpanded = true
                    }
                }
            }


           // More Menu Popup
            if (moreMenuExpanded) {

                val density = LocalDensity.current

                val dividerOffsetPx = with(density) { 5.dp.roundToPx() }

                val horizontalMarginPx = with(density) { 5.dp.roundToPx() }

                val menuPositionProvider = remember(density) {

                    object : PopupPositionProvider {

                        override fun calculatePosition(
                            anchorBounds: IntRect,
                            windowSize: IntSize,
                            layoutDirection: LayoutDirection,
                            popupContentSize: IntSize
                        ): IntOffset {

                            // Align the menu to the right edge in LTR
                            // and to the left edge in RTL.

                            val x = if (layoutDirection == LayoutDirection.Ltr) {
                                anchorBounds.right -
                                        popupContentSize.width -
                                        horizontalMarginPx
                            } else {
                                anchorBounds.left + horizontalMarginPx
                            }

                            // Place the bottom edge of the menu
                            // exactly at the Divider position.

                            val y = anchorBounds.top -
                                    popupContentSize.height +
                                    dividerOffsetPx

                            return IntOffset(
                                x = x,
                                y = y
                            )
                        }
                    }
                }

                Popup(
                    popupPositionProvider = menuPositionProvider,
                    onDismissRequest = { moreMenuExpanded = false },
                    properties = PopupProperties(focusable = true)
                ) {

                    Surface(
                        modifier = Modifier.width(180.dp),
                        color = colors.surfaceContainerHigh,
                        shape = RoundedCornerShape(4.dp)
                    ) {

                        Column(
                            modifier = Modifier.widthIn(min = 180.dp)
                        ) {

                            // Attendance
                            MoreMenuItem(
                                icon = {
                                    Icon(
                                        Icons.Default.AccessTime,
                                        contentDescription = stringResource(R.string.AttendanceHistory)
                                    )
                                },
                                title = stringResource(R.string.AttendanceHistory),
                                selected = currentRoute == "AttendanceScreen"
                            ) {
                                moreMenuExpanded = false
                                navigateTo(navController, "AttendanceScreen")
                            }

                            // Deduction
                            MoreMenuItem(
                                icon = {
                                    Icon(
                                        Icons.Default.HourglassBottom,
                                        contentDescription = stringResource(R.string.deduction)
                                    )
                                },
                                title = stringResource(R.string.deduction),
                                selected = currentRoute == "DeductionScreen"
                            ) {
                                moreMenuExpanded = false
                                navigateTo(navController, "DeductionScreen")
                            }

                            // Expenses
                            MoreMenuItem(
                                icon = {
                                    Icon(
                                        Icons.Default.Paid,
                                        contentDescription = stringResource(R.string.expenses)
                                    )
                                },
                                title = stringResource(R.string.expenses),
                                selected = currentRoute == "ExpensesScreen"
                            ) {
                                moreMenuExpanded = false
                                navigateTo(navController, "ExpensesScreen")
                            }

                            // Lunch
                            MoreMenuItem(
                                icon = {
                                    Icon(
                                        Icons.Default.LunchDining,
                                        contentDescription = stringResource(R.string.lunch)
                                    )
                                },
                                title = stringResource(R.string.lunch),
                                selected = currentRoute == "LunchScreen"
                            ) {
                                moreMenuExpanded = false
                                navigateTo(navController, "LunchScreen")
                            }

                            // Settings
                            MoreMenuItem(
                                icon = {
                                    Icon(
                                        Icons.Default.Settings,
                                        contentDescription = stringResource(R.string.settings)
                                    )
                                },
                                title = stringResource(R.string.settings),
                                selected = currentRoute == "SettingsScreen"
                            ) {
                                moreMenuExpanded = false
                                navigateTo(navController, "SettingsScreen")
                            }
                        }
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