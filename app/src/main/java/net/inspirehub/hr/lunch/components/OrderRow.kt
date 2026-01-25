package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.appColors

@Composable
fun OrderRow(){
    val colors = appColors()
    var quantity by remember { mutableIntStateOf(1) }

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ){
            Box(
                modifier = Modifier
                    .size(23.dp)
                    .background(
                        color = colors.transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = colors.tertiaryColor,
                        shape = CircleShape
                    )
                    .clickable {
                        if (quantity > 0) quantity--
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "minus",
                    tint = colors.tertiaryColor,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text( "$quantity" ,
                color = colors.onBackgroundColor,
                fontWeight = FontWeight.Medium
            )
            Box (
                modifier = Modifier
                    .size(23.dp)
                    .background(
                        color = colors.transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = colors.tertiaryColor,
                        shape = CircleShape
                    )
                    .clickable{
                        quantity++
                    },
                contentAlignment = Alignment.Center
            ){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add",
                    tint = colors.tertiaryColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Text("Burger" ,
            color = colors.onBackgroundColor,
            fontWeight = FontWeight.Medium)
        Text("250.0 EGP" ,
            color = colors.onBackgroundColor,
            fontWeight = FontWeight.Medium)
    }
}