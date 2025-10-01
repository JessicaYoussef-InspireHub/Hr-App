package com.example.myapplicationnewtest.protection.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.biometric.BiometricManager
import androidx.compose.foundation.background
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.protection.components.ProtectionBox
import com.example.myapplicationnewtest.protection.data.ProtectionViewModel
import kotlin.system.exitProcess
import com.example.myapplicationnewtest.R


@Composable
fun ProtectionScreen(
    navController: NavController,
    viewModel: ProtectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    var notShowAgainChecked by remember { mutableStateOf(false) }
    val biometricManager = remember { BiometricManager.from(context) }
    val biometricStatus = biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG
    )

    val isFingerprintHardwareAvailable = biometricStatus != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE



    // Listen to navigation instructions
    LaunchedEffect(Unit) {
        viewModel.navigateTo.collect { route ->
            navController.navigate(route)
        }
    }

    BackHandler(enabled = true) {
        exitProcess(0)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.choose_your_protection_method),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
                fontSize = 30.sp
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isFingerprintHardwareAvailable) {
                ProtectionBox(
                    label = stringResource(R.string.use_fingerprint),
                    icon = Icons.Filled.Fingerprint,
                    onClick = {
                        viewModel.onFingerprintSelected()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            ProtectionBox(
                label = stringResource(R.string.use_pin_code),
                icon = Icons.Outlined.Lock,
                onClick = {
                    viewModel.onPinCodeSelected(
                    )
                }
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                stringResource(R.string.no_protection),
                 color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                modifier = Modifier.clickable {

                    if (notShowAgainChecked) {
                        sharedPrefManager.setProtectionSkipped(true)
                    }
                    viewModel.onSkipSelected()
                    sharedPrefManager.saveProtectionMethod(3)

                })

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Checkbox(
                    checked = notShowAgainChecked,
                    onCheckedChange = { notShowAgainChecked = it },
                    colors = CheckboxDefaults.colors(
                        uncheckedColor = MaterialTheme.colorScheme.tertiary,
                        checkedColor = MaterialTheme.colorScheme.tertiary)
                )
                Text(
                    text = stringResource(R.string.do_not_show_this_again),
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 16.sp
                )
            }
        }
    }
}
