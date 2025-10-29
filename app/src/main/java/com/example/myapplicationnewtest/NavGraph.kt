package com.example.myapplicationnewtest

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplicationnewtest.check_in_out.presentation.CheckInOutScreen
import com.example.myapplicationnewtest.notifications.presentation.NotificationsScreen
import com.example.myapplicationnewtest.protection.presentation.ConfirmPinScreen
import com.example.myapplicationnewtest.protection.presentation.EnterPinScreen
import com.example.myapplicationnewtest.protection.presentation.FingerPrintScreen
import com.example.myapplicationnewtest.protection.presentation.PinCodeScreen
import com.example.myapplicationnewtest.protection.presentation.ProtectionScreen
import com.example.myapplicationnewtest.scan_qr_code.data.ScanQrCodeViewModel
import com.example.myapplicationnewtest.scan_qr_code.presentation.ScanQrCodeScreen
import com.example.myapplicationnewtest.settings.presentation.SettingsScreen
import com.example.myapplicationnewtest.sign_in.presentation.SignInScreen
import com.example.myapplicationnewtest.splash.presentation.SplashScreen
import com.example.myapplicationnewtest.time_off.presentation.TimeOffScreen


@SuppressLint("NewApi")
@Composable
fun MyAppNavHost(viewModel: ScanQrCodeViewModel, navController: NavHostController) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val savedPin = sharedPrefManager.getPin()
    val fingerprintSuccess = sharedPrefManager.isFingerprintAuthSuccess()
    val protectionSkipped = sharedPrefManager.isProtectionSkipped()
    val token = sharedPrefManager.getToken()
    val apiKey = sharedPrefManager.getApiKey()
    val companyId = sharedPrefManager.getCompanyId()

//    val nextDestination = when {
//        !token.isNullOrEmpty() && fingerprintSuccess -> "FingerPrintScreen"
//        !token.isNullOrEmpty() && !savedPin.isNullOrEmpty() -> "EnterPinScreen"
//        protectionSkipped -> "CheckInOutScreen"
////      !token.isNullOrEmpty() -> "SignInScreen/$companyId/$apiKey"
//        token.isNullOrEmpty() -> "SignInScreen/Com0001/HKP0Pt4zTDVf3ZHcGNmM4yx6"
//        else ->"ScanQrCodeScreen"
//    }

    val nextDestination = when {
        // ✅ Has token and fingerprint enabled
        !token.isNullOrEmpty() && fingerprintSuccess -> "FingerPrintScreen"

        // ✅ Has token and PIN enabled
        !token.isNullOrEmpty() && !savedPin.isNullOrEmpty() -> "EnterPinScreen"

        // ✅ Has token and skipped protection
        !token.isNullOrEmpty() && protectionSkipped -> "CheckInOutScreen"

        // ✅ Has companyId and apiKey but no token (needs to sign in)
        token.isNullOrEmpty() && !companyId.isNullOrEmpty() && !apiKey.isNullOrEmpty() -> "SignInScreen/Com0001/HKP0Pt4zTDVf3ZHcGNmM4yx6/0"

        // ✅ Has companyId and apiKey and token (needs to protection)
        !token.isNullOrEmpty() && !companyId.isNullOrEmpty() && !apiKey.isNullOrEmpty() -> "ProtectionScreen/0"

        // ✅ First time opening the app (everything is empty)
        token.isNullOrEmpty() && companyId.isNullOrEmpty() && apiKey.isNullOrEmpty() -> "ScanQrCodeScreen"

        // ✅ Fallback for any unexpected case
        else -> "ScanQrCodeScreen"
    }



    NavHost(navController = navController, startDestination = "SplashScreen") {
        composable("SplashScreen") {
            SplashScreen(
                navController = navController,
                nextDestination = nextDestination
            )
        }

        composable("ScanQrCodeScreen") { ScanQrCodeScreen(viewModel, navController) }

        composable("TimeOffScreen") {
            TimeOffScreen(
                navController = navController
            )
        }

        composable("SettingsScreen") {
            SettingsScreen(
                navController = navController
            )
        }

        composable("NotificationsScreen") {
            NotificationsScreen(
                navController = navController
            )
        }

        composable(
            route = "EnterPinScreen?changeMethod={changeMethod}",
            arguments = listOf(
                navArgument("changeMethod") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isChangingMethod = backStackEntry.arguments?.getBoolean("changeMethod") ?: false
            EnterPinScreen(navController = navController, isChangingMethod = isChangingMethod)
        }




        composable(
            "confirm_pin/{pin}"
        ) { backStackEntry ->
            val pin = backStackEntry.arguments?.getString("pin") ?: ""

            ConfirmPinScreen(
                navController = navController,
                pin = pin,
            )
        }


        composable(
            route = "PinCodeScreen",
        ) {
            PinCodeScreen(
                navController = navController,
            )
        }


        composable(
            route = "FingerPrintScreen?changeMethod={changeMethod}",
            arguments = listOf(
                navArgument("changeMethod") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isChangingMethod = backStackEntry.arguments?.getBoolean("changeMethod") ?: false
            FingerPrintScreen(
                navController = navController,
                isChangingMethod = isChangingMethod
            )
        }



        composable(
            route = "ProtectionScreen/{numberToBack}",
        )  { backStackEntry ->
            val numberToBack = backStackEntry.arguments?.getString("numberToBack")?.toIntOrNull() ?: 0

            ProtectionScreen(
                navController = navController,
                numberToBack = numberToBack
            )
        }

        composable("CheckInOutScreen") {
            CheckInOutScreen(
                navController
            )
        }


        composable("SignInScreen/{companyId}/{apiKey}/{numberToBack}") { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
            val apiKey = backStackEntry.arguments?.getString("apiKey") ?: ""
            val numberToBack = backStackEntry.arguments?.getString("numberToBack")?.toIntOrNull() ?: 0

            SignInScreen(navController = navController, companyId = companyId, apiKey = apiKey , numberToBack = numberToBack)
        }
    }
}

