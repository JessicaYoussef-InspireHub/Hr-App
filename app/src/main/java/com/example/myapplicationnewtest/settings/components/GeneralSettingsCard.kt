package com.example.myapplicationnewtest.settings.components

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplicationnewtest.R
import com.example.myapplicationnewtest.SharedPrefManager
import com.example.myapplicationnewtest.settings.data.SettingsViewModel
import com.example.myapplicationnewtest.ui.theme.LocalDarkMode
import java.util.Locale
import android.content.res.Configuration
import com.example.myapplicationnewtest.appColors


@Composable
fun GeneralSettingsCard(
    navController: NavController,
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPrefManager = remember { SharedPrefManager(context) }
    var locale by remember { mutableStateOf(Locale(sharedPrefManager.getLanguage())) }
    var expanded by remember { mutableStateOf(false) }
    val darkModeState = LocalDarkMode.current
    var showDialog by remember { mutableStateOf(false) }

    @SuppressLint("LocalContextConfigurationRead")
    fun updateLocale(newLocale: Locale) {
        locale = newLocale
        sharedPrefManager.saveLanguage(newLocale.language)

        Locale.setDefault(newLocale)
        val resources = context.resources
        val configuration = Configuration(resources.configuration)
        configuration.setLocale(newLocale)
        configuration.setLayoutDirection(newLocale)

        resources.updateConfiguration(configuration, resources.displayMetrics)

        val activity = context as Activity
        activity.recreate()
    }

    Column {
        Text(
            stringResource(R.string.general_settings),
            color = colors.tertiaryColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    BorderStroke(
                        2.dp,
                        colors.inverseOnSurface
                    ),
                    shape = RoundedCornerShape(8.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = colors.onSecondaryColor
            )
        ) {
            Column {
                SettingsItem(
                    label = stringResource(R.string.change_company),
                    icon = Icons.Default.Apartment,
                    onClick = {
                        showDialog = true
                    }
                )


                if (showDialog) {
                    ConfirmDialog(
                        message = stringResource(R.string.change_company),
                        confirmText = stringResource(R.string.are_you_sure_you_want_to_change_your_company),
                        onConfirm = {
                            showDialog = false
                            viewModel.changeCompany()
                            navController.navigate("ScanQrCodeScreen")
                            sharedPrefManager.setProtectionSkipped(false)
                        },
                        onDismiss = { showDialog = false }
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    color = colors.surfaceColor
                )

                SettingsItem(
                    label = stringResource(R.string.language),
                    icon = Icons.Default.Language,
                    onClick = { expanded = !expanded },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "expand",
                            tint = colors.surfaceColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                )

                if (expanded) {
                    Column {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 35.dp),
                            color = colors.surfaceColor
                        )
                        SettingsLanguage(
                            label = stringResource(R.string.arabic),
                            icon = painterResource(id = R.drawable.egypt),
                            onClick = {
                                if (locale.language != "ar") {
                                    updateLocale(Locale("ar"))
                                }
                                expanded = false
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 35.dp),
                            color = colors.surfaceColor
                        )

                        SettingsLanguage(
                            label = stringResource(R.string.english),
                            icon = painterResource(id = R.drawable.america),
                            onClick = {
                                if (locale.language != "en") {
                                    updateLocale(Locale("en"))
                                }
                                expanded = false
                            }
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    color = colors.surfaceColor
                )

                SettingsItem(
                    label = stringResource(R.string.dark_mode),
                    icon = Icons.Default.DarkMode,
                    onClick = {
//                        val newMode = !darkModeState.value
//                        darkModeState.value = newMode
//                        sharedPrefManager.setDarkModeEnabled(newMode)
                    },
                    trailingIcon = {
//                        CustomSwitch(
////                            checked = darkModeState.value,
//                            checked = true,
//                            onCheckedChange = {
////                                isChecked ->
////                                darkModeState.value = isChecked
////                                sharedPrefManager.setDarkModeEnabled(isChecked)
//                            },
//                        )
                    }
                )
            }
        }
    }
}