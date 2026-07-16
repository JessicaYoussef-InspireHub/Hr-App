package net.inspirehub.hr.attendance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors

@Composable
fun AttendanceTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    tabs: List<String>
) {
    val colors = appColors()

    LazyRow(
        modifier = Modifier.fillMaxWidth(0.75f),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        contentPadding = PaddingValues(horizontal = 0.dp)
    ) {

        itemsIndexed(tabs) { index, title ->

            val selected = selectedTab == index

            Text(
                text = title,
                fontSize = 18.sp,

                fontWeight =
                    if (selected)
                        FontWeight.Bold
                    else
                        FontWeight.Medium,

                color =
                    if (selected)
                        colors.tertiaryColor
                    else
                        colors.onBackgroundColor,

                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable {
                        onTabSelected(index)
                    }
                    .background(
                        color =
                            if (selected)
                                colors.surfaceContainerHigh
                            else
                                colors.transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            )
        }
    }
}