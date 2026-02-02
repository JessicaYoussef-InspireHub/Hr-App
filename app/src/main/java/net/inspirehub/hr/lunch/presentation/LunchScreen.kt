package net.inspirehub.hr.lunch.presentation

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.FullLoading
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.components.LunchBottomSheet
import net.inspirehub.hr.lunch.components.LunchCard
import net.inspirehub.hr.lunch.components.LunchCategoryRow
import net.inspirehub.hr.lunch.components.LunchSearchBox
import net.inspirehub.hr.lunch.components.OrderSnackBar
import net.inspirehub.hr.lunch.data.LunchProduct
import net.inspirehub.hr.lunch.data.fetchLunchProducts
import androidx.core.graphics.scale
import net.inspirehub.hr.lunch.components.MyHistoryBottomSheet
import net.inspirehub.hr.lunch.components.MyOrderBottomSheet
import net.inspirehub.hr.lunch.data.LunchCategory
import net.inspirehub.hr.lunch.data.fetchLunchCategories


fun base64ToImageBitmap(
    base64: String,
    targetWidth: Int = 80,
    targetHeight: Int = 80
): ImageBitmap {
    val decoded = Base64.decode(base64, Base64.DEFAULT)
    val bitmap = BitmapFactory.decodeByteArray(decoded, 0, decoded.size)
    val resized = bitmap.scale(targetWidth, targetHeight)
    return resized.asImageBitmap()
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchScreen(
    navController: NavController,
) {
    val context = LocalContext.current
    val colors = appColors()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var lunchProducts by remember { mutableStateOf<List<LunchProduct>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val successMessage = stringResource(R.string.your_order_has_been_placed_successfully)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<LunchProduct?>(null) }
    val sharedPref = remember { SharedPrefManager(context) }
    val token = sharedPref.getToken()
    var categories by remember { mutableStateOf<List<LunchCategory>>(emptyList()) }
    var openCartSheet by remember { mutableStateOf(false) }
    var showHistorySheet by remember { mutableStateOf(false) }


    LaunchedEffect(token) {
        if (!token.isNullOrBlank()) {
            categories = fetchLunchCategories(context, token)
            lunchProducts = fetchLunchProducts(context, token)
            isLoading = false
        }
    }


    Scaffold(
        containerColor = colors.onSecondaryColor,
        snackbarHost = {
            SnackbarHost(
                hostState = snackBarHostState
            ) { data ->
                OrderSnackBar(
                    snackBarData = data,
                    onViewCart = {
                        openCartSheet = true
                    })
            }
        },
        topBar = {
            MyAppBar(
                stringResource(R.string.lunch),
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->

        if (showBottomSheet && selectedItem != null) {
            LunchBottomSheet(
                productId = selectedItem!!.id,
                name = selectedItem!!.name,
                price = "${selectedItem!!.price} ${selectedItem!!.currency}",
                isNew = selectedItem!!.isNew,
                description = selectedItem!!.description,
                supplierName = selectedItem!!.supplier_name,
                imageBase64 = selectedItem!!.imageBase64,
                onDismiss = { showBottomSheet = false },
                onAddToCart = { productName ->
                    scope.launch {
                        snackBarHostState.showSnackbar(
                            "$productName added to cart"
                        )
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .padding(10.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            if (isLoading) {
                FullLoading()
            } else {
                LunchSearchBox()

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.onSecondaryColor)
                        .padding(10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LunchCategoryRow(
                            categories = categories,
                            onCategorySelected = { category ->
                                scope.launch {
                                    lunchProducts = fetchLunchProducts(
                                        context = context,
                                        token = token!!,
                                        categoryId = category.id
                                    )
                                }
                            }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(colors.surfaceContainerHigh, CircleShape)
                                    .clickable { showHistorySheet = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = "History",
                                    tint = colors.onBackgroundColor,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(5.dp))
                            Box(
                                modifier = Modifier
                                    .background(colors.surfaceContainerHigh, CircleShape)
                                    .clickable { openCartSheet = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Fastfood,
                                    contentDescription = "MyOrder",
                                    tint = colors.onBackgroundColor,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(8.dp)
                                )
                            }

                            MyOrderBottomSheet(
                                showSheet = openCartSheet,
                                onDismiss = { openCartSheet = false },
                                onOrderSuccess = {
                                    scope.launch {
                                        snackBarHostState.showSnackbar(successMessage)
                                    }
                                }
                            )
                            MyHistoryBottomSheet(
                                showSheet = showHistorySheet,
                                onDismiss = { showHistorySheet = false }
                            )

                        }
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 0.dp)
                    ) {
                        itemsIndexed(lunchProducts) { _, product ->
                            Column {
                                LunchCard(
                                    name = product.name,
                                    supplierName = product.supplier_name,
                                    price = "${product.price} ${product.currency}",
                                    imageBase64 = product.imageBase64,
                                    isNew = product.isNew,
                                    onClick = {
                                        selectedItem = product
                                        showBottomSheet = true
                                    }
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}