package net.inspirehub.hr.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun convertToArabicDigits(input: String): String {
    val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return input.map {
        if (it.isDigit()) arabicDigits[it.digitToInt()] else it
    }.joinToString("")
}

fun formatNumber(text: String, language: String): String {
    return if (language == "ar") {
        convertToArabicDigits(text)
    } else {
        text
    }
}


fun uriToBase64(context: Context, uri: Uri): String {
    val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

fun getFileName(context: Context, uri: Uri): String {
    return DocumentFile.fromSingleUri(context, uri)?.name ?: "file"
}

fun getMimeType(context: Context, uri: Uri): String {
    return context.contentResolver.getType(uri) ?: "application/octet-stream"
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatLocalizedDate(
    date: String,
    language: String
): String {

    val localDate = LocalDate.parse(date)
    val locale = Locale.forLanguageTag(language)

    val day = formatNumber(localDate.dayOfMonth.toString(), language)
    val month = localDate.month.getDisplayName(
        TextStyle.FULL,
        locale
    )
    val year = formatNumber(localDate.year.toString(), language)

    return "$day $month $year"
}


fun getLocalizedWorkedTime(
    hours: Int,
    minutes: Int,
    language: String
): String {

    return if (language == "ar") {

        when {
            hours > 0 && minutes > 0 ->
                "${formatNumber(hours.toString(), language)} ساعة ${formatNumber(minutes.toString(), language)} دقيقة"

            hours > 0 ->
                "${formatNumber(hours.toString(), language)} ساعة"

            else ->
                "${formatNumber(minutes.toString(), language)} دقيقة"
        }

    } else {

        when {
            hours > 0 && minutes > 0 ->
                "${hours}h ${minutes}m"

            hours > 0 ->
                "$hours h"

            else ->
                "$minutes m"
        }
    }
}


fun getLocalizedHourText(
    count: Double?,
    language: String
): String {

    if (count == null) return ""

    return if (language == "ar") {
        when {
            count == 0.5 -> "نصف ساعة"
            count == 1.0 -> "ساعة"
            count == 1.5 -> "ساعة ونصف"
            count == 2.0 -> "ساعتين"
            count == 2.5 -> "ساعتين ونصف"
            count in 3.0..10.0 && count % 1 == 0.0 ->
                "${formatNumber(count.toInt().toString(), language)} ساعات"

            count > 10 && count % 1 == 0.0 ->
                "${formatNumber(count.toInt().toString(), language)} ساعة"

            count % 1 == 0.5 ->
                "${formatNumber(count.toInt().toString(), language)} ساعة ونصف"

            else ->
                "${formatNumber(count.toString(), language)} ساعة"
        }
    } else {
        when {
            count == 0.5 -> "Half an hour"
            count == 1.0 -> "1 hour"
            count == 1.5 -> "1 hour and a half"
            count == 2.0 -> "2 hours"
            count == 2.5 -> "2 hours and a half"
            count % 1 == 0.0 -> "${count.toInt()} hours"
            count % 1 == 0.5 -> "${count.toInt()} and a half hours"
            else -> "$count hours"
        }
    }
}

fun getLocalizedMinuteText(
    count: Int,
    language: String
): String {
    return if (language == "ar") {
        when (count) {
            0 -> "دقيقة"
            1 -> "دقيقة"
            2 -> "دقيقتان"
            in 3..10 -> "دقائق"
            else -> "دقيقة"
        }
    } else {
        if (count == 1) "minute" else "minutes"
    }
}

fun formatLocalizedTime(
    hour: Int,
    minute: Int,
    language: String
): String {

    val isPm = hour >= 12

    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    val amPm = if (language == "ar") {
        if (isPm) "م" else "ص"
    } else {
        if (isPm) "PM" else "AM"
    }

    return "${formatNumber(hour12.toString(), language)}:${
        formatNumber("%02d".format(minute), language)
    } $amPm"
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatLocalizedDateRange(
    startDate: LocalDate,
    endDate: LocalDate,
    language: String
): String {

    val locale = Locale.forLanguageTag(language)

    val startDay = formatNumber(startDate.dayOfMonth.toString(), language)
    val endDay = formatNumber(endDate.dayOfMonth.toString(), language)

    val startMonth = startDate.month.getDisplayName(
        TextStyle.SHORT,
        locale
    )

    val endMonth = endDate.month.getDisplayName(
        TextStyle.SHORT,
        locale
    )

    val year = formatNumber(endDate.year.toString(), language)

    return if (startDate.month == endDate.month) {
        "$startDay - $endDay $endMonth $year"
    } else {
        "$startDay $startMonth - $endDay $endMonth $year"
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun formatLocalizedShortDate(
    date: LocalDate?,
    language: String
): String {

    if (date == null) return ""

    val day = formatNumber(date.dayOfMonth.toString(), language)
    val month = formatNumber(date.monthValue.toString(), language)
    val year = formatNumber(date.year.toString(), language)

    return "$day/$month/$year"
}

fun getLocalizedDayText(
    count: Double?,
    language: String
): String {

    if (count == null) return ""

    return if (language == "ar") {
        when {
            count == 0.5 -> "نصف يوم"
            count == 1.0 -> "يوم"
            count == 2.0 -> "يومان"
            count % 1 == 0.5 ->
                "${formatNumber(count.toInt().toString(), language)} يوم ونصف"

            count in 3.0..10.0 && count % 1 == 0.0 ->
                "${formatNumber(count.toInt().toString(), language)} أيام"

            count > 10 && count % 1 == 0.0 ->
                "${formatNumber(count.toInt().toString(), language)} يوم"

            else ->
                "${formatNumber(count.toString(), language)} يوم"
        }
    } else {
        when {
            count == 0.5 -> "Half day"
            count == 1.0 -> "1 day"
            count == 2.0 -> "2 days"
            count % 1 == 0.5 -> "${count.toInt()} and a half days"
            count % 1 == 0.0 -> "${count.toInt()} days"
            else -> "$count days"
        }
    }
}