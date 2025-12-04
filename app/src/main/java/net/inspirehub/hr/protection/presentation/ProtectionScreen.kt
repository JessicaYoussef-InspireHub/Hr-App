package net.inspirehub.hr.protection.presentation

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.protection.components.ProtectionBox
import net.inspirehub.hr.protection.data.ProtectionViewModel
import kotlin.system.exitProcess
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors


@Composable
fun ProtectionScreen(
    navController: NavController,
    viewModel: ProtectionViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    numberToBack: Int = 0
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    var notShowAgainChecked by remember { mutableStateOf(false) }
    val biometricManager = remember { BiometricManager.from(context) }
    val biometricStatus = biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG
    )

    val isFingerprintHardwareAvailable =
        biometricStatus != BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE


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
            .background(colors.onSecondaryColor)
            .padding(16.dp)
        .verticalScroll(rememberScrollState())

    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (numberToBack == 1) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(32.dp)
                            .clickable {
                                navController.popBackStack()
                            },
                        tint = colors.tertiaryColor
                    )
                }
            }
            Text(
                stringResource(R.string.choose_your_protection_method),
                fontWeight = FontWeight.Bold,
                color = colors.tertiaryColor,
                textAlign = TextAlign.Center,
                fontSize = 20.sp
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
                color = colors.tertiaryColor,
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
                        checkmarkColor = colors.onSecondaryColor,
                        uncheckedColor = colors.tertiaryColor,
                        checkedColor = colors.tertiaryColor,
                    )
                )
                Text(
                    text = stringResource(R.string.do_not_show_this_again),
                    color = colors.tertiaryColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}
