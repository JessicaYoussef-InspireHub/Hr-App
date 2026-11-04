package net.inspirehub.hr.deduction.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.vector.ImageVector
import net.inspirehub.hr.appColors


@Composable
fun SummaryItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {

    val colors = appColors()

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(iconColor.copy(0.12f)),
        shape = RoundedCornerShape(12.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                color = colors.onBackgroundColor
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = iconColor
            )
        }
    }
}