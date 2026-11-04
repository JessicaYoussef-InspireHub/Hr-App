package net.inspirehub.hr

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    label: String,
    onBackClick: () -> Unit,
    onMenuClick: (() -> Unit)?,
    actions: @Composable () -> Unit = {}
) {
      val colors = appColors()

//    CompositionLocalProvider(
//        LocalLayoutDirection provides LayoutDirection.Ltr
//    ) {
        CenterAlignedTopAppBar(
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = colors.tertiaryColor,
                titleContentColor = colors.onSecondaryColor,
            ),
            title = {
//                CompositionLocalProvider(
//                    LocalLayoutDirection provides LayoutDirection.Rtl
//                ) {
                    Text(text = label)
//                }
            },

            navigationIcon = {
                BackIcon(
                    onClick = {
                        onBackClick()
                    },
                    tint = colors.onSecondaryColor
                )
            },
            actions = {
                actions()

                if (onMenuClick != null) {
//                    IconButton(onClick = onMenuClick) {
//                        Icon(
//                            imageVector = Icons.Default.Menu,
//                            contentDescription = "Open menu",
//                            tint = colors.onSecondaryColor
//                        )
//                    }
                }
            }
        )
//    }
}