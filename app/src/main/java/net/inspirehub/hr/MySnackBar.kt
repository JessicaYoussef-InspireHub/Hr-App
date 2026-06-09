package net.inspirehub.hr

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.lunch.data.CustomSnackBarVisuals

@Composable
fun MySnackBar(
    snackBarData: SnackbarData,
    onViewCart: (() -> Unit)? = null,
    useOffset: Boolean
) {
    val colors = appColors()
    val custom = snackBarData.visuals as? CustomSnackBarVisuals
    val modifier = if (useOffset) {
        Modifier.padding(bottom = 10.dp)
    } else {
        Modifier.offset(y = 12.dp)
    }

    Snackbar(
        action = {
            if (custom?.showViewCart == true && onViewCart != null) {
                Text(
                    text = "View Cart",
                    modifier = Modifier.clickable {
                        onViewCart()
                        snackBarData.dismiss()
                    },
                    color = colors.tertiaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        containerColor = colors.onSecondaryColor,
        contentColor = colors.tertiaryColor,
        modifier = modifier
            .padding(horizontal = 10.dp)
            .border(
                width = 2.dp,
                color = colors.tertiaryColor,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Text(
            text = snackBarData.visuals.message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}