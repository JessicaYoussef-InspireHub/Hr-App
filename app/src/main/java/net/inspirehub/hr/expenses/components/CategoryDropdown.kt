package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.data.ExpenseCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<ExpenseCategory>,
    description: String,
    onCategorySelected: (ExpenseCategory) -> Unit,
    onDescriptionChange: (String) -> Unit
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<ExpenseCategory?>(null) }
    val colors = appColors()


    Column {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {

            TextField(
                value = selectedItem?.let {
                    val code = it.default_code?.takeIf { code -> code.isNotBlank() }
                    if (code != null) "[$code]  ${it.name}" else it.name
                } ?: "",
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
                singleLine = false
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.surfaceContainerHigh)
            ) {
                if (categories.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No categories available") },
                        onClick = { expanded = false }
                    )
                } else {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = category.default_code?.takeIf { it.isNotBlank() }?.let { "[$it] ${category.name}" } ?: category.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            },
                            onClick = {
                                selectedItem = category
                                expanded = false
                                onCategorySelected(category)

                                if (description.isBlank()) {
                                    val defaultText = category.default_code?.takeIf { it.isNotBlank() }?.let { "[$it] ${category.name}" } ?: category.name
                                    onDescriptionChange(defaultText)
                                }
                            }
                        )
                    }
                }
            }
        }
        selectedItem?.description?.takeIf { it.isNotBlank() }?.let { description ->
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = description,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                color = colors.tertiaryColor,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}