package com.example.myapplicationnewtest.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.appColors


@Composable
fun SecurityCard(
    navController: NavController,
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val protectionMethod = sharedPrefManager.getProtectionMethod()
    var showDialog by remember { mutableStateOf(false) }


    Column {
        Text(
            stringResource(R.string.security),
            color = colors.tertiaryColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
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
        ) {
            Column {
                SettingsItem(
                    label = stringResource(R.string.change_protection_method),
                    icon = Icons.Default.Lock,
                    onClick = {
                        showDialog = true
                    }
                )

                if (showDialog) {
                    ConfirmDialog(
                        message = stringResource(R.string.change_protection_method),
                        confirmText = stringResource(R.string.are_you_sure_you_want_to_change_protection_method),
                        onConfirm = {
                            showDialog = false
                            when (protectionMethod) {
                                1 -> navController.navigate("FingerPrintScreen?changeMethod=true")
                                2 -> navController.navigate("EnterPinScreen?changeMethod=true")
                                3 -> navController.navigate("ProtectionScreen/1")
                                else -> navController.navigate("ProtectionScreen/1")
                            }
                        },
                        onDismiss = { showDialog = false }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    color = colors.surfaceColor
                )

                SettingsItem(
                    stringResource(R.string.notification),
                    icon = Icons.Default.Notifications,
                    onClick = {
                        navController.navigate("NotificationsScreen")
                    }
                )
            }
        }
    }
}