package net.inspirehub.hr

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import net.inspirehub.hr.check_in_out.presentation.CheckInOutScreen
import net.inspirehub.hr.lunch.presentation.LunchScreen
import net.inspirehub.hr.lunch.presentation.OrderScreen
import net.inspirehub.hr.notifications.presentation.NotificationsScreen
import net.inspirehub.hr.protection.presentation.ConfirmPinScreen
import net.inspirehub.hr.protection.presentation.EnterPinScreen
import net.inspirehub.hr.protection.presentation.FingerPrintScreen
import net.inspirehub.hr.protection.presentation.PinCodeScreen
import net.inspirehub.hr.protection.presentation.ProtectionScreen
import net.inspirehub.hr.scan_qr_code.data.ScanQrCodeViewModel
import net.inspirehub.hr.scan_qr_code.presentation.ScanQrCodeScreen
import net.inspirehub.hr.settings.presentation.SettingsScreen
import net.inspirehub.hr.sign_in.presentation.SignInScreen
import net.inspirehub.hr.splash.presentation.SplashScreen
import net.inspirehub.hr.time_off.presentation.TimeOffScreen


@SuppressLint("NewApi")
@Composable
fun MyAppNavHost(
    viewModel: ScanQrCodeViewModel,
    navController: NavHostController,
    openedFromNotification: Boolean = false
    ) {
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

        openedFromNotification -> "NotificationsScreen"

        // ✅ Has token and fingerprint enabled
        !token.isNullOrEmpty() && fingerprintSuccess -> "FingerPrintScreen"

        // ✅ Has token and PIN enabled
        !token.isNullOrEmpty() && !savedPin.isNullOrEmpty() -> "EnterPinScreen"

        // ✅ Has token and skipped protection
        !token.isNullOrEmpty() && protectionSkipped -> "CheckInOutScreen"

        // ✅ Has companyId and apiKey but no token (needs to sign in)
        token.isNullOrEmpty() && !companyId.isNullOrEmpty() && !apiKey.isNullOrEmpty() -> "SignInScreen/$companyId/$apiKey"

        // ✅ Has companyId and apiKey and token (needs to protection)
        !token.isNullOrEmpty() && !companyId.isNullOrEmpty() && !apiKey.isNullOrEmpty() -> "ProtectionScreen/0"

        // ✅ First time opening the app (everything is empty)
        token.isNullOrEmpty() && companyId.isNullOrEmpty() && apiKey.isNullOrEmpty() -> "ScanQrCodeScreen"

        // ✅ Fallback for any unexpected case
        else -> "ScanQrCodeScreen"
    }


//    LaunchedEffect(notificationDestination) {
//        if (notificationDestination == "NotificationsScreen") {
//            navController.navigate("NotificationsScreen") {
//                popUpTo("SplashScreen") { inclusive = true }
//                launchSingleTop = true
//            }
//        }
//    }

    val startDestination = if (openedFromNotification) {
        "NotificationsScreen"
    } else {
        "SplashScreen"
    }





    NavHost(navController = navController,
        startDestination = startDestination) {
        composable("SplashScreen") {
            SplashScreen(
                navController = navController,
                nextDestination = nextDestination
            )
        }

        composable("ScanQrCodeScreen") { ScanQrCodeScreen(viewModel, navController) }

        composable("LunchScreen") {
            LunchScreen(
                navController = navController
            )
        }

        composable("OrderScreen") { OrderScreen(
            navController = navController
        ) }

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


        composable("SignInScreen/{companyId}/{apiKey}") { backStackEntry ->
            val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
            val apiKey = backStackEntry.arguments?.getString("apiKey") ?: ""
            val numberToBack = backStackEntry.arguments?.getString("numberToBack")?.toIntOrNull() ?: 0

            SignInScreen(navController = navController, companyId = companyId, apiKey = apiKey )
        }
    }
}

