package net.inspirehub.hr.lunch.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.OrderWithItems
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@SuppressLint("SimpleDateFormat")
@Composable
fun HistoryOrderCard(
    orderWithItems: OrderWithItems,
    onReorderClick: () -> Unit
) {

    val order = orderWithItems.order
    val items = orderWithItems.items
    val colors = appColors()



    @Composable
    @SuppressLint("SimpleDateFormat")
    fun formatOrderDate(orderDate: Long): String {
        val sdf = SimpleDateFormat("yyyyMMdd")

        val orderDay = sdf.format(Date(orderDate))
        val today = sdf.format(Date())

        val yesterdayCal = Calendar.getInstance()
        yesterdayCal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = sdf.format(yesterdayCal.time)

        return when (orderDay) {
            today -> stringResource(R.string.Today)
            yesterday -> stringResource(R.string.Yesterday)
            else -> SimpleDateFormat("d MMM yyyy").format(Date(orderDate))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text =  formatOrderDate(order.orderDate),
            color = colors.onBackgroundColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(5.dp))
        Card (
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colors.surfaceColor)
        ){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 12.dp)
                ) {
                    items.forEach {
                        Text(
                            "${it.quantity} x ${it.name}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.onBackgroundColor
                        )
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = "${stringResource(R.string.total_price)} ${order.totalPrice}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = colors.onBackgroundColor
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.tertiaryColor,
                        contentColor = colors.onSecondaryColor
                    ),
                    onClick = onReorderClick,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.re_order),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}