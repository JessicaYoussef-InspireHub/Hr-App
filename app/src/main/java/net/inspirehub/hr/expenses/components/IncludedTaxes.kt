package net.inspirehub.hr.expenses.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.Tax

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncludedTaxes(
    taxes: List<Tax>,
    selectedTaxes: List<Tax>,
    onTaxesChange: (List<Tax>) -> Unit,
    amount: Double,
    currencySymbol: String,
    currencyPosition: String
) {

    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val allowedIds = sharedPref.getAllowedLocationsIds()

    val colors = appColors()

    val taxAmount = remember(selectedTaxes, amount) {
        selectedTaxes.sumOf { tax ->
            when (tax.amount_type.lowercase()) {
                "percent" -> amount * tax.amount / 100
                "fixed" -> tax.amount
                else -> 0.0
            }
        }
    }



    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                selectedTaxes.forEach { tax ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = colors.surfaceContainerHigh,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = tax.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onBackgroundColor,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(end = 24.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Tax",
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.TopEnd)
                                .clickable {
                                    onTaxesChange(selectedTaxes - tax)
                                },
                            tint = colors.tertiaryColor
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                TextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    placeholder = {
                        if (selectedTaxes.isEmpty()) {
                            Text(
                                stringResource(R.string.included_taxes_small),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colors.onBackgroundColor
                            )
                        }
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "ArrowDropDown",
                            tint = colors.onBackgroundColor,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = colors.transparent,
                        unfocusedContainerColor = colors.transparent,

                        focusedIndicatorColor = colors.tertiaryColor,
                        unfocusedIndicatorColor = colors.tertiaryColor,
                        disabledIndicatorColor = colors.tertiaryColor,

                        disabledTextColor = colors.onBackgroundColor,
                        focusedTextColor = colors.onBackgroundColor,
                        unfocusedTextColor = colors.onBackgroundColor
                    ),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.tertiaryColor
                    ),
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(colors.surfaceContainerHigh)
                ) {
                    val filteredTaxes = taxes.filter { allowedIds.contains(it.company_id) }

                    if (filteredTaxes.isEmpty()) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    stringResource(R.string.no_taxes_available),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,

                                    )
                            },
                            onClick = { expanded = false }
                        )

                    } else {
                        filteredTaxes.forEach { tax ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        tax.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                },
                                onClick = {
                                    onTaxesChange(
                                        if (selectedTaxes.contains(tax)) {
                                            selectedTaxes - tax
                                        } else {
                                            selectedTaxes + tax
                                        }
                                    )
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = if (currencyPosition == "before") {
                "$currencySymbol ${"%.2f".format(taxAmount)}"
            } else {
                "${"%.2f".format(taxAmount)} $currencySymbol"
            },
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (selectedTaxes.isEmpty()) colors.onBackgroundColor else colors.tertiaryColor
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.transparent,
                unfocusedContainerColor = colors.transparent,

                focusedIndicatorColor = colors.tertiaryColor,
                unfocusedIndicatorColor = colors.tertiaryColor,

                focusedTextColor = if (selectedTaxes.isEmpty()) colors.onBackgroundColor else colors.tertiaryColor,
                unfocusedTextColor = if (selectedTaxes.isEmpty()) colors.onBackgroundColor else colors.tertiaryColor
            )
        )
    }
}