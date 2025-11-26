package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchBottomSheet(
    title: String,
    price: String,
    imageRes: Int,
    onDismiss: () -> Unit
) {
    val colors = appColors()

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    }

    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()

    val localizedPrice = if (currentLanguage == "ar") {
        convertToArabicDigits(price)
    } else {
        price
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceContainerHigh
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.tertiaryColor,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = title,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                )

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                )
                {
                    Text(
                        text = title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackgroundColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = stringResource(R.string.price_label, localizedPrice),
                        color = colors.onBackgroundColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LunchTextField()

            Spacer(Modifier.height(80.dp))

            AddToCart(
                price = price.toDouble()
            )

        }
    }
}
