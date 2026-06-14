package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.CloseIcon
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.R
import net.inspirehub.hr.SmallButtons
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.Expense

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReportBottomSheet(
    showBottomSheet: Boolean,
    isBottomSheetLoading: Boolean,
    removedExpenses: List<Expense>,
    availableExpenses: List<Expense>,
    onDismiss: () -> Unit,
    onRestoreExpense: (Expense) -> Unit,
    onAddExpense: (Expense) -> Unit,
    navController: NavController,
    reportId: Int,
    paymentMode: String,
    editScreen: Boolean
) {

    val colors = appColors()
    val removedIds = removedExpenses.map { it.id }.toSet()
    val availableFiltered = availableExpenses.filter { it.id !in removedIds }
    val selectedIds = remember { mutableStateListOf<Int>() }
    val requiredPaymentMode = paymentMode

    if (!showBottomSheet) return

    ModalBottomSheet(
        containerColor = colors.surfaceContainerHigh,
        windowInsets = WindowInsets(0),
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                CloseIcon (
                    onClick = { onDismiss() }
                )
            }

            Text(
                text = stringResource(R.string.add_more_expenses),
                color = colors.tertiaryColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (isBottomSheetLoading) {
                FullLoading()
            } else {
                val maxHeightDp = 200.dp

                if (removedExpenses.isEmpty() && availableFiltered.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_expenses_yet),
                            color = colors.tertiaryColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 0.dp, max = maxHeightDp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {

                            if (removedExpenses.isNotEmpty()) {

                                items(removedExpenses) { expense ->

                                    ExpenseItemCard(
                                        expense = ExpenseItem(
                                            id = expense.id,
                                            description = expense.name,
                                            totalAmount = expense.total_amount.toString(),
                                            date = expense.date,
                                            status = expense.state,
                                            taxesAmount = expense.tax_amount?.toInt(),
                                            currencySymbol = expense.currency,
                                            currencyPosition = expense.currency_position
                                        ),
                                        navController = navController,
                                        isSelectionMode = false,
                                        isAddMode = true,
                                        isSelected = false,
                                        isDimmed = false,
                                        is17Version = true,
                                        onSelect = {
                                            onRestoreExpense(expense)
                                        },
                                        onSendSuccess = {},
                                        isClickable = false
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }

                            items(availableFiltered) { expense ->

                                ExpenseItemCard(
                                    expense = ExpenseItem(
                                        id = expense.id,
                                        description = expense.name,
                                        totalAmount = expense.total_amount.toString(),
                                        date = expense.date,
                                        status = expense.state,
                                        taxesAmount = expense.tax_amount?.toInt(),
                                        currencySymbol = expense.currency,
                                        currencyPosition = expense.currency_position
                                    ),

                                    navController = navController,

                                    isSelectionMode = false,
                                    isAddMode = true,

                                    isSelected = expense.id in selectedIds,

                                    onSelect = {
                                        if (expense.id in selectedIds) {
                                            selectedIds.remove(expense.id)
                                        } else {
                                            selectedIds.add(expense.id)
                                        }
                                        onAddExpense(expense)
                                    },

                                    onSendSuccess = {},

                                    isDimmed = false,
                                    is17Version = true,
                                    isClickable = false
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SmallButtons(
                onConfirm = {
                    onDismiss()
                    if (editScreen){
                        navController.navigate("AddExpensesScreen?source=edit_report&reportId=$reportId&paymentMode=$requiredPaymentMode")
                    }
                    else
                        navController.navigate("AddExpensesScreen?source=create_report&reportId=$reportId&paymentMode=$requiredPaymentMode")
                },
                onDismiss = { onDismiss() },
                confirmButtonText = stringResource(R.string.new_expense),
                dismissButtonText = stringResource(R.string.discard),
                isLoading = false,
                modifier = Modifier.padding(
                    vertical = 16.dp , horizontal = 5.dp
                ),
                equalWeight = true

            )
        }
    }
}