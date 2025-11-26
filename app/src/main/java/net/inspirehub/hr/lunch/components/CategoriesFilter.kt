package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun CategoriesFilter() {

    val colors = appColors()
    var expanded by remember { mutableStateOf(false) }
    val categories = listOf( "Chicken", "Pasta", "Pizza")
    var selectedCategory by remember { mutableStateOf(List(categories.size) { false }) }


    Box(
        modifier = Modifier
            .background(colors.surfaceContainerHigh, CircleShape)
            .clickable { expanded = true }
    ){
        Icon(
            imageVector = Icons.Default.Restaurant,
            contentDescription = "Categories",
            tint = colors.onBackgroundColor,
            modifier = Modifier
                .size(40.dp)
                .padding(8.dp)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colors.surfaceContainerHigh)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            ) {
                Text(
                    stringResource(R.string.categories),
                    color = colors.onBackgroundColor
                )
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.surfaceColor
                )
                categories.forEachIndexed { index, category ->
                    DropdownMenuItem(
                        text = {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                            ){
                                Checkbox(
                                    checked = selectedCategory[index],
                                    onCheckedChange = { isChecked ->
                                        selectedCategory = selectedCategory.toMutableList().also {
                                            it[index]= isChecked}
                                    },
                                    colors = CheckboxDefaults.colors(
                                        uncheckedColor = colors.onBackgroundColor,
                                        checkedColor = colors.onSecondaryColor
                                    )
                                )
                                Text(
                                    text = category,
                                    color = colors.onBackgroundColor
                                )
                            }
                        },
                        onClick = {
                            selectedCategory = selectedCategory.toMutableList().also {
                                it[index] = !it[index]
                            }
                        }
                    )
                }
            }
        }
    }
}