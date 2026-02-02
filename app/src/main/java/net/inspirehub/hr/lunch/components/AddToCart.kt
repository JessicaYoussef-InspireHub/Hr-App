package net.inspirehub.hr.lunch.components

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.CartItem
import net.inspirehub.hr.lunch.data.DatabaseProvider

@Composable
fun AddToCart(
    productId: Int,
    price: Double,
    name: String,
    onAddClick: () -> Unit
) {
    val colors = appColors()
    var quantity by remember { mutableIntStateOf(1) }
    val totalPrice = quantity * price
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()



    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }
            .joinToString("")
    }

    val localizedQuantity =
        if (currentLanguage == "ar") convertToArabicDigits(quantity.toString())
        else quantity.toString()

    val localizedTotal =
        if (currentLanguage == "ar") convertToArabicDigits(totalPrice.toInt().toString())
        else totalPrice.toInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .padding(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically

        )
        {
            Button(
                onClick = {
                    if (quantity > 1) quantity--
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.surfaceContainerHigh
                ),
                modifier = Modifier
                    .size(50.dp)
                    .border(
                        width = 2.dp,
                        color = colors.onBackgroundColor,
                        shape = CircleShape
                    ),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)

            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "-",
                        color = colors.onBackgroundColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = localizedQuantity,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onBackgroundColor
            )

            Button(
                onClick = { quantity++ },
                colors = ButtonDefaults.buttonColors(containerColor = colors.surfaceContainerHigh),
                modifier = Modifier
                    .size(50.dp)
                    .border(
                        width = 2.dp,
                        color = colors.onBackgroundColor,
                        shape = CircleShape
                    ),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+",
                        color = colors.onBackgroundColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Button(
            onClick = {
                val db = DatabaseProvider.getDatabase(context)
                val activity = context as? ComponentActivity

                activity?.lifecycleScope?.launch(Dispatchers.IO) {
                    val item = CartItem(
                        productId = productId,
                        name = name,
                        price = price,
                        quantity = quantity
                    )
                    db.cartDao().insertItem(item)

                    val allItems = db.cartDao().getAllItems()
                    allItems.forEach {
                        Log.d("ROOM_CART", "Name: ${it.name}, Price: ${it.price}, Quantity: ${it.quantity}, ID: ${it.productId}")
                    }
                }
                onAddClick()
            },
            modifier = Modifier.height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.tertiaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                stringResource(R.string.add) + " " + localizedTotal,
                color = colors.onSecondaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}