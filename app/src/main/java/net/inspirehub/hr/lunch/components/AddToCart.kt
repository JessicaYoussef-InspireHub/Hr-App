package net.inspirehub.hr.lunch.components

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
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors

@Composable
fun AddToCart(
    price: Double,
    onAddClick: () -> Unit
){
    val colors = appColors()
    var quantity by remember { mutableIntStateOf(1) }
    val totalPrice = quantity * price
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    }

    val localizedQuantity =
        if (currentLanguage == "ar") convertToArabicDigits(quantity.toString())
        else quantity.toString()

    val localizedTotal =
        if (currentLanguage == "ar") convertToArabicDigits(totalPrice.toInt().toString())
        else totalPrice.toInt().toString()

    Row (
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Row(
            modifier = Modifier
                .padding(start = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically

        )
        { Button(
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
                        shape = RoundedCornerShape(8.dp)
                    ),
                shape = RoundedCornerShape(8.dp),
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
                        shape = RoundedCornerShape(8.dp)
                    ),
                shape = RoundedCornerShape(8.dp),
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
            onClick = { onAddClick() },
            modifier = Modifier.height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.tertiaryColor
            ),
            shape = RoundedCornerShape(8.dp)
        ){
            Text(stringResource(R.string.add_to_cart, localizedTotal),
                color = colors.onSecondaryColor,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}