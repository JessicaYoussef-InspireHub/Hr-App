package net.inspirehub.hr.time_off.components

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.time_off.data.LeaveType
import net.inspirehub.hr.utils.formatNumber

@Composable
fun MyActualTimeOff(
    leaveTypes: List<LeaveType>
) {

    val visibleLeaveTypes = leaveTypes.filter { it.requires_allocation == "yes" }
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val currentLanguage = sharedPref.getLanguage()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(visibleLeaveTypes) { leave ->

            val cardColor =
                if (leave.color.isNullOrBlank()) {
                    colors.tertiaryColor
                } else {
                    try {
                        Color(leave.color.toColorInt())
                    } catch (e: IllegalArgumentException) {
                        Log.e("LeaveBalanceCard", "Invalid color: ${leave.color}", e)
                        colors.tertiaryColor
                    }
                }


            val balance = leave.remaining_balance ?: 0f

            val balanceText =
                if (balance % 1f == 0f) {
                    balance.toInt().toString()
                } else {
                    balance.toString()
                }

            val remainingLeaveType = formatNumber(balanceText, currentLanguage)


            Card(
                modifier = Modifier.fillParentMaxWidth(.48f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    2.dp,
                    cardColor.copy(alpha = 0.25f)
                ),
                colors = CardDefaults.cardColors(
                    containerColor = colors.transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            cardColor.copy(alpha = 0.15f)
                        )
                        .padding(vertical = 15.dp)
                ) {

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = leave.name,
                            color = colors.onBackgroundColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = remainingLeaveType,
                            color = cardColor,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text =
                                if (leave.request_unit == "day" || leave.request_unit == "half_day") {
                                    stringResource(R.string.days_available)
                                } else {
                                    stringResource(R.string.hours_available)
                                },
                            color = cardColor.copy(alpha = 0.75f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}