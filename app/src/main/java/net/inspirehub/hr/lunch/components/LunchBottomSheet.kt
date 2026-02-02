package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.presentation.base64ToImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchBottomSheet(
    productId: Int,
    name: String,
    price: String,
    isNew: Boolean,
    description: String?,
    supplierName: String,
    imageBase64: String?,
    onDismiss: () -> Unit,
    onAddToCart: (String) -> Unit
) {
    val colors = appColors()

    val imageBitmap = remember(imageBase64) {
        imageBase64?.let { base64ToImageBitmap(it, 120 , 120) }
    }

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }
            .joinToString("")
    }

    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()
    var isFavorite by remember { mutableStateOf(false) }

    val localizedPrice = if (currentLanguage == "ar") {
        convertToArabicDigits(price)
    } else {
        price
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceContainerHigh,
        windowInsets = WindowInsets(0)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                        contentDescription = stringResource(R.string.close),
                        tint = colors.tertiaryColor,
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = name,
                        modifier = Modifier
                            .width(100.dp)
                            .height(100.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Text(
                            text = name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onBackgroundColor
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {

                            if (isNew) {
                                Box(
                                    modifier = Modifier
                                        .background(colors.surfaceColor, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.is_new),
                                        color = colors.tertiaryColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                            }

                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Favourite Icon",
                                tint = if (isFavorite) colors.tertiaryColor else colors.onBackgroundColor,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { isFavorite = !isFavorite }
                            )

                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    if (!description.isNullOrEmpty()) {
                        Text(
                            text = description,
                            color = colors.inverseOnSurface,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        text = supplierName,
                        color = colors.inverseOnSurface,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Text(
                        text = localizedPrice,
                        color = colors.inverseOnSurface,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LunchTextField()

            Spacer(Modifier.height(80.dp))

            AddToCart(
                price = price.toDouble(),
                name = name,
                productId = productId,
                onAddClick = {
                    onAddToCart(name)
                    onDismiss()
                }
            )
        }
    }
}
