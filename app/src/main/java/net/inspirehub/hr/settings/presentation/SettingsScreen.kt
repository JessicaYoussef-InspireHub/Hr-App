package net.inspirehub.hr.settings.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import net.inspirehub.hr.BottomBar
import net.inspirehub.hr.MyAppBar
import net.inspirehub.hr.R
import net.inspirehub.hr.appColors
import net.inspirehub.hr.settings.components.AccountCard
import net.inspirehub.hr.settings.components.GeneralSettingsCard
import net.inspirehub.hr.settings.components.SecurityCard


@Composable
fun SettingsScreen(
    navController: NavController,
) {
    val colors = appColors()
    BackHandler(enabled = true) {
        navController.navigate("CheckInOutScreen") {
            popUpTo("SettingsScreen") { inclusive = true }
        }
    }


    Scaffold(
        containerColor = colors.onSecondaryColor,
        topBar = {
            MyAppBar(
                label = stringResource(R.string.settings_screen),
                onBackClick = {
                    navController.popBackStack()
                }
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.onSecondaryColor)
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                GeneralSettingsCard(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SecurityCard(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                AccountCard(navController = navController)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}