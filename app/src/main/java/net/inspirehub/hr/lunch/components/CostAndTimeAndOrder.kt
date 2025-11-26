package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CostAndTime(
    cost: Double,
    lastOrderTime: String,
    navController: NavController
) {

    val colors = appColors()
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    val currentLanguage = sharedPrefManager.getLanguage()

    fun convertToArabicDigits(input: String): String {
        val arabicDigits = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
        return input.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }
            .joinToString("")
    }

    val formattedCost = "%.2f".format(cost)
    val localizedCost =
        if (currentLanguage == "ar") convertToArabicDigits(formattedCost) else formattedCost

    val balanceText = if (currentLanguage == "ar") {
        "$localizedCost جنيه"
    } else {
        "$localizedCost L.E"
    }


    val timeWithPeriod = try {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val date = inputFormat.parse(lastOrderTime)

        val outputFormat = if (currentLanguage == "ar") {
            SimpleDateFormat("hh:mm a", Locale("ar"))
        } else {
            SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        }

        val formattedTime = outputFormat.format(date!!)
        val localizedTime = if (currentLanguage == "ar") {
            formattedTime
                .replace("AM", "ص")
                .replace("PM", "م")
        } else {
            formattedTime
        }

        if (currentLanguage == "ar") convertToArabicDigits(localizedTime) else localizedTime
    } catch (e: Exception) {
        lastOrderTime
    }

    Column {
        Text(
            buildAnnotatedString {
                append(stringResource(R.string.your_account_is) + " ")
                withStyle(
                    style = SpanStyle(
                        color = colors.tertiaryColor,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(balanceText)
                }
            },
            fontSize = 20.sp,
            color = colors.onBackgroundColor
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            buildAnnotatedString {
                append(stringResource(R.string.the_last_time_to_order_is) + " ")
                withStyle(
                    style = SpanStyle(
                        color = colors.tertiaryColor,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(timeWithPeriod)
                }
            },
            fontSize = 20.sp,
            color = colors.onBackgroundColor
        )

        Spacer(modifier = Modifier.height(20.dp))



        Text(
            stringResource(R.string.my_order_is_here),
            color = colors.tertiaryColor,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable{
                navController.navigate("OrderScreen")
            }
        )
    }
}
