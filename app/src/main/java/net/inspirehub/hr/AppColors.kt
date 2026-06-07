package net.inspirehub.hr


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
data class AppColors(
    val tertiaryColor: Color,
    val onBackgroundColor: Color,
    val onSecondaryColor: Color,
    val surfaceVariant: Color,
    val inverseOnSurface: Color,
    val surfaceColor : Color,
    val error : Color,
    val transparent: Color,
    val surfaceContainerHigh : Color,
    val inversePrimary: Color,
    val scrim: Color,
    val onErrorContainer : Color,
    val errorContainer : Color,
    val onSecondaryContainer : Color,
    val surfaceDim : Color,
    val onTertiaryContainer : Color,
    val surfaceTint : Color

)

@Composable
fun appColors(): AppColors {
    return AppColors(
        tertiaryColor = MaterialTheme.colorScheme.tertiary,
        onSecondaryColor = MaterialTheme.colorScheme.onSecondary,
        inverseOnSurface = MaterialTheme.colorScheme.inverseOnSurface,
        onBackgroundColor = MaterialTheme.colorScheme.onBackground,
        transparent = Color.Transparent,
        surfaceVariant = MaterialTheme.colorScheme.surfaceVariant,
        surfaceColor = MaterialTheme.colorScheme.surface,
        error = MaterialTheme.colorScheme.error,
        surfaceContainerHigh = MaterialTheme.colorScheme.surfaceContainerHigh,
        inversePrimary = MaterialTheme.colorScheme.inversePrimary,
        scrim = MaterialTheme.colorScheme.scrim,
        onErrorContainer = MaterialTheme.colorScheme.onErrorContainer,
        errorContainer = MaterialTheme.colorScheme.errorContainer,
        onSecondaryContainer = MaterialTheme.colorScheme.onSecondaryContainer,
        surfaceDim = MaterialTheme.colorScheme.surfaceDim,
        onTertiaryContainer = MaterialTheme.colorScheme.onTertiaryContainer,
        surfaceTint = MaterialTheme.colorScheme.surfaceTint
    )
}
