package net.inspirehub.hr.drawer

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.launch
import net.inspirehub.hr.appColors

@Composable
fun NavigationDrawer(
    drawerContent: @Composable ColumnScope.(closeDrawer: () -> Unit) -> Unit,
    content: @Composable (onMenuClick: () -> Unit) -> Unit
) {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )

      val scope = rememberCoroutineScope()
      val color = appColors()

    // The menu icon is in the app bar actions (end side), so flip the layout
    // direction to make the drawer open from the end, then restore it for the content.
    val layoutDirection = LocalLayoutDirection.current
    val flippedDirection =
        if (layoutDirection == LayoutDirection.Ltr) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides flippedDirection) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    drawerContainerColor = color.onSecondaryColor,
                    windowInsets = WindowInsets.navigationBars
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                        drawerContent {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    }
                }
            }
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                content {
                    scope.launch {
                        drawerState.open()
                    }
                }
            }
        }
    }
}