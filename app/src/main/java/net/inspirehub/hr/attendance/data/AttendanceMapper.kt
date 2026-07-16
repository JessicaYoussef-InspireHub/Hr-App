package net.inspirehub.hr.attendance.data

import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import net.inspirehub.hr.attendance.presentation.AttendanceDay
import net.inspirehub.hr.attendance.presentation.AttendanceState

private fun String.toMinutes(): Int {

    // 2026-06-14 07:45:35
    val time = substringAfter(" ")
    val parts = time.split(":")
    return parts[0].toInt() * 60 + parts[1].toInt()
}

fun AttendanceResponse.toAttendanceDays(): List<AttendanceDay> {

    return data

        // It will aggregate all records for the same day.
        .groupBy {
            it.check_in?.substringBefore(" ") ?: ""
        }

        .map { (date, records) ->
            println("DATE = $date")

            records.forEach {
                println("isLate = ${it.is_late}")
            }

            AttendanceDay(
                date = date,
                states = records.map { record ->
                    val endMinutes = when (val value = record.check_out) {
                        null -> null
                        is JsonPrimitive -> {
                            when {
                                value.booleanOrNull == false -> null

                                value.contentOrNull != null ->
                                    value.content.toMinutes()

                                else -> null
                            }
                        }
                        else -> null
                    }

                    AttendanceState(
                        startMinutes = record.check_in?.toMinutes() ?: 0,
                        endMinutes = endMinutes,
                        isLate = record.is_late,
                        workedHours = record.worked_hours,
                        delayMinutes = record.delay_minutes,
                        expectedHours = record.expected_hours,
                        delay = record.delay
                    )
                }
            )
        }

        .sortedByDescending { it.date }
}