package net.inspirehub.hr.expenses.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.expenses.components.ReportCard
import net.inspirehub.hr.expenses.data.ExpenseReport
import net.inspirehub.hr.expenses.data.fetchReports
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import net.inspirehub.hr.utils.convertToArabicDigits

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyReportScreen(
    navController: NavController,
) {
    var reports by remember { mutableStateOf<List<ExpenseReport>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { net.inspirehub.hr.SharedPrefManager(context) }
    val token = sharedPref.getToken().orEmpty()
    var isSelectionMode by remember { mutableStateOf(false) }
    var selectedReports by remember { mutableStateOf(setOf<Int>()) }


    LaunchedEffect(Unit) {
        isLoading = true
        reports = fetchReports(context, token)
        isLoading = false
    }

    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = @Composable {
            if (!isSelectionMode) {
            MyAppBar(
                label = stringResource(R.string.my_reports),
                onBackClick = {
                    navController.popBackStack()
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.Checklist,
                        contentDescription = "Select",
                        tint = colors.onSecondaryColor,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(horizontal = 8.dp)
                            .clickable { isSelectionMode = !isSelectionMode }
                    )
                }
            )
        } else {
                TopAppBar(
                    title = {
                        Text(
                            text = convertToArabicDigits(
                                stringResource(R.string.item_selected, selectedReports.size)),
                            color = colors.onBackgroundColor
                        )
                    },
                    actions = {
                        Text(
                            text = if (selectedReports.size == reports.size)
                                stringResource(R.string.unselect_all)
                            else
                                stringResource(R.string.select_all),
                            color = colors.tertiaryColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    selectedReports =
                                        if (selectedReports.size == reports.size)
                                            emptySet()
                                        else
                                            reports.map { it.sheet_id }.toSet()
                                }
                        )
                        Text(
                            text = stringResource(R.string.delete),
                            color = if (selectedReports.isEmpty())
                                colors.onBackgroundColor.copy(alpha = 0.4f)
                            else
                                colors.error,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable(enabled = selectedReports.isNotEmpty()) {}
                        )

                        Text(
                            text = stringResource(R.string.cancel),
                            color = colors.tertiaryColor,
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    isSelectionMode = false
                                    selectedReports = emptySet()
                                }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.surfaceContainerHigh
                    )
                )
            }
        },
        bottomBar = { BottomBar(navController = navController) },
    ) { innerPadding ->
        when {
            isLoading -> {
                FullLoading()
            }

            reports.isEmpty() -> {
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
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    items(reports) { report ->

                        ReportCard(
                            report = report,
                            isSelectionMode = isSelectionMode,
                            isSelected = selectedReports.contains(report.sheet_id),
                            onSelect = {
                                selectedReports = if (selectedReports.contains(report.sheet_id)) {
                                    selectedReports - report.sheet_id
                                } else {
                                    selectedReports + report.sheet_id
                                }
                            },
                            navController = navController
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
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