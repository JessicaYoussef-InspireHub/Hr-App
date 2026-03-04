package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.Tax

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncludedTaxes(
    taxes: List<Tax>
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedTax by remember { mutableStateOf<Tax?>(null) }

    val colors = appColors()


    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            TextField(
                value = selectedTax?.name ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        "Included Taxes?",
                        fontSize = 15.sp,
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
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.surfaceContainerHigh)
            ) {
                if (taxes.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "No taxes available",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = { expanded = false }
                    )

                } else {
                    taxes.forEach { tax ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    tax.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            },
                            onClick = {
                                selectedTax = tax
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = "0.00 LE",
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackgroundColor
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.transparent,
                unfocusedContainerColor = colors.transparent,

                focusedIndicatorColor = colors.tertiaryColor,
                unfocusedIndicatorColor = colors.tertiaryColor,

                focusedTextColor = colors.onBackgroundColor,
                unfocusedTextColor = colors.onBackgroundColor
            )
        )
    }
}