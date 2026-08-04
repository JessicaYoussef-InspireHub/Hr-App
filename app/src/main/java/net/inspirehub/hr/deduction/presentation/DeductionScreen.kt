package net.inspirehub.hr.deduction.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import net.inspirehub.hr.deduction.components.DeductionStatusCard

@Composable
fun PreviousMonthCard(
    state: DeductionUiState
) {

    val noDeduction = state.previousMonthDeduction == 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xff7C3AED),
                            Color(0xff9F67FF)
                        )
                    )
                )
                .padding(20.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(.18f)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = Color.White
                    )

                }

                Spacer(Modifier.width(14.dp))

                Column {

                    Text(
                        "Previous Month Overview",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )

                    Text(
                        "Performance Summary",
                        color = Color.White.copy(.8f)
                    )

                }

            }

            Spacer(Modifier.height(24.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                if (noDeduction)
                                    Icons.Default.Celebration
                                else
                                    Icons.Default.WarningAmber,

                            contentDescription = null,

                            tint =
                                if (noDeduction)
                                    Color(0xff22C55E)
                                else
                                    Color(0xffEF4444)
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(
                            text =
                                if (noDeduction)
                                    "Great Job!"
                                else
                                    "Needs Improvement",

                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(

                        text =
                            if (noDeduction)
                                "You had no deductions last month.\n\nKeep it up and continue arriving on time!"
                            else
                                "Last month deductions reached ${state.previousMonthDeduction.toInt()} EGP.\n\nLet's aim for a better month!",

                        lineHeight = 24.sp,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MonthSummaryCard(
    state: DeductionUiState
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "This Month Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                SummaryItem(
                    modifier = Modifier.weight(1f),
                    title = "Allowed",
                    value = "${state.allowedMinutes}",
                    icon = Icons.Outlined.Schedule,
                    iconColor = Color(0xff22C55E),
                    background = Color(0xffECFDF3)
                )

                SummaryItem(
                    modifier = Modifier.weight(1f),
                    title = "Used",
                    value = "${state.usedMinutes}",
                    icon = Icons.Outlined.Timer,
                    iconColor = Color(0xffF97316),
                    background = Color(0xffFFF7ED)
                )

                SummaryItem(
                    modifier = Modifier.weight(1f),
                    title = "Remaining",
                    value = "${state.remainingMinutes}",
                    icon = Icons.Outlined.Update,
                    iconColor = Color(0xff2563EB),
                    background = Color(0xffEFF6FF)
                )

                SummaryItem(
                    modifier = Modifier.weight(1f),
                    title = "Deduction",
                    value = "${state.deductionAmount.toInt()} EGP",
                    icon = Icons.Outlined.AccountBalanceWallet,
                    iconColor = Color(0xff7C3AED),
                    background = Color(0xffF5F3FF)
                )

            }

        }

    }

}

@Composable
private fun SummaryItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    background: Color
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(background),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = iconColor
            )

        }
    }
}

enum class DeductionStatus {
    Safe,
    Warning,
    Exceeded
}

data class DeductionUiState(

    val allowedMinutes: Int,

    val usedMinutes: Int,

    val remainingMinutes: Int,

    val deductionAmount: Double,

    val previousMonthDeduction: Double,

    val status: DeductionStatus

)

@Composable
fun DeductionScreen(
    navController: NavController
){
    val colors = appColors()

    val state = DeductionUiState(
        allowedMinutes = 120,
        usedMinutes = 66,
        remainingMinutes = 54,
        deductionAmount = 0.0,
        previousMonthDeduction = 0.0,
        status = DeductionStatus.Safe
    )
    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.Deduction),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(vertical = 16.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

            DeductionStatusCard(state)

            MonthSummaryCard(state)

            PreviousMonthCard(state)
        }
    }
}