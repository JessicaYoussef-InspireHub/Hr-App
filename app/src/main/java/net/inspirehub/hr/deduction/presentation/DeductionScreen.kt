package net.inspirehub.hr.deduction.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.deduction.components.DeductionStatusCard
import net.inspirehub.hr.deduction.data.EmployeesDeductionData
import net.inspirehub.hr.deduction.data.fetchEmployeesDeduction
import net.inspirehub.hr.deduction.components.MonthSummaryCard
import net.inspirehub.hr.deduction.components.PreviousMonthCard
import java.text.SimpleDateFormat
import java.util.Calendar.DAY_OF_MONTH
import java.util.Calendar.getInstance
import java.util.Locale

@Composable
fun DeductionScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    val colors = appColors()
    var deductionData by remember { mutableStateOf<EmployeesDeductionData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }


    LaunchedEffect(Unit) {
        isLoading = true

        val token = sharedPref.getToken()
        val calendar = getInstance()

        // First day of the current month
        calendar.set(DAY_OF_MONTH, 1)
        val fromDate = SimpleDateFormat(
            "yyyy-MM-dd", Locale.US
        ).format(calendar.time)

        // Last day of the current month
        calendar.set(DAY_OF_MONTH, calendar.getActualMaximum(DAY_OF_MONTH))
        val toDate = SimpleDateFormat(
            "yyyy-MM-dd", Locale.US
        ).format(calendar.time)

        val result = fetchEmployeesDeduction(
            token = token,
            context = context,
            fromDate = fromDate,
            toDate = toDate
        )

        deductionData = result?.data
        isLoading = false
    }

    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.deduction),
                onBackClick = { navController.popBackStack() },
                onMenuClick = onMenuClick
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (isLoading) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    FullLoading()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val data = deductionData

                    if (data != null) {
                        val requestedPeriod = data.requested_period

                        DeductionStatusCard(
                            usedMinutes = requestedPeriod.total_deduction_minutes,
                            allowedMinutes = requestedPeriod.allowed_duration_per_month,
                            deductionPercentage = requestedPeriod.deduction_percentage,
                            deductionColor = requestedPeriod.deduction_color,
                            deductionMessage = requestedPeriod.deduction_message
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        MonthSummaryCard(
                            usedMinutes = requestedPeriod.total_deduction_minutes,
                            allowedMinutes = requestedPeriod.allowed_duration_per_month,
                            remainingMinutes = requestedPeriod.remaining_deduction_time,
                            excessMinutes = requestedPeriod.excess_deduction_minutes
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PreviousMonthCard(
                            deductionColor = data.last_month.deduction_color,
                            deductionMessage = data.last_month.deduction_message
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                    } else {
                        Text(
                            text = stringResource(R.string.no_deduction_found),
                            color = colors.tertiaryColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}