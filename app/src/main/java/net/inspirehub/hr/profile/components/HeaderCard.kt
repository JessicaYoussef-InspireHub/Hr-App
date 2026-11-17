package net.inspirehub.hr.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.appColors
import net.inspirehub.hr.profile.data.ProfileUiData


@Composable
fun HeaderCard(
    profile: ProfileUiData
) {
    val colors = appColors()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.tertiaryColor.copy(alpha = 0.8f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(colors.onBackgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profile.name
                        .split(" ")
                        .take(2)
                        .mapNotNull { it.firstOrNull()?.toString() }
                        .joinToString(""),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.tertiaryColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = profile.name,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackgroundColor
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = profile.jobTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = colors.onBackgroundColor.copy(alpha = 0.85f)
            )
        }
    }
}