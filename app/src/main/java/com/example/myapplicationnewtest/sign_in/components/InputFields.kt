package com.example.myapplicationnewtest.sign_in.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
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
import com.example.myapplicationnewtest.appColors


@Composable
fun InputFields(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    val colors = appColors()
    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.tertiaryColor,
        backgroundColor = colors.tertiaryColor
    )

    val focusManager = LocalFocusManager.current




    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    label ,
                    color = colors.onBackgroundColor ,
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
                cursorColor = colors.tertiaryColor,
                focusedContainerColor = colors.surfaceVariant,
                unfocusedContainerColor =colors.surfaceVariant,
                disabledContainerColor = colors.surfaceVariant,
                focusedTextColor = colors.tertiaryColor,
                unfocusedTextColor = colors.tertiaryColor,
                disabledTextColor = colors.tertiaryColor,
                focusedIndicatorColor = colors.tertiaryColor,
                unfocusedIndicatorColor = colors.tertiaryColor,
                disabledIndicatorColor = colors.tertiaryColor,
                )
        )
    }
}
