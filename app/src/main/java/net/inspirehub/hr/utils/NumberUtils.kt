package net.inspirehub.hr.utils


fun convertToArabicDigits(input: String): String {
    val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return input.map {
        if (it.isDigit()) arabicDigits[it.digitToInt()] else it
    }.joinToString("")
}