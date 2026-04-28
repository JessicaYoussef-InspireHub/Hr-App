package net.inspirehub.hr.lunch.data

import androidx.compose.material3.SnackbarVisuals

class CustomSnackBarVisuals(
    override val message: String,
    val showViewCart: Boolean = false
) : SnackbarVisuals {
    override val actionLabel: String? = null
    override val withDismissAction: Boolean = false
    override val duration = androidx.compose.material3.SnackbarDuration.Short
}