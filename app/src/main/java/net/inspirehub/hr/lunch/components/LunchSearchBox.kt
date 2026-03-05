package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors


@Composable
fun LunchSearchBox(
    searchText: String,
    onSearchChanged: (String) -> Unit,
    onSuppliersSelected: (List<Int>) -> Unit
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = SharedPrefManager(context)
    val token = sharedPref.getToken() ?: " "
    var localSearch by remember { mutableStateOf(searchText) }
    val isTextEntered = searchText.isNotEmpty()


    val customTextSelectionColors = TextSelectionColors(
        handleColor = if (isTextEntered) colors.tertiaryColor else colors.onBackgroundColor,
        backgroundColor = if (isTextEntered) colors.onBackgroundColor else colors.tertiaryColor
    )

    val iconAndCursorColor = if (isTextEntered) colors.tertiaryColor else colors.onBackgroundColor

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .background(colors.surfaceContainerHigh, RoundedCornerShape(50.dp))
        )
        {
            Row(
                modifier = Modifier
                    .background(
                        color = colors.surfaceContainerHigh,
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_your_meal),
                    tint = iconAndCursorColor,
                    modifier = Modifier.padding(end = 8.dp)
                )

                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                    BasicTextField(
                        value = localSearch,
                        onValueChange = { value ->
                            localSearch = value
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = colors.tertiaryColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        cursorBrush = Brush.linearGradient(
                            colors = listOf(iconAndCursorColor, iconAndCursorColor)
                        ),
                        decorationBox = { innerTextField ->
                            if (searchText.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.search_your_meal),
                                    fontSize = 16.sp,
                                    color = colors.onBackgroundColor
                                )
                            }
                            innerTextField()
                        },
                    )
                    LaunchedEffect(localSearch) {
                        delay(500)
                        onSearchChanged(localSearch.trim())
                    }

                }
            }
        }


        SuppliersFilterBottomSheet(
            context = context,
            token = token,
            onApply = onSuppliersSelected
        )

        MyFavorite()

    }
}