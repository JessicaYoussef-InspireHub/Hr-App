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
    selectedCategory: LunchCategory?,
    onAllSelected: () -> Unit,
    onCategorySelected: (LunchCategory) -> Unit
) {
    val colors = appColors()

    LazyRow(
        modifier = Modifier.fillMaxWidth(0.75f),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {
        item {
            Text(
                text = "All",
                fontSize = 18.sp,
                fontWeight = if (selectedCategory == null)
                    FontWeight.Bold else FontWeight.Medium,
                color = if (selectedCategory == null)
                    colors.tertiaryColor else colors.onBackgroundColor,
                modifier = Modifier
                    .clickable { onAllSelected() }
                    .background(
                        if (selectedCategory == null)
                            colors.surfaceContainerHigh
                        else colors.transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            )
        }

        items(categories) { category ->
            Text(
                text = category.name,
                fontSize = 18.sp,
                fontWeight = if (selectedCategory?.id == category.id)
                    FontWeight.Bold
                else
                    FontWeight.Medium,

                color = if (selectedCategory?.id == category.id)
                    colors.tertiaryColor
                else
                    colors.onBackgroundColor,

                modifier = Modifier
                    .clickable {
                        onCategorySelected(category)
                    }
                    .background(
                        if (selectedCategory == category)
                            colors.surfaceContainerHigh
                        else
                           colors.transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            )
        }
    }
}