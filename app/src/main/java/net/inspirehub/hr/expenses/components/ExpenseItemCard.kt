package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlinx.coroutines.launch
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.submitExpense
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.AttachFileIcon
import net.inspirehub.hr.CheckIcon
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.SendIcon

data class ExpenseItem(
    val id: Int,
    val description: String,
    val totalAmount: String,
    val date: String,
    val status: String,
    val taxesAmount: Int?,
    val currencySymbol: String?,
    val currencyPosition: String?,
    val hasAttachments: Boolean = false
)

@Composable
fun ExpenseItemCard(
    expense: ExpenseItem,
    navController: NavController,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onSendSuccess: () -> Unit,
    isDimmed: Boolean,
    is17Version: Boolean,
    isClickable: Boolean = true,
    isAddMode: Boolean = false
) {


    fun formatDate(input: String): String {
        return try {
            val parts = input.split("-") // yyyy-MM-dd

            val year = parts[0]
            val month = parts[1].toInt().toString()
            val day = parts[2].toInt().toString()

            "$day-$month-$year"
        } catch (_: Exception) {
            input
        }
    }

    val colors = appColors()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val showSend = !isDimmed
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable) {
                if (showSend) {
                    navController.navigate("EditExpenseScreen/${expense.id}")
                } else {
                    showDialog = true
                }
            },
        colors = CardDefaults.cardColors(
            containerColor =
                if (isClickable) colors.surfaceContainerHigh
                else colors.surfaceDim
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = expense.description,
                    fontWeight = FontWeight.Bold,
                    color = if (isDimmed)
                        colors.onBackgroundColor.copy(alpha = 0.7f)
                    else
                        colors.onBackgroundColor,
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )

                if (expense.hasAttachments) {
                    AttachFileIcon()
                }

                if (isSelectionMode) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onSelect() },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(
                                    color = if (isSelected)
                                        colors.tertiaryColor
                                    else
                                        colors.transparent,
                                    shape = RoundedCornerShape(50)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected)
                                        colors.tertiaryColor
                                    else
                                        colors.onBackgroundColor,
                                    shape = RoundedCornerShape(50)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                CheckIcon()
                            }
                        }
                    }
                }

                if (isAddMode) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = { onSelect() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.tertiaryColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            stringResource(R.string.add),
                            color = colors.onSecondaryColor,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = buildAnnotatedString {
                    append(formatNumber(expense.totalAmount, currentLanguage))
                    expense.taxesAmount?.toDouble()?.takeIf { it != 0.0 }?.let { tax ->
                        val formattedTax = when (expense.currencyPosition) {
                            "before" -> "${expense.currencySymbol ?: ""} $tax"
                            "after" -> "$tax ${expense.currencySymbol ?: ""}"
                            else -> "$tax ${expense.currencySymbol ?: ""}"
                        }
                        append(
                            " ${stringResource(R.string.and_taxes)} ${
                                formatNumber(
                                    formattedTax,
                                    currentLanguage
                                )
                            }"
                        )
                    }
                },
                color = colors.onBackgroundColor.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("${stringResource(R.string.you_added_this_expense_on)} ")

                        withStyle(
                            style = SpanStyle(
                                color =
                                    if (isDimmed)
                                        colors.onBackgroundColor.copy(alpha = 0.7f)
                                    else
                                        colors.tertiaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(formatNumber(formatDate(expense.date), currentLanguage))
                        }

                        append(" ${stringResource(R.string.and_its_status_is)} ")

                        withStyle(
                            style = SpanStyle(
                                color = if (isDimmed)
                                    colors.onBackgroundColor.copy(alpha = 0.7f)
                                else
                                    colors.tertiaryColor,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(expense.status)
                        }
                    },
                    color = colors.onBackgroundColor.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.weight(1f)
                )

                if (!is17Version) {
                    SendIcon(
                        enabled = showSend,
                        onClick =  {
                            coroutineScope.launch {
                                val sharedPref = SharedPrefManager(context)
                                val token = sharedPref.getToken()

                                val result = submitExpense(
                                    context = context,
                                    token = token ?: "",
                                    expenseId = expense.id
                                )

                                if (result.status == "success") {
                                    onSendSuccess()
                                } else {
                                    errorMessage = result.message
                                }
                                println("Submit result: ${result.message}")
                            }
                        }
                    )
                }
            }
        }
    }

    errorMessage?.let { message ->
        MyDialog(
            onConfirm = { errorMessage = null },
            onDismiss = { errorMessage = null },
            title = stringResource(R.string.error_dialog),
            subtitle = message,
            confirmButtonText = stringResource(R.string.ok)
        )
    }

    if (showDialog) {
        MyDialog(
            onConfirm = { showDialog = false },
            onDismiss = { showDialog = false },
            title = stringResource(R.string.cannot_edit),
            subtitle = stringResource(R.string.this_expense_cannot_be_edited_because_it_is_status_is ,  expense.status),
            confirmButtonText = stringResource(R.string.ok)
        )
    }
}