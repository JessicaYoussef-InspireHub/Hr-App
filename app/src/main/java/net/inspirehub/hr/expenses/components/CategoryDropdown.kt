package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
){
    var expanded by remember { mutableStateOf(false) }
    val colors = appColors()
    val items = listOf("[BNS] Bonus", "[CER] Certificate",
        "[COMACS] Computer Accessories", "[COMM] Communication" ,
        "[FOOD] Meals" , "[GIFT] Gifts" , "[HOSEXP] Hospital Expenses" ,
        "[LEGF] Legal Fees" )
    var selectedItem by remember { mutableStateOf<String?>(null) }

    Box {
        Row (
            modifier = Modifier.fillMaxWidth()
                .clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically

        ){
            Text(
                text = selectedItem?:  "choose the category",
                color = colors.onBackgroundColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "ArrowDropDown",
                tint = colors.onBackgroundColor,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    color = colors.surfaceContainerHigh,
                )
        ) {
            items.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            option ,
                            color = colors.onBackgroundColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold) },
                    onClick = {
                        selectedItem = option
                        expanded = false
                    }
                )
            }
        }
    }
}