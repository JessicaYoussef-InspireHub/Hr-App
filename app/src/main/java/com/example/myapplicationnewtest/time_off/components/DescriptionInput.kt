package com.example.myapplicationnewtest.time_off.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R



@Composable
fun DescriptionInput(
    modifier: Modifier = Modifier
) {
    var description by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val customTextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.tertiary,
        backgroundColor = MaterialTheme.colorScheme.tertiary
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = description,
            onValueChange = { description = it },
            placeholder = {
                Text(
                    stringResource(R.string.add_a_description),
                    color = MaterialTheme.colorScheme.surface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            TextStyle(
                color = MaterialTheme.colorScheme.surface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold)
            },
            modifier = modifier.fillMaxWidth()
                .verticalScroll(scrollState),
            maxLines = 3,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.tertiary,
                focusedTextColor = MaterialTheme.colorScheme.tertiary,
                unfocusedTextColor = MaterialTheme.colorScheme.tertiary,
                focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}
