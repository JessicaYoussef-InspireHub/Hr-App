package net.inspirehub.hr.check_in_out.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.R

@Composable
fun NotAllowedLocationDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = stringResource(id = R.string.not_allowed_location_warning))
            },
            text = {
                Text(text = stringResource(R.string.not_allowed_location_warning))
            },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }
}
