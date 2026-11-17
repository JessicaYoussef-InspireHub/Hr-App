package net.inspirehub.hr.drawer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.settings.data.SettingsViewModel

@Composable
fun DrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit,
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {

    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HeaderDrawer(
            onClose = onClose
        )

        ProfileDrawer(
            navController = navController,
            onClose = onClose
        )

        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            MenuItemsDrawer(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        }

        FooterDrawer(
            onLogoutClick = {
                showLogoutDialog = true
            }
        )


        if (showLogoutDialog) {
            MyDialog(
                onConfirm = {
                    showLogoutDialog = false
                    onClose()
                    viewModel.logout()
                    val companyId = sharedPref.getCompanyId() ?: ""
                    val apiKey = sharedPref.getApiKey() ?: ""
                    sharedPref.setProtectionSkipped(false)
                    navController.navigate("SignInScreen/$companyId/$apiKey") {
                        popUpTo(0) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onDismiss = { showLogoutDialog = false },
                title = stringResource(R.string.logout),
                subtitle = stringResource(R.string.are_you_sure_you_want_to_log_out),
                confirmButtonText = stringResource(R.string.ok),
                dismissButtonText = stringResource(R.string.cancel)
            )
        }
    }
}