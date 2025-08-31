package com.example.myapplicationnewtest.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.MyAppBar
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.settings.components.AccountCard
import com.example.myapplicationnewtest.settings.components.GeneralSettingsCard
import com.example.myapplicationnewtest.settings.components.SecurityCard


@Composable
fun SettingsScreen(
    navController: NavController,
) {

    BackHandler(enabled = true) {
        navController.navigate("CheckInOutScreen") {
            popUpTo("SettingsScreen") { inclusive = true }
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MyAppBar(
            label = stringResource(R.string.settings_screen),
            navController = navController
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            GeneralSettingsCard(
                navController = navController
            )
            Spacer(modifier = Modifier.height(16.dp))

            SecurityCard(
                navController = navController
            )

            Spacer(modifier = Modifier.height(16.dp))

            AccountCard(
                navController = navController
            )
        }
    }
}