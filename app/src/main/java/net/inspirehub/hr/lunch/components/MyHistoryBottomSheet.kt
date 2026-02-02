package net.inspirehub.hr.lunch.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.DatabaseProvider
import net.inspirehub.hr.lunch.data.OrderWithItems

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun MyHistoryBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit
) {
    if (!showSheet) return

    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    val colors = appColors()

    var orders by remember { mutableStateOf(emptyList<OrderWithItems>()) }

    LaunchedEffect(Unit) {
        orders = db.orderDao().getOrdersWithItems()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = colors.surfaceContainerHigh,
        windowInsets = WindowInsets(0)
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
                .padding(horizontal = 8.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd,
            ) {
                IconButton(onClick = { onDismiss() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = colors.tertiaryColor,
                    )
                }
            }

            Text(
                text = stringResource(R.string.my_order_history),
                color = colors.onBackgroundColor,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 10.dp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))




            if (orders.isEmpty()) {
                Text("No previous orders yet 🧾")
            } else {
                orders.forEach { order ->
                    HistoryOrderCard(order)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun HistoryOrderCard(orderWithItems: OrderWithItems) {

    val order = orderWithItems.order
    val items = orderWithItems.items

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(appColors().surfaceColor, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        Text(
            text = "Date: ${
                java.text.SimpleDateFormat("dd MMM yyyy")
                    .format(java.util.Date(order.orderDate))
            }",
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        items.forEach {
            Text("• ${it.name} x${it.quantity}")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Total: ${order.totalPrice} EGP",
            fontWeight = FontWeight.Bold
        )
    }
}

