package net.inspirehub.hr.scan_qr_code.presentation

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.navigation.NavController
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.scan_qr_code.components.ErrorCompanyInformationDialog
import net.inspirehub.hr.scan_qr_code.data.AppConfig
import net.inspirehub.hr.scan_qr_code.data.Middleware
import net.inspirehub.hr.scan_qr_code.data.PortraitCaptureActivity
import net.inspirehub.hr.scan_qr_code.data.ScanQrCodeViewModel
import net.inspirehub.hr.sign_in.components.InputFields
import kotlin.system.exitProcess

@Composable
fun ScanQrCodeScreen(
    viewModel: ScanQrCodeViewModel,
    navController: NavController
) {

    var str by remember { mutableStateOf("") }
    var middleware by remember { mutableStateOf<Middleware?>(null) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }



    val context = LocalContext.current



    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents != null) {
            viewModel.updateScannedText(result.contents)


            try {
                val middleware = Middleware.initialize(result.contents)
                AppConfig.setBaseUrl(
                    middleware.baseUrl.replace("http://", "https://"),
                    context
                )

                navController.navigate("SignInScreen/${middleware.companyId}/${middleware.apiKey}")
            } catch (e: Exception) {
                println("ERROR: ${e.message}")
            }
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
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .padding(WindowInsets.statusBars.asPaddingValues()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Image(
            painter = painterResource(R.drawable.scan_qr_code),
            contentDescription = stringResource(R.string.scan_a_qr_code),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = colors.tertiaryColor,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    scanLauncher.launch(ScanOptions().apply {
                        setPrompt(context.getString(R.string.scan_a_qr_code))
                        setBeepEnabled(true)
                        setOrientationLocked(true)
                        setCaptureActivity(PortraitCaptureActivity::class.java)
                    })
                }
        )

        Spacer(modifier = Modifier.weight(1f))
        Text(
            stringResource(R.string.or) ,
            color = colors.tertiaryColor,
            fontWeight = FontWeight.SemiBold ,
            fontSize = 40.sp)
        Spacer(modifier = Modifier.weight(1f))

        Text("Enter your company information" ,
            color = colors.onBackgroundColor,
            fontWeight = FontWeight.Medium ,
            fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))

        InputFields(
            value = str,
            onValueChange = { str = it },
            label = stringResource(R.string.enter_your_company_information),
            imeAction = ImeAction.Done,
            onImeAction = {
                if (str.isBlank()) {
                    println("ERROR: Empty input")
                }
                try {
                    middleware = Middleware.initialize(str)

                    println("Full decryption result: $middleware")
                    println("Company ID: ${middleware!!.companyId}")
                    println("API Key: ${middleware!!.apiKey}")
                    println("Base URL: ${middleware!!.baseUrl}")

                    navController.navigate("SignInScreen/${middleware!!.companyId}/${middleware!!.apiKey}")
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Unknown decryption error."
                    showErrorDialog = true
                    println("ERROR: ${e.message}")
                }
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
                try {
                    middleware = Middleware.initialize(str)
                    println("Full decryption result: $middleware")
                    println("Company ID: ${middleware!!.companyId}")
                    println("API Key: ${middleware!!.apiKey}")
                    println("Base URL: ${middleware!!.baseUrl}")
//                    AppConfig.baseUrl = middleware!!.baseUrl

//                    AppConfig.baseUrl = middleware!!.baseUrl.replace("http://", "https://")

                    AppConfig.setBaseUrl(
                        middleware!!.baseUrl.replace("http://", "https://"),
                        context
                    )

                    navController.navigate("SignInScreen/${middleware!!.companyId}/${middleware!!.apiKey}")
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Invalid company information"
                    showErrorDialog = true
                    println("ERROR: ${e.message}")
                }
            }

        ) {
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
////                navController.navigate("CheckInOutScreen")
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
////                navController.navigate("ProtectionScreen")
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

        if (showErrorDialog) {
            ErrorCompanyInformationDialog(
                message = errorMessage,
                onDismiss = { showErrorDialog = false }
            )
        }

    }
}


