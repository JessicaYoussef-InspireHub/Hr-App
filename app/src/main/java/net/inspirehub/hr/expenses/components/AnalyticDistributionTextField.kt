package net.inspirehub.hr.expenses.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SmallLoading
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticDistributionTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String = "",
    showIcon: Boolean,
    loading: Boolean = false,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    dropdownItems: List<String> = emptyList(),
) {
    var expanded by remember { mutableStateOf(false) }
    val colors = appColors()
    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.tertiaryColor,
        backgroundColor = colors.onSecondaryColor
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                if (showIcon) {
                    expanded = !expanded
                }
            },
            modifier = modifier
        ) {
            TextField(
                value = value,
                onValueChange = { input ->
                    val filtered = input.filter { it.isDigit() }
                    onValueChange(filtered)
                },
                readOnly = showIcon,
                placeholder = {
                    Text(
                        text = placeholderText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackgroundColor
                    )
                },

                trailingIcon = {
                    if (showIcon) {
                        if (loading) {
                            SmallLoading()
                        } else {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "ArrowDropDown",
                                tint = colors.onBackgroundColor,
                                modifier = Modifier
                                    .size(28.dp)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .menuAnchor()
                    .clickable(enabled = showIcon) {
                        expanded = true
                    },
                colors = TextFieldDefaults.colors(
                    cursorColor = colors.tertiaryColor,
                    focusedContainerColor = colors.transparent,
                    unfocusedContainerColor = colors.transparent,
                    focusedIndicatorColor = colors.transparent,
                    unfocusedIndicatorColor = colors.transparent,
                    focusedTextColor = colors.onBackgroundColor,
                    unfocusedTextColor = colors.onBackgroundColor
                ),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onBackgroundColor
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                )
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.onSecondaryColor)
            ) {
                if (dropdownItems.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.no_analytic_distribution_available),
                                fontSize = 15.sp,
                                color = colors.onBackgroundColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        onClick = { expanded = false }
                    )
                } else {
                    dropdownItems.forEach { item ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    item,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                onValueChange(item)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}