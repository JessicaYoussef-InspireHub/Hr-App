package com.example.myapplicationnewtest.sign_in.presentation

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
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.sign_in.components.InputFields
import com.example.myapplicationnewtest.sign_in.components.SignInButton
import com.example.myapplicationnewtest.sign_in.data.SignInViewModel
import com.example.myapplicationnewtest.sign_in.data.SignInUiState
import androidx.compose.ui.text.input.ImeAction
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.stringResource
import com.example.myapplicationnewtest.R
import kotlin.system.exitProcess


@Composable
fun SignInScreen(
    navController: NavController,
    companyId: String,
    apiKey: String,
    viewModel: SignInViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val sharedPrefManager = SharedPrefManager(LocalContext.current)
    val protectionSkipped = sharedPrefManager.isProtectionSkipped()

    val isFormValid = emailState.value.isNotBlank() && passwordState.value.isNotBlank()


    val errorMessage = if (uiState is SignInUiState.Error) {
        stringResource(R.string.email_or_password_is_incorrect)
    } else {
        null
    }

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
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
          if (token.isNullOrEmpty()) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(40.dp)
                    .clickable { navController.popBackStack() },
                tint = MaterialTheme.colorScheme.tertiary
            )}
            Text(
                stringResource(R.string.sign_in_screen),
                fontSize = 30.sp,
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "Company ID: $companyId",
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            "API Key: $apiKey",
            color = MaterialTheme.colorScheme.tertiary
        )
        Spacer(modifier = Modifier.height(20.dp))

        InputFields(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = stringResource(R.string.email) ,
            imeAction = ImeAction.Next,
            onImeAction = {
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
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
            }
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(top = 4.dp)
            )
        }

        if (uiState is SignInUiState.Loading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(32.dp)
            )
        }


        Spacer(modifier = Modifier.weight(1f))

        SignInButton(
            onClick = {
                viewModel.signIn(
                    emailState.value,
                    passwordState.value,
                    companyId,
                    apiKey
                )
//
//                val middleware = com.example.myapplicationnewtest.scan_qr_code.data.Middleware.initialize(str)
//                middleware.apiKey
//                 middleware.companyId
//                middleware.baseUrl
            },
            enabled = isFormValid
        )



        when (uiState) {
            is SignInUiState.Error -> Text("${stringResource(R.string.error)} ${(uiState as SignInUiState.Error).message}")
            is SignInUiState.Success -> {
                Text(stringResource(R.string.login_successful))

                val response = (uiState as SignInUiState.Success).response
                val employeeData = response.result.message.employee_data
                val address = response.result.message.company.firstOrNull()?.address

                val latitude = address?.latitude ?: 0.0
                val longitude = address?.longitude ?: 0.0
                val allowedDistance = address?.allowed_distance ?: 0.0

                sharedPrefManager.saveToken(employeeData.employee_token)
                sharedPrefManager.saveTokenExpiry(employeeData.token_expiry)
                sharedPrefManager.saveCompanyId(companyId)
                sharedPrefManager.saveApiKey(apiKey)
                sharedPrefManager.saveLatitude(latitude)
                sharedPrefManager.saveLongitude(longitude)
                sharedPrefManager.saveAllowedDistance(allowedDistance)


                LaunchedEffect(Unit) {
                if(protectionSkipped){
                    navController.navigate("CheckInOutScreen")
                }else{
                    navController.navigate("ProtectionScreen") {
                        popUpTo("ScanQrCodeScreen") { inclusive = true }
                    }}
                    viewModel.resetState()
                }

//                sharedPrefManager.saveToken(token)
//                sharedPrefManager.saveCompanyId(companyId)
//                sharedPrefManager.saveApiKey(apiKey)
//                sharedPrefManager.saveLatitude(latitude)
//                sharedPrefManager.saveLongitude(longitude)
//                sharedPrefManager.saveAllowedDistance(allowedDistance)

            }

            else -> {}
        }
    }
}
