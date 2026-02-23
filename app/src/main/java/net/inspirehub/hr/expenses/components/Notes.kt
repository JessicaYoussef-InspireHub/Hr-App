package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun Notes(){

    val colors = appColors()
    var notes by remember { mutableStateOf("") }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.tertiaryColor,
        backgroundColor = colors.tertiaryColor
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = notes,
            onValueChange = { notes = it },
            placeholder = {
                Text(
                    stringResource(R.string.add_your_notes),
                    color = colors.onBackgroundColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.transparent,
                unfocusedContainerColor = colors.transparent,
                disabledContainerColor = colors.transparent,
                cursorColor =colors.tertiaryColor,
                focusedTextColor = colors.onBackgroundColor,
                unfocusedTextColor = colors.onBackgroundColor,
                focusedIndicatorColor = colors.tertiaryColor,
                unfocusedIndicatorColor = colors.tertiaryColor,
                disabledIndicatorColor = colors.transparent
            ),
            textStyle = TextStyle(
                color = colors.tertiaryColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp
            ),
            singleLine = false,
        )
    }
}