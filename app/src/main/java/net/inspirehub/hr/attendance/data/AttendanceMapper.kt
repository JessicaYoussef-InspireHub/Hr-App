package net.inspirehub.hr.attendance.data

import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.AttendanceState

private fun String?.toMinutes(): Int {

    if (this.isNullOrBlank() || this == "0")
        return 0

    val dateTime = split(" ")

    if (dateTime.size < 2)
        return 0

    val time = dateTime[1]

    val parts = time.split(":")

    if (parts.size < 2)
        return 0

    return parts[0].toIntOrNull()?.times(60)
        ?.plus(parts[1].toIntOrNull() ?: 0)
        ?: 0
}

fun AttendanceResponse.toAttendanceDays(): List<AttendanceDay> {

    return data
        .sortedByDescending { it.date }
        .map { day ->

            AttendanceDay(
                date = day.date,

                hasPermission = day.entries.any {
                    it.work_entry_type == "Permissions"
                },

                states = day.entries.map { entry ->

                    val typeInfo = summary[entry.work_entry_type]

                    AttendanceState(
                        startMinutes = entry.from_date.toMinutes(),

                        endMinutes = entry.to_date?.toMinutes(),

                        workedHoursAndMinutes = entry.duration_time,
                        workedHoursPercentage = entry.duration,

                        workEntryType = entry.work_entry_type,

                        workEntryTypeColorHex =
                            typeInfo?.work_entry_type_color_hex,

                        iconId = typeInfo?.id_icon,
                    )
                }
            )
        }
}