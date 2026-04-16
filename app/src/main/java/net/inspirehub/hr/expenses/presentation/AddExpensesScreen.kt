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
import androidx.compose.runtime.mutableDoubleStateOf
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
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.AnalyticDistribution
import net.inspirehub.hr.expenses.components.ExpenseDate
import net.inspirehub.hr.expenses.components.CategoryDropdown
import net.inspirehub.hr.expenses.components.DescriptionInputExpenses
import net.inspirehub.hr.expenses.components.IncludedTaxes
import net.inspirehub.hr.expenses.components.Notes
import net.inspirehub.hr.expenses.components.PaidBy
import net.inspirehub.hr.expenses.components.SaveCancelButton
import net.inspirehub.hr.expenses.components.TextFirstExpenses
import net.inspirehub.hr.expenses.components.TotalPriceExpenses
import net.inspirehub.hr.expenses.data.ExpenseCategory
import net.inspirehub.hr.expenses.data.ExpenseCurrency
import net.inspirehub.hr.expenses.data.Tax
import net.inspirehub.hr.expenses.data.createExpense
import net.inspirehub.hr.expenses.data.fetchExpenseCategories
import net.inspirehub.hr.expenses.data.fetchTaxes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.expenses.components.ExpensesSnackBar
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExpensesScreen(
    navController: NavController
) {
    val colors = appColors()
    val context = LocalContext.current
    var taxes by remember { mutableStateOf<List<Tax>>(emptyList()) }
    var categories by remember { mutableStateOf<List<ExpenseCategory>>(emptyList()) }
    val sharedPref = SharedPrefManager(context)
    var description by remember { mutableStateOf("") }
    var totalAmount by remember { mutableDoubleStateOf(0.0) }
    var convertedAmount by remember { mutableStateOf<Double?>(null) }
    val amountForTaxes = convertedAmount ?: totalAmount
    var selectedCurrency by remember { mutableStateOf<ExpenseCurrency?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    var notes by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ExpenseCategory?>(null) }
    var selectedTaxes by remember { mutableStateOf<List<Tax>>(emptyList()) }
    var analyticMap by remember { mutableStateOf<Map<Int, Int>>(emptyMap()) }
    var paymentMode by remember { mutableStateOf("employee") }
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val successMessage = stringResource(R.string.expense_created_successfully)
    var isLoading by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var currencyError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val token = sharedPref.getToken()
        if (!token.isNullOrEmpty()) {
            taxes = fetchTaxes(context, token)
            categories = fetchExpenseCategories(context, token)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
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
                    label = stringResource(R.string.add_expenses),
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
                            categoryError = selectedCategory == null
                            descriptionError = description.isBlank()
                            amountError = totalAmount <= 0.0
                            currencyError = selectedCurrency == null

                            if (categoryError || descriptionError || amountError || currencyError) return@SaveCancelButton
                            isLoading = true
                            val token = sharedPref.getToken() ?: return@SaveCancelButton

                            CoroutineScope(Dispatchers.IO).launch {
                                val response = createExpense(
                                    context = context,
                                    token = token,
                                    name = description,
                                    productId = selectedCategory?.id ?: return@launch,
                                    totalAmount = totalAmount,
                                    date = selectedDate.format(apiFormatter),
                                    description = notes,
                                    analyticDistribution = analyticMap,
                                    taxIds = selectedTaxes.map { it.id },
                                    payment_mode = paymentMode,
                                    currencyId = selectedCurrency!!.id
                                )

                                if (response.status == "success") {
                                    scope.launch {
                                        snackBarHostState.showSnackbar(successMessage)
                                        navController.navigate("ExpensesScreen") {
                                            popUpTo("AddExpensesScreen") { inclusive = true }
                                        }
                                    }
                                    println("Expense created with id: ${response.expense_id}")
                                } else {
                                    println("Failed: ${response.message}")
                                }
                            }
                        },
                    )
                    BottomBar(navController = navController)
                }
            }
        ) { innerPadding ->
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
                        description = description,
                        onDescriptionChange = { description = it }
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
                        description = description,
                        onCategorySelected = { category ->
                            selectedCategory = category
                        },
                        onDescriptionChange = { text ->
                            description = text
                        }
                    )
                }
                if (categoryError) {
                    Text(
                        text = stringResource(R.string.please_select_a_category),
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
                    TextFirstExpenses(stringResource(R.string.expense_date))
                    Spacer(modifier = Modifier.width(10.dp))
                    ExpenseDate(
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it }
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
                        token = sharedPref.getToken() ?: "",
                        context = context,
                        initialAmount = totalAmount ,
                        onAmountChange = { totalAmount = it },
                        onConvertedAmountChange = { convertedAmount = it },
                        onCurrencySelected = { currency -> selectedCurrency = currency }
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

                if (currencyError) {
                    Text(
                        text = stringResource(R.string.please_select_a_currency),
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
                        onTaxesChange = { selectedTaxes = it },
                        amount = amountForTaxes,
                        currencySymbol = selectedCurrency?.symbol ?: "",
                        currencyPosition = selectedCurrency?.position ?: ""
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
                        onDistributionChange = {
                            analyticMap = it
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
                        initialPaidBy = paymentMode,
                        onPaymentModeChange = { paymentMode = it }
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
                        notes = notes,
                        onNotesChange = { notes = it }
                    )
                }
                Spacer(modifier = Modifier.height(25.dp))

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
    }
}