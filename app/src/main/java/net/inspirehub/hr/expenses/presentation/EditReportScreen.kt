package net.inspirehub.hr.expenses.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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
import net.inspirehub.hr.expenses.components.ExpenseReportSummary
import net.inspirehub.hr.expenses.components.ExpensesSelectionCard
import net.inspirehub.hr.expenses.components.ExpensesSnackBar
import net.inspirehub.hr.expenses.components.SaveCancelButton
import net.inspirehub.hr.expenses.components.TextFirstExpenses
import net.inspirehub.hr.expenses.data.Expense
import net.inspirehub.hr.expenses.data.ExpenseReport
import net.inspirehub.hr.expenses.data.fetchExpenses
import net.inspirehub.hr.expenses.data.fetchReports
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import net.inspirehub.hr.expenses.data.editReport
import androidx.compose.material3.ExperimentalMaterial3Api
import net.inspirehub.hr.expenses.components.AddExpensesButton
import net.inspirehub.hr.expenses.components.EditReportBottomSheet
import net.inspirehub.hr.expenses.data.fetchExpensesForReport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReportScreen(
    navController: NavController,
    reportId: Int,
    newExpenseId: Int? = null
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { net.inspirehub.hr.SharedPrefManager(context) }
    val token = sharedPref.getToken().orEmpty()
    var isLoading by remember { mutableStateOf(false) }
    var report by remember { mutableStateOf<ExpenseReport?>(null) }
    var isExpanded by remember { mutableStateOf(true) }
    var summaryName by remember { mutableStateOf("") }
    val snackBarHostState = remember { SnackbarHostState() }
    var summaryError by remember { mutableStateOf(false) }
    var allExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    val firstExpense = report?.expenses?.firstOrNull()
    val firstMatchedExpense = allExpenses.find { it.id == firstExpense?.id }
    val scope = rememberCoroutineScope()
    var isUpdating by remember { mutableStateOf(false) }
    var selectedExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var availableExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isBottomSheetLoading by remember { mutableStateOf(false) }
    var expensesError by remember { mutableStateOf(false) }
    var removedExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }

    val firstPaymentMode = when (firstMatchedExpense?.payment_mode) {
        "company_account" -> stringResource(R.string.company)
        "own_account" -> stringResource(R.string.employee)
        else -> "Unknown"
    }

    LaunchedEffect(reportId, newExpenseId) {

        isLoading = true

        val allReports = fetchReports(context, token)

        allExpenses = fetchExpenses(
            context = context,
            token = token
        ).expenses

        report = allReports.find { it.sheet_id == reportId }

        selectedExpenses = allExpenses.filter { expense ->
            report?.expenses?.any { reportExpense ->
                reportExpense.id == expense.id
            } == true
        }

        if (newExpenseId != null && newExpenseId != -1) {
            val newExpense = allExpenses.find { it.id == newExpenseId }

            newExpense?.let {
                selectedExpenses = selectedExpenses + it
            }
        }

        report?.let { rep -> summaryName = rep.name }

        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Scaffold(
            containerColor = colors.onSecondaryColor,
            snackbarHost = {
                SnackbarHost(
                    hostState = snackBarHostState
                ) { data ->
                    ExpensesSnackBar(
                        snackBarData = data
                    )
                }
            },
            topBar = {
                MyAppBar(
                    label = stringResource(R.string.edit_report),
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            },
            bottomBar = {
                Column {
                    SaveCancelButton(
                        stringResource(R.string.update),
                        isLoading = isLoading,
                        onCancel = {
                            navController.navigate("MyReportScreen")
                        },
                        onConfirm = {
                            if (isLoading) return@SaveCancelButton

                            summaryError = summaryName.isBlank()
                            if (summaryError) return@SaveCancelButton

                            if (selectedExpenses.isEmpty()) {
                                expensesError = true
                                return@SaveCancelButton
                            } else {
                                expensesError = false
                            }

                            val originalExpenseIds =
                                report?.expenses?.map { it.id } ?: emptyList()

                            val currentExpenseIds =
                                selectedExpenses.map { it.id }

                            val removedExpenseIds =
                                originalExpenseIds.filterNot { id ->
                                    currentExpenseIds.contains(id)
                                }

                            scope.launch {

                                isUpdating = true

                                val expenseIds = selectedExpenses.map { it.id }

                                val success = editReport(
                                    context = context,
                                    token = token,
                                    sheetId = reportId,
                                    expenseIds = expenseIds,
                                    removeExpenseIds = removedExpenseIds,
                                    name = summaryName
                                )

                                if (success) {
                                    snackBarHostState.showSnackbar(
                                        context.getString(R.string.report_updated_successfully)
                                    )

                                    navController.navigate("MyReportScreen") {
                                        popUpTo("EditReportScreen") { inclusive = true }
                                    }

                                } else {
                                    snackBarHostState.showSnackbar(
                                        context.getString(R.string.failed_to_update_report)
                                    )
                                }

                                isUpdating = false
                            }

                        },
                    )
                    BottomBar(navController = navController)
                }
            }
        )
        { innerPadding ->
            report?.let { it ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 5.dp)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                    ) {
                        TextFirstExpenses(stringResource(R.string.expense_report_summary))
                        ExpenseReportSummary(
                            summary = summaryName,
                            onSummaryChange = {
                                summaryName = it
                                summaryError = false
                            }
                        )
                        if (summaryError) {
                            Text(
                                text = stringResource(R.string.please_enter_expense_report_summary),
                                color = colors.error,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(25.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextFirstExpenses(stringResource(R.string.paid_by))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = firstPaymentMode,
                                color = colors.onBackgroundColor.copy(alpha = 0.6f),
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(25.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween

                        ) {
                            TextFirstExpenses(stringResource(R.string.your_expenses))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = colors.onBackgroundColor,
                                modifier = Modifier
                                    .size(28.dp)
                                    .rotate(if (isExpanded) 0f else 180f)
                                    .clickable {
                                        isExpanded = !isExpanded
                                    }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        AnimatedVisibility(visible = isExpanded) {
                            ExpensesSelectionCard(
                                expenses = selectedExpenses,
                                isLoading = isLoading,
                                onRemove = { removedExpense ->

                                    selectedExpenses =
                                        selectedExpenses.filter { it.id != removedExpense.id }

                                    removedExpenses = removedExpenses + removedExpense

                                    availableExpenses = availableExpenses + removedExpense

                                    expensesError = false
                                }
                            )
                        }

                        if (expensesError) {
                            Text(
                                text = stringResource(R.string.please_add_at_least_one_expense_before_saving),
                                color = colors.error,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    AddExpensesButton(

                        onClick = {

                            scope.launch {
                                isBottomSheetLoading = true

                                val expenses = fetchExpensesForReport(
                                    context = context,
                                    token = token
                                )

                                val requiredPaymentMode =
                                    firstMatchedExpense?.payment_mode

                                availableExpenses = expenses.filter { expense ->
                                    selectedExpenses.none { it.id == expense.id } &&
                                            expense.payment_mode == requiredPaymentMode
                                }

                                isBottomSheetLoading = false
                                showBottomSheet = true
                            }
                        },

                        stringResource(R.string.add_more_expenses)
                    )
                }
            }

        }
        if (isLoading || isUpdating) {
            Box(
                modifier = Modifier
                    .clickable(enabled = false) {}
            ) {
                FullLoading()
            }
        }

        if (showBottomSheet) {

            EditReportBottomSheet(
                showBottomSheet = true,
                isBottomSheetLoading = isBottomSheetLoading,
                removedExpenses = removedExpenses,
                availableExpenses = availableExpenses,

                onDismiss = {
                    showBottomSheet = false
                },

                onRestoreExpense = { expense ->

                    selectedExpenses = selectedExpenses + expense

                    removedExpenses =
                        removedExpenses.filter { it.id != expense.id }

                    availableExpenses =
                        availableExpenses.filter { it.id != expense.id }
                },

                onAddExpense = { expense ->

                    selectedExpenses = selectedExpenses + expense

                    availableExpenses = availableExpenses.filter { it.id != expense.id }
                },
                navController = navController,
                reportId = reportId,
                paymentMode = firstMatchedExpense?.payment_mode ?: "employee"
            )
        }
    }
}