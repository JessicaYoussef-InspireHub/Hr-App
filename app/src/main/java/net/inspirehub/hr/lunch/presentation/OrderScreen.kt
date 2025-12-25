package net.inspirehub.hr.lunch.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.appColors
import net.inspirehub.hr.lunch.components.NumberOfItems
import net.inspirehub.hr.lunch.components.OrderCard

@Composable
fun OrderScreen(
    navController: NavController
){
    val colors = appColors()

    Scaffold (
        containerColor = colors.onSecondaryColor,
        topBar = { MyAppBar("My Order" , true) } ,
        bottomBar = { BottomBar( navController = navController) }
    ){
        innerPadding ->
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ){
            NumberOfItems()
            OrderCard()
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = colors.surfaceColor
            )
            OrderCard()
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                color = colors.surfaceColor
            )
            OrderCard()
        }
    }
}