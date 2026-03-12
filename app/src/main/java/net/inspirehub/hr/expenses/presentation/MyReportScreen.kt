package net.inspirehub.hr.expenses.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.ExpenseItem

@Composable
fun MyReportScreen(
    navController: NavController
) {
    val reports = emptyList<ExpenseItem>()
    val colors = appColors()

    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.my_reports),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = { BottomBar(navController = navController) },
    ) { innerPadding ->
        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.no_reports_yet),
                    color = colors.onBackgroundColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {

        }
    }
}