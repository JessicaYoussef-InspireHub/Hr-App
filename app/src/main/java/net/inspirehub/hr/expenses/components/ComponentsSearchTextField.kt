package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SearchIcon
import net.inspirehub.hr.appColors
@Composable
fun ComponentsSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    containerColor: Color
) {

    val colors = appColors()
    val isTextEntered = value.isNotEmpty()
    val iconAndCursorColor =
        if (isTextEntered) colors.tertiaryColor else colors.onBackgroundColor
    val customTextSelectionColors = TextSelectionColors(
        handleColor = iconAndCursorColor,
        backgroundColor = iconAndCursorColor.copy(alpha = 0.3f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.search_here),
                    fontSize = 14.sp,
                    color = colors.onBackgroundColor
                )
            },
            textStyle = TextStyle(
                color = colors.tertiaryColor,
                fontSize = 16.sp
            ),
            leadingIcon = {
                 SearchIcon(tint = iconAndCursorColor)
            },
            singleLine = true,
            modifier = Modifier.padding(horizontal = 8.dp),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                cursorColor = colors.tertiaryColor,
                focusedContainerColor = containerColor,
                unfocusedContainerColor = containerColor,
                disabledContainerColor = containerColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                imeAction = ImeAction.Search
            )
        )
    }
}