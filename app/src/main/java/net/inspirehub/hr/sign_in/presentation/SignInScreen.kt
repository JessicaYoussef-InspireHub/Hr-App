package net.inspirehub.hr.sign_in.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.sign_in.components.InputFields
import net.inspirehub.hr.sign_in.components.SignInButton
import net.inspirehub.hr.sign_in.data.SignInViewModel
import net.inspirehub.hr.sign_in.data.SignInUiState
import androidx.compose.ui.text.input.ImeAction
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.sign_in.components.InCorrectCompanyIdDialog
import kotlin.system.exitProcess

//fun extractErrorMessage(response: String): String {
//    return try {
//        val json = JSONObject(response)
//        json.getJSONObject("result").getString("message")
//    } catch (e: Exception) {
//        "An unexpected error occurred"
//    }
//}


@Composable
fun SignInScreen(
    navController: NavController,
    companyId: String,
    apiKey: String,
    viewModel: SignInViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
    val colors = appColors()
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val sharedPrefManager = SharedPrefManager(LocalContext.current)
    val protectionSkipped = sharedPrefManager.isProtectionSkipped()

    val isFormValid = emailState.value.isNotBlank() && passwordState.value.isNotBlank()
    val showDialog = remember { mutableStateOf(false) }
    val dialogMessage = remember { mutableStateOf("") }

    val errorMessage: String? = if (uiState is SignInUiState.Error) {
        val msg = (uiState as SignInUiState.Error).message

        when {
            msg.contains("No company found", ignoreCase = true) -> {
                dialogMessage.value = stringResource(R.string.company_id_or_api_key_is_incorrect)
                showDialog.value = true
                null
            }

            msg.contains("Employee not found", ignoreCase = true) -> {
                stringResource(R.string.email_or_password_is_incorrect)
            }

            else -> {
                (uiState as SignInUiState.Error).message
//                "Another unexpected error occurred, try again later."
            }
        }
    } else null


    val token = sharedPrefManager.getToken()

    BackHandler(enabled = true) {
        if (token.isNullOrEmpty()) {
            navController.navigate("ScanQrCodeScreen") {
                popUpTo("SignInScreen") { inclusive = true }
            }
        } else {
            exitProcess(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colors.onSecondaryColor)
            .padding(horizontal = 16.dp)
            .padding(WindowInsets.navigationBars.asPaddingValues())
            .padding(WindowInsets.statusBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Box(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(32.dp)
                        .clickable {navController.navigate("ScanQrCodeScreen")},
                    tint = colors.tertiaryColor
                )
        }
        Image(
            painter = painterResource(id = R.drawable.sign_in),
            contentDescription = stringResource(R.string.sign_in),
            modifier = Modifier.size(130.dp)
                .statusBarsPadding()

        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            stringResource(R.string.sign_in_screen),
            fontSize = 30.sp,
            color = colors.tertiaryColor,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(40.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.email),
                color = colors.tertiaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        InputFields(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = stringResource(R.string.email),
            imeAction = ImeAction.Next,
            onImeAction = {
            }
        )
        Spacer(modifier = Modifier.height(25.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.password),
                color = colors.tertiaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        InputFields(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = stringResource(R.string.password),
            imeAction = ImeAction.Done,
            onImeAction = {
                if (isFormValid) {
                    viewModel.signIn(
                        emailState.value,
                        passwordState.value,
                        companyId,
                        apiKey
                    )
                }
            },
            isPassword = true
        )




        Spacer(modifier = Modifier.height(8.dp))
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = colors.error,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )
        }



        Spacer(modifier = Modifier.weight(1f))


        SignInButton(
            onClick = {
                val trimmedEmail = emailState.value.trim()
                val trimmedPassword = passwordState.value.trim()
                if (trimmedEmail.isNotEmpty() && trimmedPassword.isNotEmpty()) {
                    viewModel.signIn(
                        trimmedEmail,
                        trimmedPassword,
                        companyId,
                        apiKey
                            )}},
            enabled = isFormValid
        )
        if (showDialog.value) {
            InCorrectCompanyIdDialog(
                message = dialogMessage.value,
                onDismiss = {
                    showDialog.value = false
                    viewModel.resetState()
                },
                navController = navController
            )
        }



        when (uiState) {
            is SignInUiState.Success -> {
                Text(stringResource(R.string.login_successful))

                LaunchedEffect(Unit) {
                    if (protectionSkipped) {
                        navController.navigate("CheckInOutScreen")
                    } else {
                        navController.navigate("ProtectionScreen/0") {
                            popUpTo("ScanQrCodeScreen") { inclusive = true }
                        }
                    }
                    viewModel.resetState()
                }
            }
            else -> {}
        }
    }


    if (uiState is SignInUiState.Loading) {
        FullLoading()
    }
}