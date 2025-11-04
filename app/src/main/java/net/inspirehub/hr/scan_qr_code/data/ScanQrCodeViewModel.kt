package net.inspirehub.hr.scan_qr_code.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ScanQrCodeViewModel : ViewModel() {
    private var scannedText by mutableStateOf("")

    fun updateScannedText(text: String) {
        scannedText = text
    }
}
