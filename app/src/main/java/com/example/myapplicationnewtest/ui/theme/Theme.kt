package com.example.myapplicationnewtest.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import com.example.myapplicationnewtest.SharedPrefManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color.Companion.LightGray

val LocalDarkMode = staticCompositionLocalOf { mutableStateOf(false) }


private val DarkColorScheme = darkColorScheme(
    tertiary = Main_color,
    onBackground = White,
    onSecondary = Black,
    surfaceVariant = weekendAndPublicColor,
    inverseOnSurface = LightGray,
    surface = Dark_grey,


//    secondary = Validate_color,
//    onPrimary = dark_background_color,
//    onTertiary = Close_color,
//    onPrimaryContainer = Delete_color,
//    onSecondaryContainer = Discard_color,
//    onTertiaryContainer = Disabled_color,
//    surface = Dark_grey,
//    onSurface = Light_green,
//    onSurfaceVariant = Lines_color,
//    error = Red,
)

private val LightColorScheme = lightColorScheme(
    tertiary = Main_color,
    onBackground = White,
    onSecondary = Black,
    surfaceVariant = weekendAndPublicColor,
    inverseOnSurface = LightGray,
    surface = Dark_grey,
//    secondary = Validate_color,
//    tertiary = Main_color,
//    onPrimary = White,
//    onSecondary = Black,
//    onTertiary = Close_color,
//    onPrimaryContainer = Delete_color,
//    onSecondaryContainer = Discard_color,
//    onTertiaryContainer = Disabled_color,
//
//    onSurface = Light_green,
//    onSurfaceVariant = Lines_color,
//    error = Red,


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    secondary = PurpleGrey40,
    tertiary = Pink40
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MyApplicationnewTestTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val isDarkMode = sharedPrefManager.isDarkModeEnabled()
    val darkModeState = LocalDarkMode.current

    val colorScheme = if (darkModeState.value) DarkColorScheme else LightColorScheme

//    val colorScheme = when {
////        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
////            val context = LocalContext.current
////            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
////        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }



    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}