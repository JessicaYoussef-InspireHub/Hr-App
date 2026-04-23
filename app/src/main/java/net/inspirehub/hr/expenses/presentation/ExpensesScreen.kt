package net.inspirehub.hr.expenses.presentation

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import net.inspirehub.hr.expenses.components.PaymentTypeBottomSheet
import net.inspirehub.hr.expenses.components.SelectedDeleteConfirmationDialog
import net.inspirehub.hr.expenses.components.UploadBottomSheet
import net.inspirehub.hr.expenses.data.deleteExpense
import net.inspirehub.hr.expenses.data.fetchExpensesForReport

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
    val successMessage = { count: Int -> context.getString(R.string.deleted_successfully, count) }
    val failedMessage = { count: Int -> context.getString(R.string.could_not_be_deleted, count) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }
    var reportIds by remember { mutableStateOf(setOf<Int>()) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var is17Version by remember { mutableStateOf(true) }

    val imageFile = remember {
        java.io.File.createTempFile(
            "IMG_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        )
    }

    val cameraUri = androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        imageFile
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri = cameraUri
            println("Camera Image URI: $cameraImageUri")
        }
    }

    val cameraPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                cameraLauncher.launch(cameraUri)
            } else {
                println("Camera permission denied")
            }
        }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            println("Selected Image: $it")
        }
    }

    val filesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            println("Selected File: $it")
        }
    }

    LaunchedEffect(Unit) {
        isLoading = true
        val allExpenses = fetchExpenses(context = context, token = token)
        val reportExpenses = fetchExpensesForReport(context = context, token = token)
        println("Loaded ${expenses.size} expenses")

        is17Version = allExpenses.firstOrNull()?.is_17_version == false

        expenses = allExpenses
        reportIds = reportExpenses.map { it.id }.toSet()

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

                            scope.launch {
                                isLoading = true
                                val reportExpenses = fetchExpensesForReport(context, token)

                                isLoading = false

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

                                if (paymentTypes.size > 1) {
                                    showPaymentSheet = true
                                    return@launch
                                }

                                if (type != null) {
                                    navController.navigate("CreateReportScreen?type=$type")
                                } else {
                                    showPaymentSheet = true
                                }
                            }
                        },
                        onUpload = {
                            showUploadSheet = true
                        },
                        viewReport = {
                            navController.navigate("MyReportScreen")
                        },
                        is17Version = is17Version
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
                            val isDimmed = expense.id !in reportIds

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
                                },
                                isDimmed = isDimmed,
                                is17Version = is17Version
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }
        if (isLoading) {
            Box(
                modifier = Modifier
                    .clickable(enabled = false) {}
            ) {
                FullLoading()
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

        if (showUploadSheet) {
            UploadBottomSheet(
                onDismiss = { showUploadSheet = false },
                onCameraClick = { cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA) },
                onGalleryClick = { galleryLauncher.launch("image/*") },
                onFilesClick = { filesLauncher.launch(arrayOf("*/*")) }
            )
        }
    }
}