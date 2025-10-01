package com.example.myapplicationnewtest.protection.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.protection.data.EnterPinViewModel
import kotlin.system.exitProcess
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection


@Composable
fun EnterPinScreen(
    navController: NavController,
    isChangingMethod: Boolean = false
) {
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val viewModel = remember { EnterPinViewModel(sharedPrefManager) }
    val currentFocusIndex by remember { mutableIntStateOf(0) }
    val pinColor = MaterialTheme.colorScheme.tertiary
    val focusManager = LocalFocusManager.current
    val customColors = TextSelectionColors(
        handleColor = pinColor,
        backgroundColor = pinColor
    )

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }


    val titleText =
        if (isChangingMethod)
            stringResource(R.string.confirm_your_pin_to_change_protection)
        else
            stringResource(R.string.enter_your_pin)


    val subtitleText =
        if (isChangingMethod)
            stringResource(R.string.to_continue_confirm_your_identity)
        else
            stringResource(
                R.string.enter_your_4digit_pin_to_continue
            )

    BackHandler(enabled = true) {
        if (isChangingMethod) {
            navController.navigate("ProtectionScreen") {
                popUpTo("EnterPinScreen") { inclusive = true }
            }
        } else {
            exitProcess(0)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Text(
//                titleText,
//                textAlign = TextAlign.Center,
//                lineHeight = 30.sp,
//                fontSize = 30.sp,
//                color = pinColor,
//                fontWeight = FontWeight.Bold
//            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.Top,

                ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    tint = pinColor
                )

                Text(
                    text = titleText,
                    fontWeight = FontWeight.Bold,
                    color = pinColor,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 32.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                subtitleText,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = pinColor,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 30.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CompositionLocalProvider(LocalTextSelectionColors provides customColors , LocalLayoutDirection provides LayoutDirection.Ltr) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (i in 0 until viewModel.pinLength) {

                    val isFocused = i == currentFocusIndex
                    val isFilled = viewModel.pinDigits[i].isNotEmpty()
                    val borderColor = when {
                        isFocused -> pinColor
                        isFilled -> pinColor
                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                    }

                        OutlinedTextField(
                            visualTransformation = PasswordVisualTransformation(),
                            value = viewModel.pinDigits[i],
                            onValueChange = {
                                if (it.isEmpty()) {
                                    viewModel.clearDigit(i)
                                    if (i > 0) focusManager.moveFocus(FocusDirection.Previous)
                                } else {
                                    viewModel.onPinDigitChanged(i, it)
                                    if (i < viewModel.pinLength - 1) {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp)
                                .then(
                                    if (i == 0) Modifier.focusRequester(focusRequester) else Modifier
                                ),
                            singleLine = true,
                            textStyle = TextStyle(
                                fontSize = 30.sp,
                                color = pinColor,
                                textAlign = TextAlign.Center,
                                textDirection = TextDirection.Ltr
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = borderColor,
                                focusedBorderColor = pinColor,
                                cursorColor = pinColor,
                                focusedTextColor = pinColor,
                                unfocusedTextColor = pinColor
                            )
                        )
                    }
                }
            }

            if (viewModel.error.isNotEmpty()) {
                val errorText = when (viewModel.error) {
                    "error_incorrect_pin" -> stringResource(R.string.incorrect_pin)
                    else -> ""
                }

                if (errorText.isNotEmpty()) {
                    Text(
                        text = errorText,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 20.dp)
                            .align(Alignment.Start)
                    )
                }
            }


            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                enabled = viewModel.isPinComplete,
                colors = ButtonDefaults.buttonColors(
                    containerColor = pinColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    viewModel.checkPin(
                        onSuccess = {
                            if (isChangingMethod) {
                                sharedPrefManager.savePin("")
                                sharedPrefManager.setProtectionSkipped(false)
                                navController.navigate("ProtectionScreen") {
                                    popUpTo("EnterPinScreen") { inclusive = true }
                                }
                            } else {
                                navController.navigate("CheckInOutScreen") {
                                    popUpTo("EnterPinScreen") { inclusive = true }
                                }
                            }
                        },
                        onFailure = {}
                    )
                }
            ) {
                Text(
                    stringResource(R.string.submit),
                    fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            if (!isChangingMethod)
                TextButton(onClick = {
                    navController.navigate("PinCodeScreen")
                }) {
                    Text(stringResource(R.string.forget_your_password), color = pinColor)
                }
        }
    }
}
