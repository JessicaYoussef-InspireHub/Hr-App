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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
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


data class ExpenseItem(
    val id: Int,
    val description: String,
    val totalAmount: String,
    val date: String,
    val status: String,
    val taxesAmount: Int?,
    val currencySymbol: String?,
    val currencyPosition: String?
)

@Composable
fun ExpenseItemCard(
    expense: ExpenseItem,
    navController: NavController,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onSendSuccess: () -> Unit
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
    val isDraft = expense.status.equals("draft", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (expense.status.equals("draft", ignoreCase = true)) {
                    navController.navigate("EditExpenseScreen/${expense.id}")
                } else {
                    showDialog = true
                }
            },
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceContainerHigh
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
                    color = colors.onBackgroundColor,
                    fontSize = 18.sp
                )

                if (isSelectionMode) {
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
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Select",
                                    tint = colors.onSecondaryColor,
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = buildAnnotatedString {
                    append(expense.totalAmount)
                    expense.taxesAmount?.toDouble()?.takeIf { it != 0.0 }?.let { tax ->
                        val formattedTax = when (expense.currencyPosition) {
                            "before" -> "${expense.currencySymbol ?: ""} $tax"
                            "after" -> "$tax ${expense.currencySymbol ?: ""}"
                            else -> "$tax ${expense.currencySymbol ?: ""}"
                        }
                        append(" ${stringResource(R.string.and_taxes)} $formattedTax")
                    }
                },
                color = colors.onBackgroundColor,
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
                                color = colors.tertiaryColor
                            )
                        ) {
                            append(formatDate(expense.date))
                        }

                        append(" ${stringResource(R.string.and_its_status_is)} ")

                        withStyle(
                            style = SpanStyle(
                                color = colors.tertiaryColor
                            )
                        ) {
                            append(expense.status)
                        }
                    },
                    color = colors.onBackgroundColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isDraft)
                        Icons.AutoMirrored.Filled.Send
                    else
                        Icons.Default.Check,
                    contentDescription = "Send",
                    modifier = Modifier
                        .size(22.dp)
                        .rotate(if (isDraft) -30f else 0f)
                        .clickable(enabled = isDraft) {
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
                        },
                    tint = colors.tertiaryColor
                )
            }
        }
    }

    errorMessage?.let { message ->
        ErrorSubmittedDialog(
            message = message,
            onDismiss = { errorMessage = null }
        )
    }

    if (showDialog) {
        CannotEditDialog(
            onDismiss = { showDialog = false }
        )
    }
}