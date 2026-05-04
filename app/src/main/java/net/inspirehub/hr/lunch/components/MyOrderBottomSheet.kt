package net.inspirehub.hr.lunch.components


import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.data.CartItem
import net.inspirehub.hr.lunch.data.DatabaseProvider
import net.inspirehub.hr.lunch.data.OrderEntity
import net.inspirehub.hr.lunch.data.OrderItemEntity
import net.inspirehub.hr.lunch.data.submitLunchOrder

@SuppressLint("FrequentlyChangedStateReadInComposition", "SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrderBottomSheet(
    showSheet: Boolean,
    onDismiss: () -> Unit,
    onOrderSuccess: () -> Unit
) {

    val colors = appColors()
    val context = LocalContext.current
    val db = DatabaseProvider.getDatabase(context)
    var cartItems by remember { mutableStateOf(listOf<CartItem>()) }
    val total = cartItems.sumOf { it.price * it.quantity }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(showSheet) {
        if (showSheet) {
            cartItems = db.cartDao().getAllItems()
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { onDismiss() },
            containerColor = colors.surfaceContainerHigh,
            windowInsets = WindowInsets(0),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
                    .padding(horizontal = 8.dp),
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
                    text = stringResource(R.string.my_order),
                    color = colors.onBackgroundColor,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 10.dp),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(20.dp))


                // Display Cart Items dynamically
                if (cartItems.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.no_new_orders_today),
                            color = colors.tertiaryColor,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    val maxHeightDp = 200.dp

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 0.dp, max = maxHeightDp)
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            items(cartItems.size) { index ->
                                val item = cartItems[index]
                                    OrderRow(
                                        item = item,
                                        onQuantityChange = { updatedItem ->
                                            coroutineScope.launch {
                                                db.cartDao().insertItem(updatedItem)
                                                cartItems = db.cartDao().getAllItems()
                                            }
                                        },
                                        onRemoveItem = { removedItem ->
                                            coroutineScope.launch {
                                                db.cartDao().deleteItem(removedItem)
                                                cartItems = db.cartDao().getAllItems()
                                            }
                                        }
                                    )
                                if (index != cartItems.size - 1) {
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(
                                            horizontal = 16.dp,
                                            vertical = 8.dp
                                        ),
                                        color = colors.surfaceColor
                                    )
                                }
                            }
                        }
                    }


                    Spacer(modifier = Modifier.height(20.dp))
                    if (cartItems.isEmpty()) {
                        Spacer(modifier = Modifier.height(0.dp))
                    } else {

                        TotalPrice(stringResource(R.string.total_price), total)

                        Spacer(modifier = Modifier.height(20.dp))

                        OrderNowButton(
                            onClick = {
                                coroutineScope.launch {
                                    isLoading = true
                                    val apiSuccess =
                                        submitLunchOrder(context = context, cartItems = cartItems)

                                    if (apiSuccess) {
                                        launch(Dispatchers.IO) {
                                            val orderId = db.orderDao().insertOrder(
                                                OrderEntity(
                                                    orderDate = System.currentTimeMillis(),
                                                    totalPrice = total
                                                )
                                            ).toInt()

                                            val orderItems = cartItems.map {
                                                OrderItemEntity(
                                                    orderId = orderId,
                                                    productId = it.productId,
                                                    name = it.name,
                                                    price = it.price,
                                                    quantity = it.quantity
                                                )
                                            }

                                            db.orderDao().insertOrderItem(orderItems)
                                            db.cartDao().clearCart()

                                            launch(Dispatchers.Main) {
                                                cartItems = emptyList()
                                                isLoading = false
                                                onDismiss()
                                                onOrderSuccess()
                                            }
                                        }
                                    } else {
                                        println("Failed to submit lunch order to API")
                                        isLoading = false
                                    }
                                }
                            },
                            isLoading = isLoading
                        )
                    }
                }
            }
        }
    }
}