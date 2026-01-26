package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.LunchCategory

@Composable
fun LunchCategoryRow(
    categories: List<LunchCategory>,
    onCategorySelected: (LunchCategory) -> Unit = {}
) {
    val colors = appColors()


    var selectedCategory by remember {
        mutableStateOf<LunchCategory?>(null)
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(0.75f),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        items(categories) { category ->
            Text(
                text = category.name,
                fontSize = 18.sp,
                fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Medium,
                color = if (selectedCategory == category)
                    colors.tertiaryColor
                else
                    colors.onBackgroundColor,
                modifier = Modifier
                    .clickable {
                        selectedCategory = category
                        onCategorySelected(category)
                    }
                    .background(
                        if (selectedCategory == category)
                            colors.surfaceContainerHigh
                        else
                           colors.transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }
}