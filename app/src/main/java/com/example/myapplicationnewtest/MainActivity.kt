package com.example.myapplicationnewtest

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.myapplicationnewtest.scan_qr_code.data.ScanQrCodeViewModel
import com.example.myapplicationnewtest.ui.theme.LocalDarkMode
import com.example.myapplicationnewtest.ui.theme.MyApplicationnewTestTheme
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
//        enableEdgeToEdge()
        val sharedPref = SharedPrefManager(this)
        val lang = sharedPref.getLanguage()
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)


        setContent {
            val context = LocalContext.current
            val sharedPrefManager = SharedPrefManager(context)
            val darkModeState = rememberSaveable { mutableStateOf(sharedPrefManager.isDarkModeEnabled()) }

            CompositionLocalProvider(
                LocalDarkMode provides darkModeState
            ) {
                MyApplicationnewTestTheme {
                    val viewModel: ScanQrCodeViewModel = viewModel()
                    val navController = rememberNavController()

                    MyAppNavHost(viewModel, navController)

                    //                QRCodeScreen()
                }
            }
        }
    }
}






