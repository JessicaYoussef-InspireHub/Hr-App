package net.inspirehub.hr.expenses.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
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
import net.inspirehub.hr.appColors

@Composable
fun ExpensesSearchBar(
    label: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    isFilterActive: Boolean
) {

    val colors = appColors()

    val isTextEntered = query.isNotEmpty()

    val iconAndCursorColor =
        if (isTextEntered) colors.tertiaryColor else colors.onBackgroundColor

    val customTextSelectionColors = TextSelectionColors(
        handleColor = iconAndCursorColor,
        backgroundColor = iconAndCursorColor.copy(alpha = 0.3f)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {

            TextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(0.85f),
                singleLine = true,
                placeholder = {
                    Text(
                        text = label,
                        fontSize = 16.sp,
                        color = colors.onBackgroundColor
                    )
                },
                textStyle = TextStyle(
                    color = colors.tertiaryColor,
                    fontSize = 16.sp
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = iconAndCursorColor
                    )
                },
                shape = RoundedCornerShape(50.dp),
                colors = TextFieldDefaults.colors(
                    cursorColor = colors.tertiaryColor,
                    focusedContainerColor = colors.surfaceContainerHigh,
                    unfocusedContainerColor = colors.surfaceContainerHigh,
                    disabledContainerColor = colors.surfaceContainerHigh,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    imeAction = ImeAction.Search
                )
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .background(colors.surfaceContainerHigh, CircleShape)
                .clickable { onFilterClick() }
        ) {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = "Filter",
                tint = if (isFilterActive) colors.tertiaryColor else colors.onBackgroundColor,
                modifier = Modifier
                    .size(50.dp)
                    .padding(10.dp)
            )
        }
    }
}