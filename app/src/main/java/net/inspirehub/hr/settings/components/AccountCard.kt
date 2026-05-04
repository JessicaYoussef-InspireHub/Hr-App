package net.inspirehub.hr.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.settings.data.SettingsViewModel


@Composable
fun AccountCard(
    navController: NavController,
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    var showDialog by remember { mutableStateOf(false) }


    Column {
        Text(
            stringResource(R.string.account),
            color = colors.tertiaryColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        2.dp,
                        colors.inverseOnSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = colors.onSecondaryColor
            )
        ){
            Column {
                SettingsItem(
                    stringResource(R.string.logout),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        showDialog = true
                    }
                )

                if (showDialog) {
                    ConfirmDialog(
                        message = stringResource(R.string.logout),
                        confirmText = stringResource(R.string.are_you_sure_you_want_to_log_out),
                        onConfirm = {
                            showDialog = false
                            viewModel.logout()
                            val companyId = sharedPref.getCompanyId() ?: ""
                            val apiKey = sharedPref.getApiKey() ?: ""
                            sharedPref.setProtectionSkipped(false)
                            navController.navigate("SignInScreen/$companyId/$apiKey")
                        },
                        onDismiss = { showDialog = false }
                    )
                }
            }
        }
    }
}