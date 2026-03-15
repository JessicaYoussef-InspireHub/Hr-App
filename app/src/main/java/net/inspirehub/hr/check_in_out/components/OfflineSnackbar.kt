package net.inspirehub.hr.check_in_out.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape


@Composable
fun OfflineSnackBar(
    message: String,
    onDismiss: () -> Unit
) {
    val colors = appColors()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(message) {
        if (message.isNotEmpty()) {
            scope.launch {
                snackBarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Long
                )
                onDismiss()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            snackbar = { snackBarData ->
                Snackbar(
                    containerColor = colors.onSecondaryColor,
                    contentColor = colors.tertiaryColor,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .border(
                            width = 2.dp,
                            color = colors.tertiaryColor,
                            shape = RoundedCornerShape(8.dp)
                        ),
                ) {
                    Text(
                        text = snackBarData.visuals.message,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        )
    }
}