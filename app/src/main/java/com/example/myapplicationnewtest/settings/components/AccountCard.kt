package com.example.myapplicationnewtest.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.settings.data.SettingsViewModel


@Composable
fun AccountCard(
    navController: NavController,
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }


    Column {
        Text(
            stringResource(R.string.account),
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(4.dp))
        Card (
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        2.dp,
                        MaterialTheme.colorScheme.onSecondaryContainer),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary
            )
        ){
            Column {
                SettingsItem(
                    stringResource(R.string.logout),
                    icon = Icons.AutoMirrored.Filled.Logout,
                    onClick = {
                        viewModel.logout()
                        val companyId = sharedPrefManager.getCompanyId() ?: ""
                        val apiKey = sharedPrefManager.getApiKey() ?: ""
                        sharedPrefManager.setProtectionSkipped(false)
                        navController.navigate("SignInScreen/$companyId/$apiKey")
                    }
                )
            }
        }
    }
}