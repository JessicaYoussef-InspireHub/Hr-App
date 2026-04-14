package net.inspirehub.hr.expenses.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
import net.inspirehub.hr.expenses.data.deleteExpense

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
        topBar = {
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
                }
            )
        },
        bottomBar = {
            Column {
                NewAndReportButton(
                    onNewExpenses = {
                        navController.navigate("AddExpensesScreen")
                    },
                    onCreateReport = {
                        if (expenses.isEmpty()) {
                            showNoReportDialog = true
                        } else {
                        }
                    },
                    onUpload = {},
                    viewReport = {
                        navController.navigate("MyReportScreen")
                    }
                )
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
                val draftExpenses = remember(expenses) { expenses.filter { it.state == "draft" } }
                val totalDraftAmount = remember(draftExpenses) { draftExpenses.sumOf { it.total_amount } }
                val totalDraftTaxes = remember(draftExpenses) { draftExpenses.sumOf { it.tax_amount ?: 0.0 } }
                val totalDraftWithTaxes = totalDraftAmount + totalDraftTaxes
                val firstExpense = expenses.firstOrNull()
                val totalDraftText = formatAmount(
                    amount = totalDraftWithTaxes,
                    symbol = firstExpense?.currency_symbol,
                    position = firstExpense?.currency_position
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildAnnotatedString {
                            append("${stringResource(R.string.you_have)} ")

                            withStyle(
                                style = SpanStyle(
                                    color = colors.tertiaryColor,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(totalDraftText)
                            }

                            append(" ${stringResource(R.string.in_drafts)}")
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackgroundColor
                    )

                    Spacer(modifier = Modifier.height(30.dp))

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
                                                message = "Expense deleted successfully"
                                            )
                                        } else {
                                            deleteErrorMessage = result.reason
                                        }
                                    }
                                },
                                navController = navController
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

        if (showNoReportDialog) {
            NoReportDialog(
                isLoading = false,
                onCancel = { showNoReportDialog = false }
            )
        }
    }
}