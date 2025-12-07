package net.inspirehub.hr.protection.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.protection.data.FingerprintViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.system.exitProcess


@SuppressLint("InlinedApi")
@Composable
fun FingerPrintScreen(
    navController: NavController,
    isChangingMethod: Boolean = false,
    viewModel: FingerprintViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val colors = appColors()
    val titleText = if (isChangingMethod)
        stringResource(R.string.confirm_fingerprint_to_change_protection)
    else
        stringResource(R.string.click_to_register_the_fingerprint)

    val context = LocalContext.current
    val activity = context.findActivity()
    val executor = remember { Executors.newSingleThreadExecutor() }
    val authStatus by viewModel.authStatus
    val authSuccess by viewModel.authSuccess
    val prefManager = remember { SharedPrefManager(context) }


    LaunchedEffect(Unit) {
        if (activity != null) {
            startBiometricAuth(
                activity = activity,
                executor = executor,
                context = context,
                onResult = { success ->
                    println("🎯 Authentication result: $success")
                    viewModel.onAuthenticationResult(success)
                    if (success) {
                        SharedPrefManager(context).setFingerprintAuthSuccess(true)
                    }
                }
            )
        }
    }


    LaunchedEffect(authSuccess) {
        if (authSuccess) {
            prefManager.saveProtectionMethod(1)
            val destination = if (isChangingMethod) {
                prefManager.setProtectionSkipped(false)
                prefManager.setFingerprintAuthSuccess(false)
                "ProtectionScreen/0"
            } else {
                "CheckInOutScreen"
            }

            navController.navigate(destination) {
                popUpTo("FingerPrintScreen") { inclusive = true }
            }
        }
    }

    val biometricManager = remember { BiometricManager.from(context) }
    val biometricStatus =
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
    println("🔍 Current fingerprint status: $biometricStatus ")

    when (biometricStatus) {
        BiometricManager.BIOMETRIC_SUCCESS -> println("✅ The device is fingerprint ready")
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> println("⚠️ No fingerprint registered")
        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> println("⚠️ No fingerprint sensor")
        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> println("⚠️Fingerprint sensor is not available")
        else -> println("⚠️ Unknown status: $biometricStatus")
    }

    val authStatusText = when (authStatus) {
        "auth_success" -> stringResource(R.string.auth_success)
        "auth_failed" -> stringResource(R.string.auth_failed)
        "unable_open_settings" -> stringResource(R.string.unable_open_settings)
        else -> ""
    }

    val errorMessage = when (biometricStatus) {
        BiometricManager.BIOMETRIC_SUCCESS -> null
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
            stringResource(R.string.click_here_to_go_to_settings)

        BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
            stringResource(R.string.your_device_does_not_have_a_fingerprint_sensor)

        BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
         stringResource(R.string.fingerprint_is_currently_unavailable_try_again_later)

        else -> stringResource(R.string.fingerprint_is_not_supported_on_this_device)
    }

    BackHandler(enabled = true) {
        if (isChangingMethod) {
            navController.navigate("ProtectionScreen/0") {
                popUpTo("FingerPrintScreen") { inclusive = true }
            }
        } else {
            exitProcess(0)
        }
    }
//    Box(
//        modifier = Modifier
//            .background(colors.onSecondaryColor)
//            .fillMaxSize()
//            .verticalScroll(rememberScrollState())
//        ){

        Column (
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(colors.onSecondaryColor)
                .padding(horizontal = 5.dp)
                .padding(WindowInsets.navigationBars.asPaddingValues())
                .padding(WindowInsets.statusBars.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { navController.popBackStack() },
                    tint = colors.tertiaryColor
                )
                Spacer(modifier = Modifier.weight(0.3f))
            if( errorMessage == null){
                Text(
                    text = titleText,
                    fontWeight = FontWeight.Bold,
                    color = colors.tertiaryColor,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.weight(1f))
            }}

         Spacer(modifier = Modifier.weight(0.5f))

         when {
            errorMessage == null -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Fingerprint Icon",
                        modifier = Modifier
                            .size(200.dp)
                            .clickable {
                                println("👆 The fingerprint icon has been pressed.")
                                if (activity != null) {
                                    startBiometricAuth(
                                        activity = activity,
                                        executor = executor,
                                        context = context,
                                        onResult = { success ->
                                            println("🎯 Authentication result: $success")
                                            viewModel.onAuthenticationResult(
                                                success,
                                            )
                                            if (success) {
                                                SharedPrefManager(context).setFingerprintAuthSuccess(
                                                    true
                                                )
                                            }
                                        }
                                    )
                                }
                            },
                        tint = colors.tertiaryColor
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        authStatusText,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.tertiaryColor,
                        fontSize = 20.sp
                    )
                }
            }

            biometricStatus == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Text(
                    text = errorMessage ?: "",
                    fontWeight = FontWeight.Normal,
                    color = colors.tertiaryColor,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            try {
                                context.startActivity(
                                    Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        putExtra(
                                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                            BiometricManager.Authenticators.BIOMETRIC_STRONG
                                        )
                                    }
                                )
                            } catch (e: Exception) {
                                viewModel.setError("unable_open_settings")
                                println("❌ ${e.message}")
                            }
                        }
                )
            }
            else -> {
                Text(
                    text = errorMessage ?: "",
                    color = colors.tertiaryColor)
            }

         }
            Spacer(modifier = Modifier.weight(1f))


        }
}

fun startBiometricAuth(
    activity: Activity,
    context: Context,
    executor: Executor,
    onResult: (Boolean) -> Unit
) {
    println("🚀 Start startBiometricAuth")

    val biometricManager = BiometricManager.from(activity)
    val canAuthenticate =
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)

    if (canAuthenticate != BiometricManager.BIOMETRIC_SUCCESS) {
        println("❌ Fingerprint cannot be used. Authentication status: $canAuthenticate")
        onResult(false)
        return
    }

    println("✅ The device supports fingerprint - let's get started BiometricPrompt")

    try {
        // First try to cast to FragmentActivity (the original supported type)
        val fragmentActivity = activity as? FragmentActivity
            ?: throw IllegalStateException("Activity must be FragmentActivity or ComponentActivity")

        val biometricPrompt = BiometricPrompt(
            fragmentActivity,
            executor,
            createAuthCallback(onResult)
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.fingerprint_authentication))
            .setSubtitle(context.getString(R.string.please_confirm_your_identity_with_fingerprint_or_pin_code))
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    } catch (e: Exception) {
        println("❌ Error running BiometricPrompt: ${e.message}")
        onResult(false)
    }
}

private fun createAuthCallback(onResult: (Boolean) -> Unit): BiometricPrompt.AuthenticationCallback {
    return object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            println("✅ Fingerprint authentication successful")
            onResult(true)
        }

        override fun onAuthenticationFailed() {
            println("❌ Authentication Failed")
            onResult(false)
        }

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            println("❌ Fingerprint error: $errString (code $errorCode)")
            onResult(false)
        }
    }
}

// Extension function to find Activity from Context
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}






