package net.inspirehub.hr.qr_code.data

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class QrCodeViewModel : ViewModel() {

    var qrBitmap by mutableStateOf<Bitmap?>(null)

    fun generateQR() {
        val companyId = "Com0001"
        val apiKey = "d4ymOMvFHFx6kR6CjK7zC0fV"
//        val urlInspireHub = "https://alialaashawky-androidapp1.odoo.com"

        val textToEncode = """
            companyId = "$companyId"
            ApiKey = "$apiKey"
        """.trimIndent()

        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(textToEncode, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height

            val qrColor = Color.parseColor("#085C90")
            val backgroundColor = Color.WHITE

            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp.setPixel(
                        x,
                        y,
                        if (bitMatrix[x, y]) qrColor else backgroundColor
                    )
                }
            }
            qrBitmap = bmp
        } catch (e: Exception){
            e.printStackTrace()
        }
    }
}