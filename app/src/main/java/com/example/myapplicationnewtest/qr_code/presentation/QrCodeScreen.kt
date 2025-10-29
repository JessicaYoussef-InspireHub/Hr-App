package com.example.myapplicationnewtest.qr_code.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.appColors
import com.example.myapplicationnewtest.qr_code.data.QrCodeViewModel

@Composable
fun QRCodeScreen(viewModel: QrCodeViewModel = viewModel()) {

    val qrBitmap = viewModel.qrBitmap
    val colors = appColors()

    Column(
        modifier = Modifier
            .background(colors.onSecondaryColor)
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.generateQR() },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.tertiaryColor,
                contentColor = colors.onSecondaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(stringResource(R.string.generate_qr))
        }
        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            )
        }
    }
}
