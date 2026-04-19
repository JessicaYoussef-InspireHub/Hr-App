package net.inspirehub.hr.expenses.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.AnalyticDistribution
import net.inspirehub.hr.expenses.components.CategoryDropdown
import net.inspirehub.hr.expenses.components.DescriptionInputExpenses
import net.inspirehub.hr.expenses.components.ExpenseDate
import net.inspirehub.hr.expenses.components.ExpensesSnackBar
import net.inspirehub.hr.expenses.components.IncludedTaxes
import net.inspirehub.hr.expenses.components.Notes
import net.inspirehub.hr.expenses.components.PaidBy
import net.inspirehub.hr.expenses.components.SaveCancelButton
import net.inspirehub.hr.expenses.components.TextFirstExpenses
import net.inspirehub.hr.expenses.components.TotalPriceExpenses
import net.inspirehub.hr.expenses.data.Expense
import net.inspirehub.hr.expenses.data.ExpenseCategory
import net.inspirehub.hr.expenses.data.ExpenseCurrency
import net.inspirehub.hr.expenses.data.Tax
import net.inspirehub.hr.expenses.data.editExpense
import net.inspirehub.hr.expenses.data.fetchExpenseCategories
import net.inspirehub.hr.expenses.data.fetchExpenses
import net.inspirehub.hr.expenses.data.fetchTaxes
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun EditExpenseScreen(
    navController: NavController,
    expenseId: Int,
    token: String
) {
    val context = LocalContext.current
    var expense by remember { mutableStateOf<Expense?>(null) }
    val scope = rememberCoroutineScope()
    val successUpdatedMessage = stringResource(R.string.expense_created_successfully)
    val colors = appColors()
    val snackBarHostState = remember { SnackbarHostState() }
    var descriptionText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }
    var descriptionError by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var categories by remember { mutableStateOf<List<ExpenseCategory>>(emptyList()) }
    var selectedDate by remember {
        mutableStateOf(expense?.date?.let { LocalDate.parse(it) } ?: LocalDate.now())
    }
    var amount by remember { mutableStateOf(expense?.total_amount) }
    var selectedTaxes by remember { mutableStateOf<List<Tax>>(emptyList()) }
    var analyticDistribution by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var paidBy by remember { mutableStateOf("employee") }
    var amountError by remember { mutableStateOf(false) }
    var selectedCurrency by remember { mutableStateOf<ExpenseCurrency?>(null) }
    var taxes by remember { mutableStateOf<List<Tax>>(emptyList()) }
    var isFetching by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    val failedMessage = stringResource(R.string.failed_to_update_expense)

    LaunchedEffect(expenseId) {
        isFetching = true

        val tokenFromPref = token

        val fetchedCategories = fetchExpenseCategories(context, tokenFromPref)
        val fetchedTaxes = fetchTaxes(context, tokenFromPref)
        val allExpenses = fetchExpenses(context, tokenFromPref)

        expense = allExpenses.find { it.id == expenseId }

        categories = fetchedCategories
        taxes = fetchedTaxes

        expense?.let { exp ->
            descriptionText = exp.name
            noteText = exp.description
            paidBy = exp.payment_mode ?: "employee"
            amount = exp.total_amount

            selectedCategory = fetchedCategories.find { it.id == exp.product_id }

            selectedDate = exp.date.let { LocalDate.parse(it) } ?: LocalDate.now()

            analyticDistribution = exp.analytic_distribution
                .mapKeys { it.key.toIntOrNull() ?: 0 }
                .mapValues { it.value.toInt() }

            selectedTaxes = exp.taxes.map { expTax ->
                Tax(
                    id = expTax.id,
                    name = expTax.name,
                    amount = expTax.amount,
                    amount_type = expTax.amount_type,
                    description = "",
                    company_id = 0,
                    company_name = ""
                )
            }
        }

        isFetching = false
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
                    label = stringResource(R.string.edit_expense),
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            },
            bottomBar = {
                Column {
                    SaveCancelButton(
                        stringResource(R.string.update),
                        isLoading = isFetching || isSubmitting,
                        onCancel = {
                            navController.navigate("ExpensesScreen")
                        },
                        onConfirm = {
                            if (isFetching || isSubmitting) return@SaveCancelButton
                            descriptionError = descriptionText.isBlank()
                            amountError = amount == null || amount == 0.0
                            if (descriptionError || amountError) return@SaveCancelButton
                            isSubmitting = true

                            scope.launch {
                                val success = editExpense(
                                    context = context,
                                    token = token,
                                    expenseId = expenseId,
                                    name = descriptionText,
                                    totalAmount = amount ?: 0.0,
                                    date = selectedDate.toString(),
                                    productId = selectedCategory?.id ?: 0,
                                    description = noteText,
                                    currencyId = selectedCurrency?.id ?: expense?.currency_id ?: 1,
                                    paymentMode = paidBy,
                                    taxIds = selectedTaxes.map { it.id },
                                    analyticDistribution = analyticDistribution.mapKeys { it.key.toString() }
                                )

                                if (success) {
                                    snackBarHostState.showSnackbar(successUpdatedMessage)

                                    navController.navigate("ExpensesScreen") {
                                        popUpTo("ExpensesScreen") { inclusive = true }
                                    }
                                } else {
                                    snackBarHostState.showSnackbar(failedMessage)
                                }

                                isSubmitting = false

                            }
                        },
                    )
                    BottomBar(navController = navController)
                }
            }
        )
        { innerPadding ->
            expense?.let { exp ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFirstExpenses(stringResource(R.string.description))
                        DescriptionInputExpenses(
                            description = descriptionText,
                            onDescriptionChange = { newText ->
                                descriptionText = newText
                            }
                        )
                    }
                    if (descriptionError) {
                        Text(
                            text = stringResource(R.string.please_enter_description),
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
                        TextFirstExpenses(stringResource(R.string.category))
                        Spacer(modifier = Modifier.width(10.dp))
                        CategoryDropdown(
                            categories = categories,
                            initialSelectedCategory = selectedCategory,
                            description = selectedCategory?.name ?: "",
                            onCategorySelected = { category ->
                                selectedCategory = category
                            },
                            onDescriptionChange = { text -> }
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFirstExpenses(stringResource(R.string.expense_date))
                        Spacer(modifier = Modifier.width(10.dp))
                        ExpenseDate(
                            selectedDate = selectedDate,
                            onDateSelected = { newDate -> selectedDate = newDate }
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFirstExpenses(stringResource(R.string.total))
                        Spacer(modifier = Modifier.width(10.dp))
                        TotalPriceExpenses(
                            token = token,
                            context = context,
                            initialAmount = expense?.total_amount,
                            onAmountChange = { newAmount -> amount = newAmount },
                            onConvertedAmountChange = { converted -> },
                            onCurrencySelected = { currency -> selectedCurrency = currency },
                            initialCurrencyCode = expense?.currency,
                        )
                    }

                    if (amountError) {
                        Text(
                            text = stringResource(R.string.please_enter_amount),
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
                        TextFirstExpenses(stringResource(R.string.included_taxes))
                        Spacer(modifier = Modifier.width(10.dp))
                        IncludedTaxes(
                            taxes = taxes,
                            selectedTaxes = selectedTaxes,
                            onTaxesChange = { newSelectedTaxes ->
                                selectedTaxes = newSelectedTaxes
                            },
                            amount = amount ?: 0.0,
                            currencySymbol = expense?.currency ?: "$",
                            currencyPosition = expense?.currency_position ?: "before"
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFirstExpenses(stringResource(R.string.analytic_distribution))
                        Spacer(modifier = Modifier.width(10.dp))
                        AnalyticDistribution(
                            initialDistribution = analyticDistribution,
                            onDistributionChange = { newDistribution ->
                                analyticDistribution = newDistribution
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(25.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFirstExpenses(stringResource(R.string.paid_by))
                        Spacer(modifier = Modifier.width(10.dp))
                        PaidBy(
                            initialPaidBy = paidBy,
                            onPaymentModeChange = { mode ->
                                paidBy = mode
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextFirstExpenses(stringResource(R.string.notes))
                        Spacer(modifier = Modifier.width(10.dp))
                        Notes(
                            notes = noteText,
                            onNotesChange = { noteText = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }
        if (isFetching || isSubmitting) {
            Box(
                modifier = Modifier
                    .clickable(enabled = false) {}
            ) {
                FullLoading()
            }
        }
    }
}