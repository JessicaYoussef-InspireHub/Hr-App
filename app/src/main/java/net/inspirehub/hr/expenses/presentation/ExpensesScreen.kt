package net.inspirehub.hr.expenses.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.AnalyticDistribution
import net.inspirehub.hr.expenses.components.ExpenseDate
import net.inspirehub.hr.expenses.components.CategoryDropdown
import net.inspirehub.hr.expenses.components.DescriptionInputExpenses
import net.inspirehub.hr.expenses.components.IncludedTaxes
import net.inspirehub.hr.expenses.components.Notes
import net.inspirehub.hr.expenses.components.PaidBy
import net.inspirehub.hr.expenses.components.SaveCancelButton
import net.inspirehub.hr.expenses.components.TextFirstExpenses
import net.inspirehub.hr.expenses.components.TotalPriceExpenses

@RequiresApi(Build.VERSION_CODES.O)
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
        bottomBar = {
            Column {
                SaveCancelButton(
                    onCancel = {},
                    onConfirm = {}
                )
                BottomBar(navController = navController)
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFirstExpenses(stringResource(R.string.description))
                DescriptionInputExpenses()
            }

            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFirstExpenses(stringResource(R.string.category))
                Spacer(modifier = Modifier.width(10.dp))
                CategoryDropdown()
            }
            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFirstExpenses(stringResource(R.string.expense_date))
                Spacer(modifier = Modifier.width(10.dp))
                ExpenseDate()
            }
            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFirstExpenses(stringResource(R.string.total))
                Spacer(modifier = Modifier.width(10.dp))
                TotalPriceExpenses()
            }
            Spacer(modifier = Modifier.height(25.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFirstExpenses(stringResource(R.string.analytic_distribution))
                Spacer(modifier = Modifier.width(10.dp))
                AnalyticDistribution()
            }
            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFirstExpenses(stringResource(R.string.included_taxes))
                Spacer(modifier = Modifier.width(10.dp))
                IncludedTaxes()
            }
            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFirstExpenses(stringResource(R.string.paid_by))
                Spacer(modifier = Modifier.width(10.dp))
                PaidBy()
            }
            Spacer(modifier = Modifier.height(25.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextFirstExpenses(stringResource(R.string.notes))
                Spacer(modifier = Modifier.width(10.dp))
                Notes()
            }
            Spacer(modifier = Modifier.height(25.dp))

        }
    }
}