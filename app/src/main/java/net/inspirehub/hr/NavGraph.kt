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
import net.inspirehub.hr.attendance.presentation.AttendanceScreen
import net.inspirehub.hr.check_in_out.presentation.CheckInOutScreen
import net.inspirehub.hr.deduction.presentation.DeductionScreen
import net.inspirehub.hr.expenses.presentation.AddExpensesScreen
import net.inspirehub.hr.expenses.presentation.CreateReportScreen
import net.inspirehub.hr.expenses.presentation.EditExpenseScreen
import net.inspirehub.hr.expenses.presentation.EditReportScreen
import net.inspirehub.hr.expenses.presentation.ExpensesScreen
import net.inspirehub.hr.expenses.presentation.MyReportScreen
import net.inspirehub.hr.lunch.presentation.LunchScreen
import net.inspirehub.hr.notifications.presentation.NotificationsScreen
import net.inspirehub.hr.protection.presentation.ConfirmPinScreen
import net.inspirehub.hr.protection.presentation.EnterPinScreen
import net.inspirehub.hr.protection.presentation.FingerPrintScreen
import net.inspirehub.hr.protection.presentation.PinCodeScreen
import net.inspirehub.hr.protection.presentation.ProtectionScreen
import net.inspirehub.hr.scan_qr_code.data.ScanQrCodeViewModel
import net.inspirehub.hr.scan_qr_code.presentation.ScanQrCodeScreen
import net.inspirehub.hr.settings.presentation.SettingsScreen
import net.inspirehub.hr.sign_in.data.SessionExpired
import net.inspirehub.hr.sign_in.presentation.SignInScreen
import net.inspirehub.hr.splash.presentation.SplashScreen
import net.inspirehub.hr.time_off.presentation.TimeOffScreen
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import net.inspirehub.hr.deduction.presentation.WizardDeductionScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import net.inspirehub.hr.drawer.DrawerContent
import net.inspirehub.hr.drawer.NavigationDrawer
import net.inspirehub.hr.profile.presentation.ProfileScreen
import net.inspirehub.hr.profile.presentation.WizardProfileScreen
import net.inspirehub.hr.settings.presentation.WizardSaudiLanguageScreen

@SuppressLint("NewApi")
@Composable
fun MyAppNavHost(
    viewModel: ScanQrCodeViewModel,
    navController: NavHostController,
    openedFromNotification: Boolean = false
) {
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val savedPin = sharedPref.getPin()
    val fingerprintSuccess = sharedPref.isFingerprintAuthSuccess()
    val protectionSkipped = sharedPref.isProtectionSkipped()
    val token = sharedPref.getToken()
    val apiKey = sharedPref.getApiKey()
    val companyId = sharedPref.getCompanyId()

    val nextDestination = when {
        openedFromNotification && token.isNullOrEmpty() ->
            "SignInScreen/$companyId/$apiKey"

        openedFromNotification && !token.isNullOrEmpty() &&
                (fingerprintSuccess || !savedPin.isNullOrEmpty()) ->
            "ProtectionScreen/0"

        openedFromNotification && !token.isNullOrEmpty() ->
            "NotificationsScreen"

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

    // The server refused to renew the key: go to sign in, with nothing behind it.
    LaunchedEffect(Unit) {
        SessionExpired.events.collect {
            navController.navigate("SignInScreen/${sharedPref.getCompanyId()}/${sharedPref.getApiKey()}") {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val startDestination = "SplashScreen"
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationDrawer(
        drawerContent = { closeDrawer ->

            DrawerContent(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    closeDrawer()
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            launchSingleTop = true
                        }
                    }
                },
                navController = navController,
                onClose = closeDrawer
            )
        }
    ) { onMenuClick ->
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable("SplashScreen") {
                SplashScreen(
                    navController = navController,
                    nextDestination = nextDestination
                )
            }

            composable("DeductionScreen") {
                DeductionScreen(
                    navController = navController,
                    onMenuClick = onMenuClick
                )
            }

            composable("AttendanceScreen") {
                AttendanceScreen(
                    navController = navController,
                    onMenuClick = onMenuClick
                )
            }

            composable("ScanQrCodeScreen") { ScanQrCodeScreen(viewModel, navController) }

            composable("MyReportScreen") {
                MyReportScreen(
                    navController,
                    onMenuClick = onMenuClick
                )
            }

            composable("ProfileScreen") {
                ProfileScreen(
                    navController = navController,
                    onMenuClick = onMenuClick
                )
            }

            composable(
                route = "CreateReportScreen?type={type}",
                arguments = listOf(
                    navArgument("type") {
                        type = NavType.StringType
                        defaultValue = "company"
                    }
                )
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type") ?: "company"

                CreateReportScreen(
                    navController = navController,
                    type = type,
                    onMenuClick = onMenuClick
                )
            }

            composable(
                route = "EditReportScreen/{reportId}?expenseId={expenseId}",
                arguments = listOf(
                    navArgument("reportId") { type = NavType.IntType },
                    navArgument("expenseId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->
                val reportId = backStackEntry.arguments?.getInt("reportId")!!
                val expenseId = backStackEntry.arguments?.getInt("expenseId") ?: -1

                EditReportScreen(
                    navController = navController,
                    reportId = reportId,
                    newExpenseId = expenseId,
                    onMenuClick = onMenuClick
                )
            }

            composable("ExpensesScreen") {
                ExpensesScreen(
                    navController,
                    token = token ?: "",
                    onMenuClick = onMenuClick
                )
            }

            composable(
                route = "AddExpensesScreen?source={source}&reportId={reportId}&paymentMode={paymentMode}",
                arguments = listOf(
                    navArgument("source") {
                        type = NavType.StringType
                        defaultValue = "expenses"
                    },
                    navArgument("reportId") {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                    navArgument("paymentMode") {
                        type = NavType.StringType
                        defaultValue = "employee"
                    }
                )
            ) { backStackEntry ->

                val source = backStackEntry.arguments?.getString("source") ?: "expenses"
                val reportId = backStackEntry.arguments?.getInt("reportId") ?: -1

                AddExpensesScreen(
                    navController = navController,
                    reportId = reportId,
                    source = source,
                    onMenuClick = onMenuClick
                )
            }


            composable(
                "EditExpenseScreen/{expenseId}",
                arguments = listOf(navArgument("expenseId") { type = NavType.IntType })
            ) { backStackEntry ->
                val expenseId = backStackEntry.arguments?.getInt("expenseId") ?: 0
                EditExpenseScreen(
                    expenseId = expenseId,
                    token = token ?: "",
                    navController = navController,
                    onMenuClick = onMenuClick
                )
            }

            composable("LunchScreen") {
                LunchScreen(
                    navController = navController,
                    onMenuClick = onMenuClick
                )
            }

            composable("TimeOffScreen") {
                TimeOffScreen(
                    navController = navController,
                    onMenuClick = onMenuClick
                )
            }

            composable("SettingsScreen") {
                SettingsScreen(
                    navController = navController,
                    onMenuClick = onMenuClick
                )
            }

            composable("NotificationsScreen") {
                NotificationsScreen(
                    navController = navController,
                    onMenuClick = onMenuClick
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
            ) { backStackEntry ->
                val numberToBack =
                    backStackEntry.arguments?.getString("numberToBack")?.toIntOrNull() ?: 0

                ProtectionScreen(
                    navController = navController,
                    numberToBack = numberToBack
                )
            }

            composable("WizardSaudiLanguageScreen") {
                WizardSaudiLanguageScreen(
                    exploreSettings = {
                        navController.navigate("SettingsScreen") {
                            popUpTo("WizardSaudiLanguageScreen") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onSkip = {
                        navController.navigate("CheckInOutScreen") {
                            popUpTo("WizardSaudiLanguageScreen") {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }


            composable("CheckInOutScreen") {

                var hasSeenProfileIntro by remember { mutableStateOf(sharedPref.hasSeenProfileIntro()) }

                var hasSeenDeductionIntro by remember { mutableStateOf(sharedPref.hasSeenDeductionIntro()) }

                var hasSeenSaudiLanguageIntro by remember { mutableStateOf(sharedPref.hasSeenSaudiLanguageIntro()) }

                when {
                    // First: Show Profile Wizard
                    !hasSeenProfileIntro -> {
                        WizardProfileScreen(
                            exploreProfile = {
                                sharedPref.setProfileIntroSeen()
                                hasSeenProfileIntro = true

                                navController.navigate("ProfileScreen")
                            },
                            onSkip = {
                                sharedPref.setProfileIntroSeen()
                                hasSeenProfileIntro = true

                                navController.navigate("CheckInOutScreen") {
                                    popUpTo("CheckInOutScreen") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    // Second: Show Deduction Wizard
                    !hasSeenDeductionIntro -> {

                        WizardDeductionScreen(
                            exploreDeduction = {
                                sharedPref.setDeductionIntroSeen()
                                hasSeenDeductionIntro = true

                                navController.navigate("DeductionScreen") {
                                    popUpTo("CheckInOutScreen") {
                                        inclusive = true
                                    }
                                }
                            },
                            onSkip = {
                                sharedPref.setDeductionIntroSeen()
                                hasSeenDeductionIntro = true

                                navController.navigate("CheckInOutScreen") {
                                    popUpTo("CheckInOutScreen") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    // Third: Show Saudi Arabic Language Wizard
                    !hasSeenSaudiLanguageIntro -> {

                        WizardSaudiLanguageScreen(
                            exploreSettings = {
                                sharedPref.setSaudiLanguageIntroSeen()
                                hasSeenSaudiLanguageIntro = true

                                navController.navigate("SettingsScreen") {
                                    popUpTo("CheckInOutScreen") {
                                        inclusive = true
                                    }
                                }
                            },
                            onSkip = {
                                sharedPref.setSaudiLanguageIntroSeen()
                                hasSeenSaudiLanguageIntro = true

                                navController.navigate("CheckInOutScreen") {
                                    popUpTo("CheckInOutScreen") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    // Finally: Show Home
                    else -> {
                        CheckInOutScreen(navController)
                    }
                }
            }



            composable("SignInScreen/{companyId}/{apiKey}") { backStackEntry ->
                val companyId = backStackEntry.arguments?.getString("companyId") ?: ""
                val apiKey = backStackEntry.arguments?.getString("apiKey") ?: ""

                SignInScreen(navController = navController, companyId = companyId, apiKey = apiKey)
            }
        }
    }
}

