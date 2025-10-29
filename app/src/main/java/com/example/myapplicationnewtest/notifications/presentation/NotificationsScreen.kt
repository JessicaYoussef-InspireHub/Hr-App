package com.example.myapplicationnewtest.notifications.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.BottomBar
import com.example.myapplicationnewtest.MyAppBar
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.appColors


@Composable
fun NotificationsScreen(
    navController: NavController,
) {
    val colors = appColors()


    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.notification),
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

        }
    }
}