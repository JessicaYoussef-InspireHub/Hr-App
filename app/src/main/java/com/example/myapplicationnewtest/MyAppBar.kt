package com.example.myapplicationnewtest

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAppBar(
    label: String,
) {
    val colors = appColors()

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = colors.tertiaryColor,
            titleContentColor = colors.onSecondaryColor,
        ),
        title = { Text(text = label) }
    )
}