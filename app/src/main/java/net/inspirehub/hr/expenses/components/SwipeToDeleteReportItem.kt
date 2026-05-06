package net.inspirehub.hr.expenses.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteReportItem(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val colors = appColors()
    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                showDialog = true
                false
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
                    .background(colors.error)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = stringResource(R.string.delete),
                    color = colors.onBackgroundColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        content = {
            content()
        }
    )

    if (showDialog) {
        ReportSelectedDeleteConfirmationDialog(
            count = 1,
            onConfirm = {
                onDelete()
                showDialog = false
            },
            onDismiss = {
                showDialog = false
                scope.launch { dismissState.reset() }
            }
        )
    }
}