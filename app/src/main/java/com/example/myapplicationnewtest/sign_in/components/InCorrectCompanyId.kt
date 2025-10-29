package com.example.myapplicationnewtest.sign_in.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.appColors

@Composable
fun InCorrectCompanyId(
    message: String,
    onDismiss: () -> Unit,
    navController: NavController
) {
    val colors = appColors()

    AlertDialog(
        containerColor = colors.surfaceVariant,
        onDismissRequest = onDismiss,
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        onDismiss()
                        navController.navigate("ScanQrCodeScreen")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.tertiaryColor,
                        contentColor = colors.onSecondaryColor
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text =  stringResource(R.string.scan_qr_code_again),
                        color = colors.onSecondaryColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        title = {
            Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = colors.tertiaryColor,
                    modifier = Modifier
                        .clickable { onDismiss() }
                )
            }
            Text(
                text = stringResource(R.string.attention),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = colors.tertiaryColor,
            )
        } },
        text = {
            Text(
                message ,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = colors.onBackgroundColor,
            )
        }
    )
}
