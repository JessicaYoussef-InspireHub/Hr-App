package com.example.myapplicationnewtest.time_off.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.appColors


@Composable
fun DescriptionInput(
    modifier: Modifier = Modifier
) {
    var description by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val colors = appColors()


    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.tertiaryColor,
        backgroundColor = colors.tertiaryColor
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = {
                Text(
                    stringResource(R.string.add_a_description),
                    color = colors.onBackgroundColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            TextStyle(
                color = colors.onBackgroundColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold)
            },
            modifier = modifier.fillMaxWidth()
                .verticalScroll(scrollState),
            maxLines = 3,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = colors.transparent,
                unfocusedContainerColor = colors.transparent,
                disabledContainerColor = colors.transparent,
                cursorColor =colors.tertiaryColor,
                focusedTextColor = colors.tertiaryColor,
                unfocusedTextColor = colors.tertiaryColor,
                focusedIndicatorColor = colors.tertiaryColor,
                unfocusedIndicatorColor = colors.tertiaryColor,
                disabledIndicatorColor = colors.transparent
            )
        )
    }
}
