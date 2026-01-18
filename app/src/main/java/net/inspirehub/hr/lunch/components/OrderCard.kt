package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

@Composable
fun OrderCard() {

    val color = appColors()
    var count by remember { mutableIntStateOf(1) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 25.dp , bottom = 0.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Card(
            modifier = Modifier
                .width(120.dp)
                .height(170.dp)
                .padding(bottom = 25.dp),
            colors = CardDefaults.cardColors(
                containerColor = color.surfaceContainerHigh,
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.grilled_chicken),
                contentDescription = "Order Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Grilled Chicken",
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = color.onBackgroundColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "chicken house",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = color.onBackgroundColor
            )
            Spacer(modifier = Modifier.height(30.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "50.00",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    color = color.onBackgroundColor
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(
                                1.dp,
                                color.onBackgroundColor,
                                CircleShape
                            )
                            .clickable {
                                if (count > 1) {
                                    count--
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "-",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = color.onBackgroundColor
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = count.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        color = color.onBackgroundColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(
                                1.dp,
                                color.onBackgroundColor,
                                CircleShape
                            )
                            .clickable { count++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "+",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Medium,
                            color = color.onBackgroundColor
                        )
                    }
                }
            }
        }
    }
}