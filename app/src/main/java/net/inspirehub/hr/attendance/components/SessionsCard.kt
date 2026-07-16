package net.inspirehub.hr.attendance.components


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.formatNumber
import net.inspirehub.hr.utils.getPercentage
import net.inspirehub.hr.utils.toUi

@Composable
fun SessionsCard(
    day: AttendanceDay
) {
    val colors = appColors()
    val status = getDayStatus(day)
    val statusUi = status.toUi()
    val totalSessions = day.states.size
    val percentage = status.getPercentage(day.states)
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPref.getLanguage()


    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = colors.inverseSurface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = stringResource(R.string.attendance_sessions),
                color = colors.onBackgroundColor,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(statusUi.color.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(
                            2.dp,
                            color = statusUi.color,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(top = 16.dp , bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = formatNumber(totalSessions.toString(), currentLanguage),
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusUi.color,
                        )

                        Text(
                            text = stringResource(R.string.total_sessions),
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            color = colors.onBackgroundColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(statusUi.color.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(
                            2.dp,
                            color = statusUi.color,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(top = 16.dp , bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = statusUi.icon,
                                contentDescription = stringResource(R.string.hours_worked),
                                tint = statusUi.color,
                                modifier = Modifier.size(30.dp)

                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = statusUi.text,
                                fontSize = 18.sp,
                                color = statusUi.color
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = if (currentLanguage == "ar") {
                                "٪${formatNumber(percentage.toString(), currentLanguage)}"
                            } else {
                                "${formatNumber(percentage.toString(), currentLanguage)}%"
                            },
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusUi.color,
                        )
                    }
                }
            }
        }
    }
}