package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors

@Composable
fun OrderSnackBar(
    snackBarData: SnackbarData
) {
    val colors = appColors()

    Snackbar (
        containerColor = colors.onSecondaryColor,
        contentColor = colors.tertiaryColor,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .border(
                width = 2.dp,
                color = colors.tertiaryColor,
                shape = RoundedCornerShape(8.dp)
            ),
    ){
        Text(snackBarData.visuals.message ,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}