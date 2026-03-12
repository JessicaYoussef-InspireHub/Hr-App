package net.inspirehub.hr.expenses.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.ExpenseCurrency
import net.inspirehub.hr.expenses.data.fetchExpenseCurrencies
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotalPriceExpenses(
    token: String,
    context: Context,
    onAmountChange: (Double) -> Unit,
    onConvertedAmountChange: (Double?) -> Unit
) {

    val colors = appColors()
    var amount by remember { mutableStateOf("") }
    var currencyExpanded by remember { mutableStateOf(false) }
    var selectedCurrency by remember { mutableStateOf<ExpenseCurrency?>(null) }
    var currencies by remember { mutableStateOf(listOf<ExpenseCurrency>()) }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.tertiaryColor,
        backgroundColor = colors.onBackgroundColor
    )

    LaunchedEffect(Unit) {
        currencies = fetchExpenseCurrencies(context, token)
    }

    val amountDouble = amount.toDoubleOrNull()
    val convertedAmount = selectedCurrency
        ?.takeIf { !it.is_company_currency }
        ?.let { amountDouble?.times(it.rate) }

    LaunchedEffect(amountDouble, selectedCurrency) {

        if (selectedCurrency == null) {
            onConvertedAmountChange(null)
            return@LaunchedEffect
        }

        if (selectedCurrency!!.is_company_currency) {
            onConvertedAmountChange(null)
        } else {
            val converted =
                amountDouble?.times(selectedCurrency!!.rate)

            onConvertedAmountChange(converted)
        }
    }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                TextField(
                    value = if (selectedCurrency != null && amount.isNotEmpty()) {
                        if (selectedCurrency!!.position == "before") {
                            "${selectedCurrency!!.symbol} $amount"
                        } else {
                            "$amount ${selectedCurrency!!.symbol}"
                        }
                    } else {
                        amount
                    },
                    onValueChange = { input ->
                        val numericInput = input.filter { it.isDigit() || it == '.' }
                        if (numericInput.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                            amount = numericInput
                            val doubleAmount = amount.toDoubleOrNull() ?: 0.0
                            onAmountChange(doubleAmount)
                        }
                    },
                    placeholder = {
                        Text(
                            "0.00",
                            color = colors.onBackgroundColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    modifier = Modifier.weight(0.6f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.transparent,
                        unfocusedContainerColor = colors.transparent,
                        disabledContainerColor = colors.transparent,
                        cursorColor = colors.tertiaryColor,
                        focusedTextColor = colors.onBackgroundColor,
                        unfocusedTextColor = colors.onBackgroundColor,
                        focusedIndicatorColor = colors.tertiaryColor,
                        unfocusedIndicatorColor = colors.tertiaryColor,
                        disabledIndicatorColor = colors.transparent
                    ),
                    textStyle = TextStyle(
                        color = colors.tertiaryColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp
                    ),
                )
            }

            Spacer(modifier = Modifier.width(10.dp))


            ExposedDropdownMenuBox(
                expanded = currencyExpanded,
                onExpandedChange = { currencyExpanded = !currencyExpanded },
                modifier = Modifier.weight(1f)
            ) {
                TextField(
                    value = selectedCurrency?.currency_code ?: "",
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    textStyle = TextStyle(
                        color = colors.tertiaryColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    placeholder = {
                        Text(
                            stringResource(R.string.choose_the_currency),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackgroundColor,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = colors.onBackgroundColor,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.transparent,
                        unfocusedContainerColor = colors.transparent,
                        disabledContainerColor = colors.transparent,
                        cursorColor = colors.tertiaryColor,
                        focusedTextColor = colors.onBackgroundColor,
                        unfocusedTextColor = colors.onBackgroundColor,
                        focusedIndicatorColor = colors.tertiaryColor,
                        unfocusedIndicatorColor = colors.tertiaryColor,
                        disabledIndicatorColor = colors.transparent
                    )
                )
                ExposedDropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false },
                    modifier = Modifier.background(colors.surfaceContainerHigh)
                ) {
                    if (currencies.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.no_currencies_available)) },
                            onClick = { currencyExpanded = false }
                        )
                    } else {
                        currencies.filter { it.active }.forEach { currency ->
                            DropdownMenuItem(
                                text = {
                                    Text(currency.currency_code)
                                },
                                onClick = {
                                    selectedCurrency = currency
                                    currencyExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
        if (selectedCurrency != null && !selectedCurrency!!.is_company_currency) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {


                val rounding = selectedCurrency!!.rounding
                val roundedAmount = (convertedAmount?.div(rounding))?.roundToInt()?.times(rounding)

                val companyCurrency = currencies.find { it.is_company_currency }
                val companySymbol = companyCurrency?.symbol
                val companyPosition = companyCurrency?.position
                val decimalPlaces = companyCurrency?.decimal_places
                val formatString = "%.${decimalPlaces}f"

                val convertedText = if (companyPosition == "before") {
                    "$companySymbol ${formatString.format(roundedAmount)}"
                } else {
                    "${formatString.format(roundedAmount)} $companySymbol"
                }

                Text(
                    text = convertedText,
                    color = colors.onBackgroundColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 15.dp)
                        .weight(0.6f)
                )

                Text(
                    text = "1 ${selectedCurrency!!.name} = ${selectedCurrency!!.rate} ${companyCurrency?.currency_code}",
                    color = colors.onBackgroundColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 8.dp)
                        .weight(1f)
                )
            }
        }
    }
}