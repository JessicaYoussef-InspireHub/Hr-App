package net.inspirehub.hr.lunch.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.components.CostAndTime
import net.inspirehub.hr.lunch.components.LunchBottomSheet
import net.inspirehub.hr.lunch.components.LunchCard
import net.inspirehub.hr.lunch.components.LunchCategoryRow
import net.inspirehub.hr.lunch.components.LunchSearchBox



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchScreen(
    navController: NavController,
){
    val colors = appColors()

    data class LunchItem(
        val title: String,
        val price: String,
        val imageRes: Int,
        val restaurantName: String,
        val phoneNumber: String
    )

    val lunchItems = listOf(
        LunchItem(
            title = "Grilled Chicken",
            price = "50.00",
            imageRes = R.drawable.grilled_chicken,
            restaurantName = "Chicken House",
            phoneNumber = "0123456789"
        ),
        LunchItem(
            title = "Vegetarian Pasta",
            price = "40.00",
            imageRes = R.drawable.grilled_chicken,
            restaurantName = "Green Plate",
            phoneNumber = "0109876543"
        ),
        LunchItem(
            title = "Beef Steak",
            price = "130.00",
            imageRes = R.drawable.grilled_chicken,
            restaurantName = "Steak Master",
            phoneNumber = "0112233445"
        )
    )

    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<LunchItem?>(null) }



    Scaffold (
        containerColor = colors.onSecondaryColor,
        topBar = @Composable { LunchSearchBox(
            onBackClick = {
                navController.navigate("CheckInOutScreen")
            }
        ) },
        bottomBar = { BottomBar(navController = navController) }
    ){
        innerPadding ->

        if (showBottomSheet && selectedItem != null) {
            LunchBottomSheet(
                title = selectedItem!!.title,
                price = selectedItem!!.price,
                imageRes = selectedItem!!.imageRes,
                onDismiss = { showBottomSheet = false }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ){

            Spacer(modifier = Modifier.height(40.dp))
            CostAndTime(156.5 , "12:00 Pm" , navController)
            Spacer(modifier = Modifier.height(30.dp))
            LunchCategoryRow { selected -> println("Selected: $selected") }
            Spacer(modifier = Modifier.height(15.dp))
            LazyColumn (
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {

                itemsIndexed(lunchItems) { index, item ->
                    LunchCard(
                        title = item.title,
                        price = item.price,
                        restaurant = item.restaurantName,
                        phone = item.phoneNumber,
                        imageRes = item.imageRes,
                        onClick = {
                            selectedItem = item
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }
}