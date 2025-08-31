package com.example.myapplicationnewtest.protection.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
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
import com.example.myapplicationnewtest.protection.data.PinCodeViewModel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalLayoutDirection


@Composable
fun PinCodeScreen(
    navController: NavController,
    viewModel: PinCodeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val pinDigits = viewModel.pinDigits
    val pinLength = viewModel.pinLength
    val focusManager = LocalFocusManager.current
    val currentFocusIndex by remember { mutableIntStateOf(0) }
    val pinColor = MaterialTheme.colorScheme.tertiary
    val customColors = TextSelectionColors(
        handleColor = pinColor,
        backgroundColor = pinColor
    )

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }


    BackHandler(enabled = true) {
        navController.navigate("ProtectionScreen") {
            popUpTo("PinCodeScreen") { inclusive = true }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    tint = MaterialTheme.colorScheme.tertiary
                )

                Text(
                    text = stringResource(R.string.choose_a_pin),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 32.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.use_4_digits_to_secure_your_account),
                fontSize = 20.sp,
                color = pinColor,
                fontWeight = FontWeight.Normal,
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
                for (i in 0 until pinLength) {

                    val isFocused = i == currentFocusIndex
                    val isFilled = pinDigits[i].isNotEmpty()
                    val borderColor = when {
                        isFocused -> pinColor
                        isFilled -> pinColor
                        else -> MaterialTheme.colorScheme.onTertiaryContainer
                    }


                        OutlinedTextField(
                            visualTransformation = PasswordVisualTransformation(),
                            value = pinDigits[i],
                            onValueChange = {
                                if (it.isEmpty()) {
                                    viewModel.clearDigit(i)
                                    if (i > 0) focusManager.moveFocus(FocusDirection.Previous)
                                } else if (it.length == 1 && it.all { c -> c.isDigit() }) {
                                    viewModel.updateDigit(i, it)
                                    if (i < pinLength - 1) focusManager.moveFocus(FocusDirection.Next)
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
                                unfocusedTextColor = pinColor,
                                focusedLabelColor = pinColor,
                                unfocusedLabelColor = pinColor
                            )
                        )
                    }
                }
            }

            if (viewModel.errorMessage.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = viewModel.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 20.dp)
                            .align(Alignment.CenterStart)
                    )
                }
            }


            Spacer(modifier = Modifier.height(32.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                enabled = viewModel.isPinComplete(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = pinColor,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp),
                onClick = {
                    if (viewModel.isPinComplete()) {
                        navController.navigate("confirm_pin/${viewModel.getPin()}")
                    }
                }) {
                Text(
                    stringResource(R.string.next),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


    }
}