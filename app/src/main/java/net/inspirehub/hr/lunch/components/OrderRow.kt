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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.CartItem
import net.inspirehub.hr.utils.convertToArabicDigits

@Composable
fun OrderRow(
    item: CartItem,
    onQuantityChange: (CartItem) -> Unit,
    onRemoveItem: (CartItem) -> Unit
) {
    val colors = appColors()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
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
                        if (item.quantity > 1) {
                            onQuantityChange(item.copy(quantity = item.quantity - 1))
                        }
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

            Text(
                convertToArabicDigits("${item.quantity}"),
                color = colors.onBackgroundColor,
                fontWeight = FontWeight.Medium
            )
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
                        onQuantityChange(item.copy(quantity = item.quantity + 1))
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "add",
                    tint = colors.tertiaryColor,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Text(item.name, color = colors.onBackgroundColor, fontWeight = FontWeight.Medium)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                convertToArabicDigits(item.price.toString()),
                color = colors.onBackgroundColor,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = colors.tertiaryColor,
                modifier = Modifier
                    .size(28.dp)
                    .clickable {
                        onRemoveItem(item)
                    }
            )

        }
    }
}