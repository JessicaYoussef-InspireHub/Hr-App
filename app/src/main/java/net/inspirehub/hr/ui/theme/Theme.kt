package net.inspirehub.hr.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.LightGray


private val LightColorScheme = lightColorScheme(
    tertiary = Light_Main_color,
    onBackground = Black,
    onSecondary = White,
    onSecondaryContainer = Black,
    onTertiaryContainer = White,
    surfaceVariant = lightWeekendAndPublicColor,
    inverseOnSurface = LightGray,
    surface = LightGray,
    surfaceTint = Medium_grey,
    surfaceDim = lightBottomSheetGray,
    error = Red,
    onSurface = Gray,
    surfaceContainerHigh = Light_Lunch_card,
    inversePrimary = Light_green,
    scrim = Dark_green,
    onErrorContainer = Dark_yellow,
    errorContainer = Light_yellow,
    surfaceContainer = PresentColor,
    surfaceContainerHighest = AbsentColor,
    surfaceContainerLow = LateColor,
    outline = AlertPermission,
    onSurfaceVariant = lightBottomSheetGray,
    surfaceBright = lightWeekendAndPublicColor,
    inverseSurface = LightGray,
    surfaceContainerLowest = LightGray,
    onError = workHoursColor


)

private val DarkColorScheme = darkColorScheme(
    tertiary = Dark_Main_color,
    onBackground = White,
    onSecondary = Black,
    onSecondaryContainer = Black,
    onTertiaryContainer = White,
    surfaceVariant = darkWeekendAndPublicColor,
    inverseOnSurface = LightGray,
    surface = Dark_grey,
    surfaceTint = Dark_grey,
    surfaceDim = Dark_grey,
    error = Red,
    onSurface = Gray,
    surfaceContainerHigh = Dark_Lunch_card,
    inversePrimary = Light_green,
    scrim = Dark_green,
    onErrorContainer = Dark_yellow,
    errorContainer = Light_yellow,
    surfaceContainer = PresentColor,
    surfaceContainerHighest = AbsentColor,
    surfaceContainerLow = LateColor,
    outline = AlertPermission,
    onSurfaceVariant = White,
    surfaceContainerLowest = White,
    surfaceBright = Dark_Lunch_card,
    inverseSurface = darkWeekendAndPublicColor,
    onError = workHoursColor

)

@Composable
fun HrTheme(
    darkMode: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkMode) DarkColorScheme else LightColorScheme


    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}