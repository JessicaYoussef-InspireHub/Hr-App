package com.example.myapplicationnewtest.scan_qr_code.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.navigation.NavController
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.appColors
import com.example.myapplicationnewtest.scan_qr_code.data.Middleware
import com.example.myapplicationnewtest.scan_qr_code.data.ScanQrCodeViewModel
import com.example.myapplicationnewtest.sign_in.components.InputFields
import kotlin.system.exitProcess

@Composable
fun ScanQrCodeScreen(
    viewModel: ScanQrCodeViewModel,
    navController: NavController
) {

    var str = "voev4Jd6hmDBb4cvsAFEfrE+UX6SQa7BmhuZuotjz6PUvqmODNWU/8zDAZsY6xGiq+2Ed1QX0osvp5926CnIqkuYmZxEyTusnv9Gq/BWzQYtoO7sWvNfSjhkwWQqf4YG"
//    val str = remember { mutableStateOf("") }
    val middleware = Middleware.initialize(str)

    val context = LocalContext.current



    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents != null) {
            viewModel.updateScannedText(result.contents)

            val lines = result.contents.lines()
            val companyId =
                lines.getOrNull(0)?.substringAfter("=")?.trim()?.removeSurrounding("\"") ?: ""
            val apiKey =
                lines.getOrNull(1)?.substringAfter("=")?.trim()?.removeSurrounding("\"") ?: ""

            // ✅ Go to the SignIn page and send them each one separately
            navController.navigate("SignInScreen/${companyId}/${apiKey}/1")
        }
    }

    BackHandler(enabled = true) {
        exitProcess(0)
    }
    val colors = appColors()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(colors.onSecondaryColor)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.tertiaryColor,
                contentColor = colors.onSecondaryColor
            ),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                scanLauncher.launch(ScanOptions().apply {
                    setPrompt(context.getString(R.string.scan_a_qr_code))
                    setBeepEnabled(true)
                    setOrientationLocked(false)
                })
            }) {
            Text(stringResource(R.string.scan_a_qr_code))
        }
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            stringResource(R.string.or) ,
            color = colors.tertiaryColor,
            fontWeight = FontWeight.ExtraBold ,
            fontSize = 30.sp)
        Spacer(modifier = Modifier.height(30.dp))

        InputFields(
            value = str,
            onValueChange = { str = it },
            label = stringResource(R.string.enter_your_company_information),
            imeAction = ImeAction.Done,
            onImeAction = {
//                middleware.apiKey
//                middleware.companyId
//                middleware.baseUrl
//               println("test"+middleware.toString())
//               println("test"+middleware.apiKey)
               navController.navigate("SignInScreen/${middleware.companyId}/${middleware.apiKey}/1")
            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.tertiaryColor,
                contentColor = colors.onSecondaryColor
            ),
            shape = RoundedCornerShape(8.dp),
            onClick = {
                navController.navigate("SignInScreen/${middleware.companyId}/${middleware.apiKey}/1")
            }) {
            Text(stringResource(R.string.done))
        }




//        Spacer(modifier = Modifier.height(30.dp))
//        Button(
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.tertiary,
//                contentColor = MaterialTheme.colorScheme.onPrimary
//            ),
//            shape = RoundedCornerShape(8.dp),
//            onClick = {
////                navController.navigate("CheckInOutScreen/eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbXBsb3llZV9pZCI6NSwiZW1haWwiOiJ0ZXN0IiwiZXhwIjoxNzUxOTY4MjY5fQ.pSRGRJhFqcIIT7I0pPw51PYI58xeNQBiJiFmFmqo8cs/27.191249/31.188578/100.0")
//                navController.navigate("CheckInOutScreen")
//            }) {
//            Text("Go To CheckIn Screen")
//        }
//        Spacer(modifier = Modifier.height(30.dp))
//
//        Button(
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.tertiary,
//                contentColor = MaterialTheme.colorScheme.onPrimary
//            ),
//            shape = RoundedCornerShape(8.dp),
//            onClick = {
//                navController.navigate("TimeOffScreen")
//            }) {
//            Text("Go To TimeOff Screen D4AF37")
//        }
//        Spacer(modifier = Modifier.height(30.dp))
//
//        Button(
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.tertiary,
//               contentColor = MaterialTheme.colorScheme.onPrimary
//            ),
//            shape = RoundedCornerShape(8.dp),
//            onClick = {
//
////                navController.navigate("ProtectionScreen/eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbXBsb3llZV9pZCI6NSwiZW1haWwiOiJ0ZXN0IiwiZXhwIjoxNzUxOTY4MjY5fQ.pSRGRJhFqcIIT7I0pPw51PYI58xeNQBiJiFmFmqo8cs/27.191249/31.188578/100.0")
//                navController.navigate("ProtectionScreen")
//            }) {
//            Text("Go To Protection Screen")
//        }
//
//        Spacer(modifier = Modifier.height(30.dp))
//
//        Button(
//            modifier = Modifier.fillMaxWidth(),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.tertiary,
//               contentColor = MaterialTheme.colorScheme.onPrimary
//            ),
//            shape = RoundedCornerShape(8.dp),
//            onClick = {
//
//                navController.navigate("SettingsScreen")
//            }) {
//            Text("Go To Settings Screen")
//        }


    }
}


