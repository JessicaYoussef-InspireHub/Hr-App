package com.example.myapplicationnewtest.sign_in.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.text.font.FontWeight


@Composable
fun InputFields(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.tertiary,
        backgroundColor = MaterialTheme.colorScheme.tertiary
    )

    val focusManager = LocalFocusManager.current



    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    label ,
                    color = MaterialTheme.colorScheme.tertiary ,
                    fontWeight = FontWeight.Normal) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = {
                    focusManager.moveFocus(FocusDirection.Down)
                    onImeAction()
                },
                onDone = {
                    focusManager.clearFocus()
                    onImeAction()
                }
            ),
            colors = TextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.tertiary,
                focusedContainerColor = MaterialTheme.colorScheme.onPrimary,
                disabledLabelColor = MaterialTheme.colorScheme.onPrimary,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                unfocusedContainerColor =MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.tertiary,
                unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                disabledTextColor = MaterialTheme.colorScheme.tertiary,
                focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                disabledIndicatorColor = MaterialTheme.colorScheme.tertiary,

                )
        )
    }
}
