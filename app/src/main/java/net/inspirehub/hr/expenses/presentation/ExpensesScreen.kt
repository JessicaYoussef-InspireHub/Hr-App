package net.inspirehub.hr.expenses.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.ExpenseItem
import net.inspirehub.hr.expenses.components.NewAndReportButton
import net.inspirehub.hr.expenses.components.NoReportDialog
import net.inspirehub.hr.expenses.components.SwipeToDeleteItem
import net.inspirehub.hr.expenses.data.Expense
import net.inspirehub.hr.expenses.data.fetchExpenses
import kotlinx.coroutines.launch
import net.inspirehub.hr.expenses.components.DeleteExpenseErrorDialog
import net.inspirehub.hr.expenses.components.ExpensesSnackBar
import net.inspirehub.hr.expenses.components.SelectedDeleteConfirmationDialog
import net.inspirehub.hr.expenses.components.UploadBottomSheet
import net.inspirehub.hr.expenses.data.deleteExpense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpensesScreen(
    navController: NavController,
    token: String,
) {
    val colors = appColors()

    var expenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var showNoReportDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var deleteErrorMessage by remember { mutableStateOf<String?>(null) }
    val snackBarHostState = remember { SnackbarHostState() }
    var showUploadSheet by remember { mutableStateOf(false) }
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(setOf<Int>()) }
    val oneDeletedMessage = stringResource(R.string.expense_deleted_successfully)
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val successMessage = { count: Int ->
        context.getString(R.string.deleted_successfully, count)
    }

    val failedMessage = { count: Int ->
        context.getString(R.string.could_not_be_deleted, count)
    }

    LaunchedEffect(Unit) {
        isLoading = true
        expenses = fetchExpenses(context = context, token = token)
        println("Loaded ${expenses.size} expenses")
        isLoading = false
    }

    fun formatAmount(
        amount: Double,
        symbol: String?,
        position: String?
    ): String {

        val safeSymbol = symbol ?: ""

        return when (position) {
            "before" -> "$safeSymbol $amount"
            "after" -> "$amount $safeSymbol"
            else -> "$amount $safeSymbol"
        }
    }

    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = @Composable {
            if (!isSelectionMode) {
                MyAppBar(
                    label = stringResource(R.string.expenses),
                    onBackClick = {
                        val previousRoute =
                            navController.previousBackStackEntry?.destination?.route

                        if (previousRoute == "ExpensesScreen") {
                            navController.popBackStack()
                            navController.popBackStack()
                        } else {
                            navController.popBackStack()
                        }
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = "Select",
                            tint = colors.onSecondaryColor,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    isSelectionMode = true
                                }
                        )
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(
                                R.string.item_selected,
                                selectedItems.size
                            ),
                            color = colors.onBackgroundColor
                        )
                    },
                    actions = {
                        Text(
                            text = if (selectedItems.size == expenses.size)
                                stringResource(R.string.unselect_all)
                            else
                                stringResource(R.string.select_all),
                            color = colors.tertiaryColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    selectedItems = if (selectedItems.size == expenses.size) {
                                        emptySet()
                                    } else {
                                        expenses.map { it.id }.toSet()
                                    }
                                }
                        )
                        Text(
                            text = stringResource(R.string.delete),
                            color = if (selectedItems.isEmpty())
                                colors.onBackgroundColor.copy(alpha = 0.4f)
                            else
                                colors.error,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable(enabled = selectedItems.isNotEmpty()) {
                                    showDeleteConfirmDialog = true
                                }
                        )

                        Text(
                            text = stringResource(R.string.cancel),
                            color = colors.tertiaryColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    isSelectionMode = false
                                    selectedItems = emptySet()
                                }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.surfaceContainerHigh
                    )
                )
            }
        },
        bottomBar = {
            Column {
                if (!isSelectionMode) {
                    NewAndReportButton(
                        onNewExpenses = {
                            navController.navigate("AddExpensesScreen")
                        },
                        onCreateReport = {
                            if (expenses.isEmpty()) {
                                showNoReportDialog = true
                            } else {
                                navController.navigate("MyReportScreen")
                            }
                        },
                        onUpload = {
                            showUploadSheet = true
                        },
                        viewReport = {
                            navController.navigate("MyReportScreen")
                        },
                    )
                }
                BottomBar(navController = navController)
            }

        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    ExpensesSnackBar(snackBarData = data)
                }
            )
        },
    ) { innerPadding ->
        when {
            isLoading -> {
                FullLoading()
            }

            expenses.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_expenses_yet),
                        color = colors.onBackgroundColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {

                    Spacer(modifier = Modifier.height(4.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        items(expenses) { expense ->
                            SwipeToDeleteItem(
                                expense = ExpenseItem(
                                    id = expense.id,
                                    description = expense.name,
                                    totalAmount = formatAmount(
                                        amount = expense.total_amount,
                                        symbol = expense.currency_symbol,
                                        position = expense.currency_position
                                    ),
                                    date = expense.date,
                                    status = expense.state,
                                    taxesAmount = expense.tax_amount?.toInt(),
                                    currencySymbol = expense.currency_symbol,
                                    currencyPosition = expense.currency_position
                                ),
                                onDelete = {
                                    scope.launch {
                                        val result = deleteExpense(
                                            context = context,
                                            token = token,
                                            expenseId = expense.id
                                        )

                                        if (result.success) {
                                            expenses = expenses.filter { it.id != expense.id }
                                            snackBarHostState.showSnackbar(
                                                message = oneDeletedMessage
                                            )
                                        } else {
                                            deleteErrorMessage = result.message
                                        }
                                    }
                                },
                                navController = navController,
                                isSelectionMode = isSelectionMode,
                                isSelected = selectedItems.contains(expense.id),
                                onSelect = {
                                    selectedItems = if (selectedItems.contains(expense.id)) {
                                        selectedItems - expense.id
                                    } else {
                                        selectedItems + expense.id
                                    }
                                },
                                onSendSuccess = {
                                    scope.launch {
                                        isLoading = true
                                        expenses = fetchExpenses(context, token)
                                        isLoading = false

                                        snackBarHostState.showSnackbar(
                                            context.getString(R.string.expense_sent_successfully)
                                        )
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        deleteErrorMessage?.let { reason ->
            DeleteExpenseErrorDialog(
                reason = reason,
                onDismiss = { deleteErrorMessage = null }
            )
        }

        if (showDeleteConfirmDialog) {
            SelectedDeleteConfirmationDialog(
                count = selectedItems.size,
                onDismiss = {
                    showDeleteConfirmDialog = false
                },
                onConfirm = {
                    showDeleteConfirmDialog = false

                    scope.launch {

                        val toDelete = selectedItems.toList()

                        val successfulIds = mutableSetOf<Int>()
                        var failedCount = 0

                        toDelete.forEach { id ->
                            val result = deleteExpense(
                                context = context,
                                token = token,
                                expenseId = id
                            )

                            if (result.success) {
                                successfulIds.add(id)
                            } else {
                                failedCount++
                            }
                        }

                        expenses = expenses.filter { it.id !in successfulIds }

                        val successCount = successfulIds.size

                        selectedItems = emptySet()
                        isSelectionMode = false

                        val message = when {
                            successCount > 0 && failedCount > 0 ->
                                "${successMessage(successCount)} - ${failedMessage(failedCount)}"

                            successCount > 0 ->
                                successMessage(successCount)

                            else ->
                                failedMessage(failedCount)
                        }
                        snackBarHostState.showSnackbar(message)
                    }
                }
            )
        }

        if (showNoReportDialog) {
            NoReportDialog(
                isLoading = false,
                onCancel = { showNoReportDialog = false }
            )
        }

        if (showUploadSheet) {
            UploadBottomSheet(
                onDismiss = { showUploadSheet = false },
                onCameraClick = { },
                onGalleryClick = { }
            )
        }
    }
}