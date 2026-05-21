package net.inspirehub.hr.utils

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.documentfile.provider.DocumentFile

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