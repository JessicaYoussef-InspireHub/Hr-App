package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.inspirehub.hr.lunch.presentation.base64ToImageBitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.DatabaseProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import net.inspirehub.hr.R
import net.inspirehub.hr.lunch.data.CartItem
import net.inspirehub.hr.utils.convertToArabicDigits


@Composable
fun FavoriteCard() {

    val colors = appColors()
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    val favorites by db.favoriteLunchDao().getAllFavoritesFlow()
        .collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()
    val addedItems = remember { mutableStateOf(setOf<Int>()) }
    val cartItems by db.cartDao().getAllItemsFlow().collectAsState(initial = emptyList())


    favorites.forEachIndexed { index, item ->
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    item.imageBase64?.let {
                        val bitmap = base64ToImageBitmap(it)
                        Image(
                            bitmap = bitmap,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = item.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackgroundColor
                        )
                        Text(
                            text = item.supplierName,
                            color = colors.inverseOnSurface,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text(
                            text = convertToArabicDigits(item.price),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.inverseOnSurface
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically

                ) {


                    Box(
                        modifier = Modifier
                            .background(
                                if (cartItems.any { it.productId == item.id }) colors.surfaceColor else colors.tertiaryColor,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .clickable(enabled = !(cartItems.any { it.productId == item.id })) {
                                coroutineScope.launch {
                                    val cartItem = CartItem(
                                        productId = item.id,
                                        name = item.name,
                                        price = item.price.toDoubleOrNull() ?: 0.0,
                                        quantity = 1
                                    )
                                    db.cartDao().insertItem(cartItem)
                                    addedItems.value = addedItems.value + item.id
                                }
                            },
                    ) {
                        Text(
                            text = if (cartItems.any { it.productId == item.id }) stringResource(R.string.added) else stringResource(
                                R.string.add_to_cart
                            ),
                            color = if (cartItems.any { it.productId == item.id }) colors.tertiaryColor else colors.onSecondaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Remove from favorites",
                        tint = colors.tertiaryColor,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable {
                                coroutineScope.launch {
                                    db.favoriteLunchDao().deleteFavorite(item.id)
                                }
                            }
                    )
                }
            }
            if (index != favorites.lastIndex) {
            HorizontalDivider(
                thickness = 1.dp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = colors.surfaceColor

            )}
        }
    }
}