package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun OrderSnackBar(
    snackBarData: SnackbarData,
    onViewCart: (() -> Unit)? = null
) {
    val colors = appColors()

    Snackbar(
        action = {
            if (onViewCart != null) {
                Text(
                    text = stringResource(R.string.view_cart),
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .clickable {
                            onViewCart()
                            snackBarData.dismiss()
                        },
                    color = colors.tertiaryColor,
                    fontWeight = FontWeight.Bold
                )
        }} ,
        containerColor = colors.onSecondaryColor,
        contentColor = colors.tertiaryColor,
        modifier = Modifier
            .padding(bottom = 10.dp)
            .border(
                width = 2.dp,
                color = colors.tertiaryColor,
                shape = RoundedCornerShape(8.dp)
            ),
    ) {
        Text(
            snackBarData.visuals.message,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}