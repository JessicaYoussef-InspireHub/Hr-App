package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors

@Composable
fun LunchTextField() {
    val colors = appColors()
    var noteToRestaurant by remember { mutableStateOf("") }


    OutlinedTextField(
        value = noteToRestaurant,
        onValueChange = { noteToRestaurant = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        label = { Text(
            stringResource(R.string.note_to_restaurant),
            color = colors.onBackgroundColor) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colors.transparent,
            unfocusedContainerColor = colors.transparent,
            disabledContainerColor = colors.transparent,
            cursorColor = colors.tertiaryColor,
            focusedTextColor = colors.tertiaryColor,
            unfocusedTextColor = colors.tertiaryColor,
            focusedIndicatorColor = colors.tertiaryColor,
            unfocusedIndicatorColor = colors.tertiaryColor,
            disabledIndicatorColor = colors.transparent
        )
    )
}
