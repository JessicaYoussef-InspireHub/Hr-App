package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R

@Composable
fun OrderCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // ارتفاع المستطيل
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.grilled_chicken), // استبدلي باسم الصورة
                contentDescription = "Order Image",
                contentScale = ContentScale.Crop, // يملأ المستطيل ويقص على الحواف
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
        }
    }
}
