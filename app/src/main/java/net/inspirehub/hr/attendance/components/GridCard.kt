package net.inspirehub.hr.attendance.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.StatusCircleIcon
import net.inspirehub.hr.appColors
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.getDayStatus
import net.inspirehub.hr.utils.getLocalizedWorkedTime
import net.inspirehub.hr.utils.toComposeColor
import net.inspirehub.hr.utils.toUi

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GridCard(
    day: AttendanceDay,
    onClick: (AttendanceDay) -> Unit
) {

    val colors = appColors()

    val context = androidx.compose.ui.platform.LocalContext.current

    val sharedPref = SharedPrefManager(context)

    val currentLanguage = sharedPref.getLanguage()

    /*
     * Get all distinct work entry types.
     */
    val workEntryTypes = day.states
        .filter {
            it.workEntryType.isNotBlank()
        }
        .distinctBy {
            it.workEntryType
        }

    /*
     * First entry is used for the main icon.
     *
     * The icon is now coming from the API
     * using iconId -> iconImage.
     */
    val firstEntry = workEntryTypes.firstOrNull()

    val firstEntryColor = firstEntry
        ?.workEntryTypeColorHex
        ?.toComposeColor()
        ?: colors.tertiaryColor

    /*
     * API image for the first work entry.
     *
     * Example:
     *
     * Attendance -> id 1 -> icon_image
     * Early      -> id 18 -> icon_image
     */
    val firstEntryIconImage = firstEntry?.iconImage

    /*
     * Calculate worked time.
     */
    val workedMinutes = (
            day.states.sumOf {
                it.workedHoursPercentage
            } * 60
            ).toInt()

    val workedHours = workedMinutes / 60

    val remainingMinutes = workedMinutes % 60

    val workedText = getLocalizedWorkedTime(
        workedHours,
        remainingMinutes,
        currentLanguage
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clickable {
                onClick(day)
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceVariant
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            /*
             * Date
             */
            DateHeader(
                date = day.date
            )

            /*
             * Main work entry icon.
             *
             * The image comes from:
             *
             * /api/work-entry-types
             *
             * and is matched using:
             *
             * AttendanceState.iconId
             */
            if (!firstEntryIconImage.isNullOrBlank()) {

                WorkEntryTypeImage(
                    base64Image = firstEntryIconImage,
                    backgroundColor = firstEntryColor
                )

            } else {

                /*
                 * Fallback to the old icon
                 * if API image is missing.
                 */
                if (!firstEntryIconImage.isNullOrBlank()) {
                    WorkEntryTypeImage(
                        base64Image = firstEntryIconImage,
                        backgroundColor = firstEntryColor
                    )
                } else {
                    StatusCircleIcon(
                        imageVector = getDayStatus(day).toUi().icon,
                        tint = firstEntryColor
                    )
                }
            }

            /*
             * Work entry type badges.
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    workEntryTypes.forEach { entry ->

                        val entryColor = entry
                            .workEntryTypeColorHex
                            ?.toComposeColor()
                            ?: colors.tertiaryColor

                        StatusBadge(
                            text = entry.workEntryType,
                            color = entryColor,
                            roundedCorner = 50,
                            fontSize = 14,
                            modifier = Modifier.padding(
                                horizontal = 18.dp,
                                vertical = 2.dp
                            )
                        )
                    }
                }
            }

            /*
             * Worked hours.
             */
            WorkedHours(
                size = 12,
                workedText = workedText
            )

            /*
             * Progress.
             */
            Progress(
                attendanceStates = day.states
            )
        }
    }
}