package net.inspirehub.hr.expenses.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.TextFirstExpenses

@Composable
fun ExpensesScreen(
    navController: NavController
) {
    val colors = appColors()

    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.expenses),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = { BottomBar(navController = navController) }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            TextFirstExpenses(stringResource(R.string.description))
            TextFirstExpenses("Category")
            TextFirstExpenses("Total")
            TextFirstExpenses("Paid by")
            TextFirstExpenses("Notes:")
            TextFirstExpenses("Expense Date")
            TextFirstExpenses("Analytic Distribution")
        }
    }
}