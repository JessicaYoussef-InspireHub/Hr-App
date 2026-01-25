package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.presentation.base64ToImageBitmap

@Composable
fun LunchCard(
    name: String,
    supplierName: String,
    price: String,
    imageBase64: String?,
    isNew: Boolean,
    onClick: () -> Unit,

    ) {
    val colors = appColors()
    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }
            .joinToString("")
    }

    val localizedPrice = if (currentLanguage == "ar") convertToArabicDigits(price) else price

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = colors.surfaceContainerHigh
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    )
    {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .padding(top = 8.dp, bottom = 28.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    Spacer(modifier = Modifier.width(8.dp))

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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val imageBitmap = remember(imageBase64) {
                    imageBase64?.let { base64ToImageBitmap(it) }
                }

                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )
                }


                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackgroundColor
                    )
                    Text(
                        text = supplierName,
                        color = colors.inverseOnSurface,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = localizedPrice,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.inverseOnSurface
                    )
                }
            }
        }
    }
}