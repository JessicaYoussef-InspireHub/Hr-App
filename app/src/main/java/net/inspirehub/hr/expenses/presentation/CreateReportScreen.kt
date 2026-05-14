package net.inspirehub.hr.expenses.presentation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.mutableIntStateOf
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
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.expenses.components.ExpenseReportSummary
import net.inspirehub.hr.expenses.components.ExpensesSelectionCard
import net.inspirehub.hr.expenses.components.ExpensesSnackBar
import net.inspirehub.hr.expenses.components.SaveCancelButton
import net.inspirehub.hr.expenses.components.TextFirstExpenses
import net.inspirehub.hr.expenses.data.Expense
import net.inspirehub.hr.expenses.data.fetchExpensesForReport
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import net.inspirehub.hr.expenses.components.AddExpensesButton
import net.inspirehub.hr.expenses.components.EditReportBottomSheet
import net.inspirehub.hr.expenses.components.EmptyExpensesDialog
import net.inspirehub.hr.expenses.data.submitReport

@SuppressLint("AutoboxingStateCreation")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateReportScreen(
    navController: NavController,
    type: String
) {

    val colors = appColors()
    val snackBarHostState = remember { SnackbarHostState() }
    var isLoading by remember { mutableStateOf(false) }
    var summary by remember { mutableStateOf("") }
    var summaryError by remember { mutableStateOf(false) }
    var isExpensesLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    var showEmptyExpensesDialog by remember { mutableStateOf(false) }
    var refreshTrigger by remember { mutableIntStateOf(0) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var availableExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isBottomSheetLoading by remember { mutableStateOf(false) }
    var removedExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var selectedExpenses by remember { mutableStateOf<List<Expense>>(emptyList()) }

    LaunchedEffect(refreshTrigger) {

        isExpensesLoading = true
        val token = SharedPrefManager(context).getToken() ?: ""
        val allExpenses = fetchExpensesForReport(context, token)

        selectedExpenses = allExpenses.filter {
            when (type) {
                "company" -> it.payment_mode == "company_account"
                "employee" -> it.payment_mode == "own_account"
                else -> false
            }
        }

        isExpensesLoading = false
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
                    label = stringResource(R.string.create_report),
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            },
            bottomBar = {
                Column {
                    SaveCancelButton(
                        stringResource(R.string.save),
                        isLoading = isLoading,
                        onCancel = {
                            navController.navigate("ExpensesScreen") {
                                popUpTo("AddExpensesScreen") { inclusive = true }
                            }
                        },
                        onConfirm = {
                            if (isLoading) return@SaveCancelButton

                            summaryError = summary.isBlank()
                            if (summaryError) return@SaveCancelButton

                            if (selectedExpenses.isEmpty()) {
                                showEmptyExpensesDialog = true
                                return@SaveCancelButton
                            }

                            scope.launch {
                                isLoading = true

                                val token = SharedPrefManager(context).getToken() ?: ""

                                val expenseIds = selectedExpenses.map { it.id }


                                val success = submitReport(
                                    context = context,
                                    token = token,
                                    expenseIds = expenseIds,
                                    name = summary
                                )


                                if (success) {
                                    snackBarHostState.showSnackbar(
                                        context.getString(R.string.report_created_successfully)
                                    )

                                    navController.navigate("MyReportScreen") {
                                        popUpTo("CreateReportScreen") { inclusive = true }
                                    }

                                } else {
                                    println("Failed to submit report")
                                }

                                isLoading = false
                            }
                        }
                    )
                    BottomBar(navController = navController)
                }
            }
        ) { innerPadding ->
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
                        summary = summary,
                        onSummaryChange = {
                            summary = it
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
                            text = if (type == "company")
                                stringResource(R.string.company)
                            else
                                stringResource(R.string.employee),
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
                        TextFirstExpenses(stringResource(R.string.choose_expenses))
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
                            isLoading = isExpensesLoading,
                            onRemove = { removedExpense ->

                                selectedExpenses =
                                    selectedExpenses.filter { it.id != removedExpense.id }

                                removedExpenses = removedExpenses + removedExpense

                                availableExpenses = availableExpenses + removedExpense
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                AddExpensesButton(
                    onClick = {
                        scope.launch {
                            isBottomSheetLoading = true

                            val token = SharedPrefManager(context).getToken() ?: ""
                            val allExpenses = fetchExpensesForReport(context, token)

                            val requiredPaymentMode =
                                if (selectedExpenses.isNotEmpty())
                                    selectedExpenses.first().payment_mode
                                else
                                    null

                            availableExpenses = allExpenses.filter { expense ->
                                selectedExpenses.none { it.id == expense.id } &&
                                        (requiredPaymentMode == null || expense.payment_mode == requiredPaymentMode)
                            }

                            isBottomSheetLoading = false
                            showBottomSheet = true
                        }
                    },
                    label = stringResource(R.string.add_more_expenses)
                )
            }
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .clickable(enabled = false) {}
                ) {
                    FullLoading()
                }
            }
        }

        if (showEmptyExpensesDialog) {
            EmptyExpensesDialog(
                onDismiss = {
                    showEmptyExpensesDialog = false
                },
                onAddExpenses = {
                    showEmptyExpensesDialog = false
                    refreshTrigger++
                }
            )
        }

        if (showBottomSheet) {
            EditReportBottomSheet(
                showBottomSheet = true,
                isBottomSheetLoading = isBottomSheetLoading,
                removedExpenses = removedExpenses,
                availableExpenses = availableExpenses,

                onDismiss = { showBottomSheet = false },

                onAddExpense = { expense ->

                    selectedExpenses = selectedExpenses + expense

                    availableExpenses = availableExpenses.filter { it.id != expense.id }

                    removedExpenses = removedExpenses.filter { it.id != expense.id }
                },

                onRestoreExpense = { expense ->

                    selectedExpenses = selectedExpenses + expense

                    removedExpenses = removedExpenses.filter { it.id != expense.id }

                    availableExpenses = availableExpenses.filter { it.id != expense.id }
                },

                navController = navController,
                reportId = -1,
                paymentMode = type,
                editScreen = false
            )
        }
    }
}