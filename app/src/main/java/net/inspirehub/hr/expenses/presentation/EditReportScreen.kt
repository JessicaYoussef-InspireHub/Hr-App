package net.inspirehub.hr.expenses.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.SaveCancelButton


@Composable
fun EditReportScreen(
    navController: NavController,
    reportId: Int,
){
    val colors = appColors()
    var isLoading by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ){
        Scaffold(
            containerColor = colors.onSecondaryColor,
            snackbarHost = {},
            topBar = {
                MyAppBar(
                    label = stringResource(R.string.edit_expense),
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            },
            bottomBar = {
                Column {
                    SaveCancelButton(
                        stringResource(R.string.update),
                        isLoading = isLoading,
                        onCancel = {
                            navController.navigate("ExpensesScreen")
                        },
                        onConfirm = {},
                    )
                    BottomBar(navController = navController)
                }
            }
        )
        { innerPadding ->
            Column (
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ){

            }

        }
          if (isLoading) {
            Box(
                modifier = Modifier
                    .clickable(enabled = false) {}
            ) {
                FullLoading()
            }
        }
    }

}