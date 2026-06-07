package net.inspirehub.hr.expenses.presentation

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import net.inspirehub.hr.expenses.components.ReportCard
import net.inspirehub.hr.expenses.data.ExpenseReport
import net.inspirehub.hr.expenses.data.fetchReports
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import net.inspirehub.hr.expenses.components.CreateAnotherReport
import net.inspirehub.hr.expenses.components.DeleteExpenseErrorDialog
import net.inspirehub.hr.expenses.components.ExpensesAndReportSearchBar
import net.inspirehub.hr.expenses.components.ExpensesFilterBottomSheet
import net.inspirehub.hr.expenses.components.ExpensesSnackBar
import net.inspirehub.hr.expenses.components.NoReportDialog
import net.inspirehub.hr.expenses.components.PaymentTypeBottomSheet
import net.inspirehub.hr.expenses.components.SelectedDeleteConfirmationDialog
import net.inspirehub.hr.expenses.components.SwipeToDeleteReportItem
import net.inspirehub.hr.expenses.data.deleteReport
import net.inspirehub.hr.expenses.data.fetchExpensesForReport
import net.inspirehub.hr.utils.formatNumber

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportScreen(
    navController: NavController,
) {
    var reports by remember { mutableStateOf<List<ExpenseReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { net.inspirehub.hr.SharedPrefManager(context) }
    val token = sharedPref.getToken().orEmpty()
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedReports by remember { mutableStateOf(setOf<Int>()) }
    val scope = rememberCoroutineScope()
    val currentLanguage = sharedPref.getLanguage()
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    var deleteErrorMessage by remember { mutableStateOf<String?>(null) }
    val oneDeletedMessage = stringResource(R.string.report_deleted_successfully)
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showNoReportDialog by remember { mutableStateOf(false) }
    var isLoadingReports by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showFilterSheet by remember { mutableStateOf(false) }
    var selectedStatuses by remember { mutableStateOf(setOf<String>()) }
    var tempSelectedStatuses by remember { mutableStateOf(setOf<String>()) }

    val filteredReports = reports.filter { report ->

        val matchesSearch =
            report.name.contains(searchQuery, ignoreCase = true)

        val matchesStatus =
            selectedStatuses.isEmpty()
                    || selectedStatuses.contains("all")
                    || selectedStatuses.contains(report.state)

        matchesSearch && matchesStatus
    }

    val successMessage = { count: Int ->
        context.getString(R.string.deleted_successfully, count)
    }
    val failedMessage = { count: Int ->
        context.getString(R.string.could_not_be_deleted, count)
    }

    LaunchedEffect(Unit) {
        isLoading = true
        reports = fetchReports(context, token)
        isLoading = false
    }

    Scaffold(
        containerColor = colors.onSecondaryColor,
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState,
                snackbar = { data ->
                    ExpensesSnackBar(snackBarData = data)
                }
            )
        },
        topBar = @Composable {
            if (!isSelectionMode) {
                MyAppBar(
                    label = stringResource(R.string.my_reports),
                    onBackClick = {
                        navController.navigate("ExpensesScreen")
                    },
                    actions = {
                        Icon(
                            imageVector = Icons.Default.Checklist,
                            contentDescription = "Select",
                            tint = colors.onSecondaryColor,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(horizontal = 8.dp)
                                .clickable { isSelectionMode = !isSelectionMode }
                        )
                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = formatNumber(
                                stringResource(R.string.item_selected, selectedReports.size),
                                currentLanguage
                            ),
                            color = colors.onBackgroundColor
                        )
                    },
                    actions = {
                        Text(
                            text = if (selectedReports.size == reports.size)
                                stringResource(R.string.unselect_all)
                            else
                                stringResource(R.string.select_all),
                            color = colors.tertiaryColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    selectedReports =
                                        if (selectedReports.size == reports.size)
                                            emptySet()
                                        else
                                            reports.map { it.sheet_id }.toSet()
                                }
                        )
                        Text(
                            text = stringResource(R.string.delete),
                            color = if (selectedReports.isEmpty())
                                colors.onBackgroundColor.copy(alpha = 0.4f)
                            else
                                colors.error,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable(enabled = selectedReports.isNotEmpty()) {

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
                                    selectedReports = emptySet()
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
                CreateAnotherReport(
                    isLoading = isLoading,
                    onConfirm = {
                        scope.launch {

                            isLoadingReports = true

                            val reportExpenses = fetchExpensesForReport(context, token)

                            isLoadingReports = false

                            if (reportExpenses.isEmpty()) {
                                showNoReportDialog = true
                                return@launch
                            }

                            val paymentTypes = reportExpenses.map { it.payment_mode }.toSet()

                            val type = when {
                                paymentTypes.size == 1 && paymentTypes.contains("company_account") -> "company"
                                paymentTypes.size == 1 && paymentTypes.contains("own_account") -> "employee"
                                else -> null
                            }

                            if (type != null) {
                                navController.navigate("CreateReportScreen?type=$type")
                            } else {
                                showPaymentSheet = true
                            }
                        }
                    },
                )
                BottomBar(navController = navController)

            }
        },
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(vertical = 16.dp, horizontal = 10.dp)
        ) {
            ExpensesAndReportSearchBar(
                label = stringResource(R.string.search_report_by_name),
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { showFilterSheet = true },
                isFilterActive = selectedStatuses.isNotEmpty()
            )

        when {
            isLoading || isLoadingReports -> {
                FullLoading()
            }

            reports.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        stringResource(R.string.no_reports_yet),
                        color = colors.onBackgroundColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            filteredReports.isEmpty() -> {
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
                Spacer(modifier = Modifier.height(30.dp))

                LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp)
                    ) {
                        items(filteredReports) { report ->

                            SwipeToDeleteReportItem(
                                onDelete = {
                                    scope.launch {

                                        val result =
                                            deleteReport(context, token, listOf(report.sheet_id))

                                        if (result.success) {
                                            reports =
                                                reports.filter { it.sheet_id != report.sheet_id }
                                            snackBarHostState.showSnackbar(
                                                message = oneDeletedMessage
                                            )
                                        } else {
                                            deleteErrorMessage = result.message
                                        }
                                    }
                                }
                            ) {
                                ReportCard(
                                    report = report,
                                    isSelectionMode = isSelectionMode,
                                    isSelected = selectedReports.contains(report.sheet_id),
                                    onSelect = {
                                        selectedReports =
                                            if (selectedReports.contains(report.sheet_id)) {
                                                selectedReports - report.sheet_id
                                            } else {
                                                selectedReports + report.sheet_id
                                            }
                                    },
                                    navController = navController,
                                    onSendSuccess = {
                                        scope.launch {
                                            isLoading = true

                                            val updatedReports = fetchReports(context, token)

                                            reports = updatedReports

                                            isLoading = false

                                            snackBarHostState.showSnackbar(
                                                context.getString(R.string.report_sent_to_your_manger_successfully)
                                            )
                                        }
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        if (showDeleteConfirmDialog) {
            SelectedDeleteConfirmationDialog(
                count = selectedReports.size,
                onDismiss = {
                    showDeleteConfirmDialog = false
                },
                onConfirm = {
                    showDeleteConfirmDialog = false

                    scope.launch {
                        val idsToDelete = selectedReports.toList()

                        val result = deleteReport(context, token, idsToDelete)

                        val successIds = result.deleted?.map { it.id } ?: emptyList()
                        val failedCount = result.failed?.size ?: 0

                        reports = reports.filter { it.sheet_id !in successIds }

                        val successCount = successIds.size

                        selectedReports = emptySet()
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

        deleteErrorMessage?.let { message ->
            DeleteExpenseErrorDialog(
                reason = message,
                onDismiss = { deleteErrorMessage = null }
            )
        }

        if (showNoReportDialog) {
            NoReportDialog(
                isLoading = false,
                onCancel = { showNoReportDialog = false }
            )
        }

        if (showPaymentSheet) {
            PaymentTypeBottomSheet(
                onDismiss = { showPaymentSheet = false },
                onSelectCompany = {
                    showPaymentSheet = false
                    navController.navigate("CreateReportScreen?type=company")
                },
                onSelectEmployee = {
                    showPaymentSheet = false
                    navController.navigate("CreateReportScreen?type=employee")
                }
            )
        }

        if (showFilterSheet) {

            ExpensesFilterBottomSheet(

                tempFromDate = null,
                tempToDate = null,
                selectedStatuses = tempSelectedStatuses,
                onFromDateClick = {},
                onToDateClick = {},
                onStatusChange = { updatedSet ->
                    tempSelectedStatuses = updatedSet
                },
                onReset = { tempSelectedStatuses = emptySet() },
                onApply = {
                    selectedStatuses = tempSelectedStatuses
                    showFilterSheet = false
                },
                onDismiss = { showFilterSheet = false },
                onDateReset = {},
                onStatusReset = {
                    tempSelectedStatuses = emptySet()
                    selectedStatuses = emptySet()
                },
                expenses = false
            )
        }

//        if (isLoading || isLoadingReports) {
//            Box(
//                modifier = Modifier
//                    .clickable(enabled = false) {}
//            ) {
//                FullLoading()
//            }
//        }
    }
}