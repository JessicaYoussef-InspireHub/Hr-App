package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors

@Composable
fun LunchCard(
    title: String,
    price: String,
    phone : String,
    restaurant: String ,
    imageRes: Int,
    onClick: () -> Unit,

){
    val colors = appColors()
    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠','١','٢','٣','٤','٥','٦','٧','٨','٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    }

    val localizedPrice = if (currentLanguage == "ar") convertToArabicDigits(price) else price
    val localizedPhone = if (currentLanguage == "ar") convertToArabicDigits(phone) else phone


    Box(
        modifier = Modifier
            .width(250.dp)
            .padding(bottom = 30.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .align(Alignment.BottomCenter),
            colors = CardDefaults.cardColors(
                containerColor = colors.surfaceContainerHigh
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.Start
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackgroundColor
                    )
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Favourite Icon",
                        tint = if (isFavorite) colors.tertiaryColor else colors.onBackgroundColor,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { isFavorite = !isFavorite }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = restaurant + "\n" + localizedPhone,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onBackgroundColor
                    )
                    Text(
                        text = " \n$localizedPrice",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.surfaceContainerHigh
                    )
                }
            }
        }

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(260.dp)
                .offset(y = (50).dp, x = (100).dp)
                .clip(RoundedCornerShape(100.dp))
                .shadow(8.dp, CircleShape)
        )
    }
}