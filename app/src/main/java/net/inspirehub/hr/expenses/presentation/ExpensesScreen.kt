package net.inspirehub.hr.expenses.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.ExpenseItem
import net.inspirehub.hr.expenses.components.ExpenseItemCard
import net.inspirehub.hr.expenses.components.NewAndReportButton
import net.inspirehub.hr.expenses.components.NoReportDialog

@Composable
fun ExpensesScreen(
    navController: NavController
) {
    val colors = appColors()

    val expenses = emptyList<ExpenseItem>()
    var showNoReportDialog by remember { mutableStateOf(false) }


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
                NewAndReportButton(
                    onNewExpenses = {
                        navController.navigate("AddExpensesScreen")
                    },
                    onCreateReport = {
                        if (expenses.isEmpty()) {
                            showNoReportDialog = true
                        } else {
                        }
                    },
                    onUpload = {},
                    viewReport = {
                        navController.navigate("MyReportScreen")
                    }
                )
                BottomBar(navController = navController)
            }

        }
    ) { innerPadding ->
        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.no_expenses_yet),
                    color = colors.onBackgroundColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(16.dp),
            ) {
                items(expenses) {
                    ExpenseItemCard(it)
                }
            }
        }

        if (showNoReportDialog) {
            NoReportDialog(
                isLoading = false,
                onCancel = { showNoReportDialog = false }
            )
        }
    }
}