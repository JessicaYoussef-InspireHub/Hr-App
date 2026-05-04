package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.Expense
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.utils.formatNumber

@Composable
fun ExpensesSelectionCard(
    expenses: List<Expense>,
    isLoading: Boolean,
    onRemove: (Expense) -> Unit
) {
    val colors = appColors()
    val expenseList = remember(expenses) {
        mutableStateListOf<Expense>().apply { addAll(expenses) }
    }
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPref.getLanguage()

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

    when {
        isLoading -> {
            FullLoading()
        }

        expenseList.isEmpty() -> {
            Text(
                stringResource(R.string.no_expenses_yet),
                color = colors.onBackgroundColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        else -> {
            Column {
                expenseList.forEach { expense ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
                                    text = expense.name,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colors.onBackgroundColor,
                                )
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.delete),
                                    tint = colors.tertiaryColor,
                                    modifier = Modifier.clickable {
                                        onRemove(expense)
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))


                            Text(
                                text = buildAnnotatedString {

                                    val baseAmount = expense.total_amount
                                    val currency = expense.currency_symbol ?: ""
                                    val position = expense.currency_position ?: "after"

                                    val formattedBaseAmount = formatNumber("%.2f".format(baseAmount) , currentLanguage)

                                    val formattedBase = when (position) {
                                        "before" -> "$currency $formattedBaseAmount"
                                        "after" -> "$formattedBaseAmount $currency"
                                        else -> "$formattedBaseAmount $currency"
                                    }

                                    append(formattedBase)

                                    val tax = expense.tax_amount?.minus(baseAmount)

                                    if (tax != null && tax != 0.0) {

                                        val formattedTaxAmount = formatNumber("%.2f".format(tax) , currentLanguage)

                                        val formattedTax = when (position) {
                                            "before" -> "$currency $formattedTaxAmount"
                                            "after" -> "$formattedTaxAmount $currency"
                                            else -> "$formattedTaxAmount $currency"
                                        }
                                        append(" ${stringResource(R.string.and_taxes)} $formattedTax")
                                    }
                                },
                                color = colors.onBackgroundColor.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = buildAnnotatedString {
                                    append("${stringResource(R.string.you_added_this_expense_on)} ")

                                    withStyle(
                                        style = SpanStyle(
                                            color = colors.tertiaryColor
                                        )
                                    ) {
                                        append(formatNumber(formatDate(expense.date) , currentLanguage))
                                    }

                                    append(" ${stringResource(R.string.and_its_status_is)} ")

                                    withStyle(
                                        style = SpanStyle(
                                            color = colors.tertiaryColor
                                        )
                                    ) {
                                        append(expense.state)
                                    }
                                },
                                color = colors.onBackgroundColor.copy(alpha = 0.7f),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}