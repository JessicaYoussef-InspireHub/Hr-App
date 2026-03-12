package net.inspirehub.hr.sign_in.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
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
import net.inspirehub.hr.appColors
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Composable
fun InputFields(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    isPassword: Boolean = false
) {
    val colors = appColors()
    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.tertiaryColor,
        backgroundColor = colors.tertiaryColor
    )

    val focusManager = LocalFocusManager.current
    val passwordVisible = remember { mutableStateOf(false) }



    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    label,
                    color = colors.onBackgroundColor,
                    fontWeight = FontWeight.Normal
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            maxLines = 1,
            visualTransformation = if (isPassword && !passwordVisible.value)
                PasswordVisualTransformation()
            else
                VisualTransformation.None,

            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = imeAction,
                keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email
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
                unfocusedContainerColor = colors.surfaceVariant,
                disabledContainerColor = colors.surfaceVariant,
                focusedTextColor =   colors.onBackgroundColor,
                unfocusedTextColor =  colors.onBackgroundColor,
                disabledTextColor =  colors.onBackgroundColor,
                focusedIndicatorColor = colors.tertiaryColor,
                unfocusedIndicatorColor = colors.tertiaryColor,
                disabledIndicatorColor = colors.tertiaryColor,
            ),

            trailingIcon = {
                if (isPassword) {
                    val image = if (passwordVisible.value)
                        Icons.Filled.Visibility
                    else
                        Icons.Filled.VisibilityOff

                    IconButton(onClick = {
                        passwordVisible.value = !passwordVisible.value
                    }) {
                        Icon(
                            imageVector = image,
                            contentDescription = if (passwordVisible.value) "Hide password" else "Show password",
                            tint = colors.tertiaryColor
                        )
                    }
                }
            }
        )
    }
}