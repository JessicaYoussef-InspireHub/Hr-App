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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncludedTaxes(){

    var expanded by remember { mutableStateOf(false) }
    var selectedTaxes by remember { mutableStateOf("") }

    val colors = appColors()

    val taxes = listOf(
        "14% Tax - Expenses Purchase",
        "0% Tax - Purchase",
        "Foreigner - Purchase",
        "1% Tax With-holding - Purchase",
        "3% Tax With-holding - Purchase",
        "5% Tax With-holding - Purchase",
        "14% Tax Hardware Purchase",
        "14% Tax Assets Purchase"
    )


    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            TextField(
                value = selectedTaxes,
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
                taxes.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        },
                        onClick = {
                            selectedTaxes = option
                            expanded = false
                        }
                    )
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