package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown() {

    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }

    val colors = appColors()

    val items = listOf(
        "[BNS] Bonus",
        "[CER] Certificate",
        "[COMACS] Computer Accessories",
        "[COMM] Communication",
        "[FOOD] Meals",
        "[GIFT] Gifts",
        "[HOSEXP] Hospital Expenses",
        "[LEGF] Legal Fees"
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {

        TextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    "choose the category",
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
            items.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                    },
                    onClick = {
                        selectedItem = option
                        expanded = false
                    }
                )
            }
        }
    }
}