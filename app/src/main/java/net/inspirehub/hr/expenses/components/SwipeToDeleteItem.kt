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
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDeleteItem(
    expense: ExpenseItem,
    onDelete: () -> Unit,
    navController: NavController,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onSendSuccess: () -> Unit,
    isDimmed: Boolean,
    is17Version: Boolean
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
                    color = colors.onTertiaryContainer,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        content = {
            ExpenseItemCard(
                expense,
                navController,
                isSelectionMode,
                isSelected,
                onSelect,
                onSendSuccess,
                isDimmed,
                is17Version
            )
        }
    )


    if (showDialog) {

        MyDialog(
            onConfirm = {
                onDelete()
                showDialog = false
            },
            onDismiss = {
                showDialog = false
                scope.launch {
                    dismissState.reset()
                }
            },
            title = stringResource(R.string.delete_confirmation),
            subtitle = stringResource(R.string.are_you_sure_you_want_to_delete_this_expense),
            confirmButtonText = stringResource(R.string.delete),
            dismissButtonText = stringResource(R.string.cancel)
        )
    }
}