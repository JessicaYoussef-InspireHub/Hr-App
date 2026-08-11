package net.inspirehub.hr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun MyDialog(
    onConfirm: () -> Unit,
    onDismiss: (() -> Unit),
    title: String,
    subtitle: String,
    confirmButtonText: String,
    dismissButtonText: String? = null,
    isLoading: Boolean = false,
    subtitleContent: (@Composable () -> Unit)? = null
) {

    val colors = appColors()

    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CloseIcon(onClick = {
                        if (!isLoading) {
                            onDismiss()
                        }
                    })
                }
                Text(
                    title,
                    color = colors.tertiaryColor,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        text = {
                if (subtitleContent != null) {
                    subtitleContent()
                } else {
                    Text(
                        text = subtitle,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = colors.onBackgroundColor
                    )
            }
        },
        confirmButton = {
            SmallButtons(
                onConfirm = onConfirm,
                onDismiss = onDismiss,
                confirmButtonText = confirmButtonText,
                dismissButtonText = dismissButtonText,
                isLoading = isLoading
            )
        }
    )
}