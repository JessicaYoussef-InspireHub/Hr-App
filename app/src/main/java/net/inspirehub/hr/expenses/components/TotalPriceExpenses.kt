package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun TotalPriceExpenses(){

    val colors  = appColors()
    var amount by remember { mutableStateOf("") }

    val customTextSelectionColors = TextSelectionColors(
        handleColor = colors.tertiaryColor,
        backgroundColor = colors.tertiaryColor
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){

        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
            TextField(
                value = amount,
                onValueChange = { amount = it },
                placeholder = {
                    Text(
                        "0.00",
                        color = colors.onBackgroundColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                singleLine = true,
            )
        }

        Text("EGP" ,
            color = colors.onBackgroundColor
        )
    }

}