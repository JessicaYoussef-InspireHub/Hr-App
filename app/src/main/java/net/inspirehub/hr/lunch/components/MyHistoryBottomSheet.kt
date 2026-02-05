package net.inspirehub.hr.lunch.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.CartItem
import net.inspirehub.hr.lunch.data.DatabaseProvider
import net.inspirehub.hr.lunch.data.OrderWithItems

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun MyHistoryBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onReorderSuccess: () -> Unit
) {
    if (!showSheet) return

    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    val colors = appColors()
    val scope = rememberCoroutineScope()

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
                modifier = Modifier.fillMaxWidth(),
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
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = stringResource(R.string.no_previous_orders_yet),
                        color = colors.tertiaryColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                orders.forEach { order ->
                    HistoryOrderCard(
                        orderWithItems = order,
                        onReorderClick = {

                            scope.launch {
                                order.items.forEach { item ->
                                    db.cartDao().insertItem(
                                        CartItem(
                                            productId = item.productId,
                                            name = item.name,
                                            price = item.price,
                                            quantity = item.quantity
                                        )
                                    )
                                }
                                onDismiss()
                            onReorderSuccess()
                        }}
                    )
                    Spacer(modifier = Modifier.height(25.dp))
                }
            }
        }
    }
}

