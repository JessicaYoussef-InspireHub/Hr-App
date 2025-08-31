package com.example.myapplicationnewtest.time_off.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationnewtest.R

@Composable
fun DialogTitle(
    onDiscard: () -> Unit
) {
    Column {
        Row(
            Modifier.fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.time_off_request),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary,
                fontSize = 20.sp,
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        onDiscard()
                    },
                tint = MaterialTheme.colorScheme.onTertiary
            )
        }
        Spacer(modifier = Modifier.size(10.dp))
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth()
                .background(MaterialTheme.colorScheme.onSecondaryContainer)
        )
    }
}