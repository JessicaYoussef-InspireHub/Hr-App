package net.inspirehub.hr.lunch.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.components.NumberOfItems
import net.inspirehub.hr.lunch.components.OrderCard
import net.inspirehub.hr.lunch.components.OrderNowButton
import net.inspirehub.hr.lunch.components.OrderSnackBar
import net.inspirehub.hr.lunch.components.TotalPrice

@Composable
fun OrderScreen(
    navController: NavController
) {
    val colors = appColors()
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }



    Box {
        Scaffold(
            containerColor = colors.onSecondaryColor,
            topBar = {
                MyAppBar(
                    "My Order",
                    onBackClick = {
                        navController.popBackStack()
                    })
            },
            bottomBar = { BottomBar(navController = navController) }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.onSecondaryColor)
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    NumberOfItems()

                    OrderCard()
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surfaceColor
                    )
                    OrderCard()
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surfaceColor
                    )
                    OrderCard()
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surfaceColor
                    )
                    OrderCard()
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surfaceColor
                    )

                    Spacer(modifier = Modifier.height(90.dp))

                    TotalPrice("Total Price", 100.0)
                    Spacer(modifier = Modifier.height(10.dp))

                    TotalPrice("Delivery", 10.0)
                    Spacer(modifier = Modifier.height(10.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.surfaceColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    TotalPrice("Total", 110.0)

                    Spacer(modifier = Modifier.height(20.dp))

                    OrderNowButton(
                        onClick = {
                            scope.launch {
                                isLoading = true

                                val result = snackBarHostState.showSnackbar(
                                    message = "Your order has been placed successfully!",
                                )

                                if (result == SnackbarResult.Dismissed || result == SnackbarResult.ActionPerformed) {
                                    isLoading = false
                                    navController.navigate("LunchScreen")
                                }
                            }
                        }
                    )
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0x88000000)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = colors.tertiaryColor)
                    }
                }


            }

        }
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            snackbar = { data -> OrderSnackBar(data) }
        )
    }
}