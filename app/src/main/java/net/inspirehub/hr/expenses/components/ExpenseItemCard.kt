package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors


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
    navController: NavController
) {

    val colors = appColors()
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable {
                if (expense.status.equals("draft", ignoreCase = true)){
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
            Text(
                text = expense.description,
                fontWeight = FontWeight.Bold,
                color = colors.onBackgroundColor,
                fontSize = 18.sp
            )
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

            Text(
                text = buildAnnotatedString {
                    append("${stringResource(R.string.you_added_this_expense_on)} ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = colors.onBackgroundColor
                        )
                    ) {
                        append(expense.date)
                    }

                    append(" ${stringResource(R.string.and_its_status_is)} ")

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = colors.onBackgroundColor
                        )
                    ) {
                        append(expense.status)
                    }
                },
                color = colors.onBackgroundColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Light
            )
        }
    }

    if (showDialog) {
        CannotEditDialog(
            onDismiss = { showDialog = false }
        )
    }
}