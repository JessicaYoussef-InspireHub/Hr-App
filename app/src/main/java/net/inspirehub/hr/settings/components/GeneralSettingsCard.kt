package net.inspirehub.hr.settings.components

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import net.inspirehub.hr.R
import net.inspirehub.hr.SharedPrefManager
import net.inspirehub.hr.settings.data.SettingsViewModel
import java.util.Locale
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import net.inspirehub.hr.MyDivider
import net.inspirehub.hr.MyDialog
import net.inspirehub.hr.appColors
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat


@Composable
fun GeneralSettingsCard(
    navController: NavController,
    viewModel: SettingsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val colors = appColors()
    val context = LocalContext.current
    val sharedPref = remember { SharedPrefManager(context) }
    var locale by remember {  mutableStateOf(Locale.forLanguageTag(sharedPref.getLanguage())) }
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isDarkMode by remember { mutableStateOf(sharedPref.isDarkModeEnabled()) }

    fun updateLocale(newLocale: Locale) {
        locale = newLocale
        sharedPref.saveLanguage(newLocale.language)

        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(newLocale.language)
        )
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
                    val darkMode = sharedPref.isDarkModeEnabled()
                    val language = sharedPref.getLanguage()
                    MyDialog(
                        onConfirm = {
                            showDialog = false
                            viewModel.changeCompany()
                            navController.navigate("ScanQrCodeScreen")
                            sharedPref.setProtectionSkipped(false)
                            sharedPref.setDarkModeEnabled(darkMode)
                            sharedPref.saveLanguage(language)
                        },
                        onDismiss = { showDialog = false },
                        title = stringResource(R.string.change_company),
                        subtitle = stringResource(R.string.are_you_sure_you_want_to_change_your_company),
                        confirmButtonText = stringResource(R.string.ok),
                        dismissButtonText = stringResource(R.string.cancel)

                    )
                }

                MyDivider(
                    horizontalPadding = 20,
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
                        MyDivider(
                            horizontalPadding = 35,
                            color = colors.surfaceColor
                        )

                        SettingsLanguage(
                            label = stringResource(R.string.arabic),
                            icon = painterResource(id = R.drawable.egypt),
                            onClick = {
                                if (locale.language != "ar") {
                                    updateLocale(Locale.forLanguageTag("ar"))
                                }
                                expanded = false
                            }
                        )
                        MyDivider(
                            horizontalPadding = 35,
                            color = colors.surfaceColor
                        )

                        SettingsLanguage(
                            label = stringResource(R.string.english),
                            icon = painterResource(id = R.drawable.america),
                            onClick = {
                                if (locale.language != "en") {
                                    updateLocale(Locale.forLanguageTag("en"))
                                }
                                expanded = false
                            }
                        )
                    }
                }

                MyDivider(
                    horizontalPadding = 20,
                    color = colors.surfaceColor
                )

                SettingsItem(
                    label = stringResource(R.string.dark_mode),
                    icon = Icons.Default.DarkMode,
                    onClick = {},
                    trailingIcon = {
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = { enabled ->
                                isDarkMode = enabled
                                sharedPref.setDarkModeEnabled(enabled)
                                (context as Activity).recreate()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.onSecondaryColor,
                                checkedTrackColor = colors.tertiaryColor,
                                uncheckedThumbColor = colors.onSecondaryColor,
                                uncheckedTrackColor = colors.onBackgroundColor,
                                uncheckedBorderColor = colors.transparent,
                                checkedBorderColor = colors.transparent
                            )
                        )
                    }
                )
            }
        }
    }
}