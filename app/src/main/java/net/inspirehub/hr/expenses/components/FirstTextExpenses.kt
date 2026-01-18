package net.inspirehub.hr.expenses.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun TextFirstExpenses(
    label: String
){
    val colors = appColors()
    Text(
        text = label ,
        fontSize = 25.sp,
        color = colors.onBackgroundColor
    )
}