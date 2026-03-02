package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class ExpenseItem(
    val title: String,
    val amount: String,
    val date: String
)
@Composable
fun ExpenseItemCard(expense: ExpenseItem) {


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = expense.title,
                fontWeight = FontWeight.Bold
            )

            Text(text = expense.amount)

            Text(text = expense.date)
        }
    }
}