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
    )
}
