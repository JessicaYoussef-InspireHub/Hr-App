package net.inspirehub.hr.attendance.data

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.AttendanceState

fun JsonElement?.asDateTime(): String? {

    val primitive = this as? JsonPrimitive ?: return null

    val value = primitive.contentOrNull ?: return null

    return if (value == "0") null else value
}


private fun String.toMinutes(): Int {

    if (isBlank() || this == "0")
        return 0

    val dateTime = split(" ")

    if (dateTime.size < 2)
        return 0

    val time = dateTime[1]

    val parts = time.split(":")

    if (parts.size < 2)
        return 0

    return parts[0].toInt() * 60 + parts[1].toInt()
}



fun AttendanceResponse.toAttendanceDays(): List<AttendanceDay> {

    return data.map { day ->

        AttendanceDay(
            date = day.date,
            hasPermission = day.entries.any {
                it.work_entry_type == "Permissions"
            },
            states = day.entries.map { entry ->

                AttendanceState(
                    startMinutes = entry.from_date.asDateTime()?.toMinutes() ?: 0,
                    endMinutes = entry.to_date.asDateTime()?.toMinutes(),
                    workedHoursAndMinutes = entry.duration_time,
                    workedHoursPercentage = entry.duration,
                    workEntryType = entry.work_entry_type
                )
            }
        )
    }
}